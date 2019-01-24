//
//  HLSAESCallbacks.cpp
//  NexPlayerSDK_HW
//
//  Created by Lee Ian on 10/11/2016.
//  Copyright © 2016 Nexstreaming. All rights reserved.
//

#include <jni.h>
#include <android/log.h>
#include "dlfcn.h"

#include "nexplayer_Callback.h"
#include "NexHLSDRMManager.h"

#include <stdlib.h>

#define NEXPLAYERENGINE_LIB "/data/data/com.nexstreaming.nexplayersample/lib/libnexplayerengine.so"

#define  LOG_TAG    "NexHLSAES128Callbacks"
#define  DEFAULT_KEY_ATTR_LENGTH 1024

#ifndef NOLOG
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGV(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#else
#define  LOGI(...)
#define  LOGE(...)
#define  LOGW(...)
#define  LOGV(...)
#endif


static JavaVM *g_VM;

static struct {
	jclass NexHLSAES128CBClass;
	
	jmethodID issup_callback;
	jmethodID getkey_callback;
}gCachedID;

static JNIEnv * getJNIEnv(bool* needsDetach)
{
	if ( !g_VM) {
		LOGE("I_JNI: jni_init_graphics No JNI VM available.\n");
		return NULL;
	}
	
	*needsDetach = false;
	JNIEnv* env = NULL;
	
	g_VM->GetEnv((void**)&env,JNI_VERSION_1_4);
	if (env == NULL) {
		g_VM->AttachCurrentThread ((JNIEnv **)&env, NULL);
		*needsDetach = true;
	}
	return env;
}

int HLSIsSupportKey(char* pMpdUrl, char* pKeyAttr, void* pUserData)
{
	int iRet = NOT_SUPPORTING;
	bool needDetatch = false;
	JNIEnv* env = getJNIEnv(&needDetatch);
	
	jstring strMpdURL = env->NewStringUTF(pMpdUrl);
	jstring strKeyAttr = env->NewStringUTF(pKeyAttr);
	jobject weak_thiz = (jobject)pUserData;
	
	jboolean bRet = env->CallBooleanMethod(weak_thiz, gCachedID.issup_callback, strKeyAttr, strMpdURL);
	if(bRet == true)
	{
		iRet = SUPPORTING;
	}
	if(strMpdURL)
		env->DeleteLocalRef(strMpdURL);
	if(strKeyAttr)
		env->DeleteLocalRef(strKeyAttr);

	if(needDetatch)
	{
		g_VM->DetachCurrentThread();
	}
	return iRet;
}

#define GETKEYEXT_SUCCESS				0	// Success. (Key is copied successfully.)
#define GETKEYEXT_NEEDMOREBUF			1	// The key buffer is not enough to copy the key. // If the callback returns this value, the key size must be assigned at *pdwKeySize.
#define GETKEYEXT_ERROR					2	// Error.
#define GETKEYEXT_NO_EFFECT				3	// Work as if this callback is not registered. (nxProtocol will download the key.)
#define GETKEYEXT_NO_KEY_DOWN			4	// nxProtocol will not download the key.

unsigned char* pTempSTR = NULL;

int GetKeyExt(char*	pKeyUrl,
			  unsigned long	dwKeyUrlLen,
			  unsigned char* pKeyBuf,
			  unsigned long dwKeyBufSize,
			  unsigned long* pdwKeySize,
			  unsigned long pUserData)
{
	int ret = GETKEYEXT_ERROR;
	bool needDetatch = false;
	
	if(pTempSTR)
	{
		LOGI("Reuse temporal buffer space, %p len(%ld)", pTempSTR, dwKeyBufSize);
		memcpy(pKeyBuf, pTempSTR, dwKeyBufSize);
		free(pTempSTR);
		pTempSTR = NULL;
		return GETKEYEXT_SUCCESS;
	}
	
	JNIEnv* env = getJNIEnv(&needDetatch);
	jobject weak_thiz = (jobject)pUserData;
	jstring strKeyUrl = env->NewStringUTF(pKeyUrl);
	
	jbyteArray baRet = (jbyteArray)env->CallObjectMethod(weak_thiz, gCachedID.getkey_callback, strKeyUrl);
	
	int len = env->GetArrayLength(baRet);
	LOGI("Bytearray(%p) length %d", baRet, len);
	if(len)
	{
		unsigned char* pKeyDecodedBuffer = (unsigned char*)malloc(len);
		memset(pKeyDecodedBuffer, 0, len);
		env->GetByteArrayRegion(baRet, 0, len, reinterpret_cast<jbyte*>(pKeyDecodedBuffer));
		if(len > 0 && len <= (int)dwKeyBufSize)
		{
			memset(pKeyBuf, 0, dwKeyBufSize);
			memcpy(pKeyBuf, pKeyDecodedBuffer, len);
			*pdwKeySize = len;
			ret = GETKEYEXT_SUCCESS;
		}
		else if(len > 0 && len > (int)dwKeyBufSize)
		{
			LOGI("pKeyBuf is not enough to copy'em. Use temporal space(%p), len(%d)", pTempSTR, len);
			if(pTempSTR == NULL)
			{
				pTempSTR = (unsigned char*)malloc(len);
				memset(pTempSTR, 0, len);
			}
			memcpy(pTempSTR, pKeyDecodedBuffer, len);
			*pdwKeySize = len;
			ret = GETKEYEXT_NEEDMOREBUF;
		}
		env->DeleteLocalRef(baRet);
		free(pKeyDecodedBuffer);
	}
	if(strKeyUrl)
		env->DeleteLocalRef(strKeyUrl);
	return ret;
}


int _registerCallbacks(JNIEnv * env, jstring libName, jobject nexPlayerInstance, jobject weak_thiz)
{
	void *handle = NULL;
	const char *strLibName = NULL;
	
	strLibName = env->GetStringUTFChars(libName, NULL);
	if(strLibName != NULL)
	{
		handle = dlopen(strLibName, RTLD_LAZY);
		
		LOGI("[_registerCallbacks] libName[%p]:%s",handle, strLibName);
	}
	else
	{
		/* Load Default NexPlayerEngine library */
		handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
	LOGI("initializeAgent : nextreaming handle=%p", handle);
	if (handle == NULL)
	{
		LOGE("[_registerCallbacks] error=%s", dlerror());
		env->ReleaseStringUTFChars(libName, strLibName);
		return -1;
	}
	
	if(nexPlayerInstance)
	{
		int (*fptr_AddCallback_multi)(int what, void* pFunc, void* pNexPlayerInstance, void* pUserData);
		
		fptr_AddCallback_multi = (int (*)(int what, void* pFunc, void* pNexPlayerInstance, void* pUserData))dlsym(handle, "NexHLSDRM_AddCallback_Multi");
		LOGI("[_registerCallbacks Static] fptr = %p", fptr_AddCallback_multi);
		
		(*fptr_AddCallback_multi)(IS_SUPPORT_KEY_EXT_KEY, (void*)HLSIsSupportKey, (void*)nexPlayerInstance, (void*)weak_thiz);
		(*fptr_AddCallback_multi)(GET_KEY_EXT, (void*)GetKeyExt,(void*)nexPlayerInstance, (void*)weak_thiz);
	}
	else
	{
		int (*fptr_AddCallback)(int what, void* pFunc, void* pUserData);
		
		fptr_AddCallback = (int (*)(int what, void* pFunc, void* pUserData))dlsym(handle, "NexHLSDRM_AddCallback");
		LOGI("[_registerCallbacks Static] fptr = %p", fptr_AddCallback);
		
		(*fptr_AddCallback)(IS_SUPPORT_KEY_EXT_KEY, (void*)HLSIsSupportKey, (void*)weak_thiz);
		(*fptr_AddCallback)(GET_KEY_EXT, (void*)GetKeyExt, (void*)weak_thiz);
	}
	return 0;
}

int initDRMManager (JNIEnv * env,
					jobject clazz,
					jstring libName)
{
	LOGI("[initDRMManager] Start \n");
	int ret = 1;
	jobject weak_thiz = env->NewGlobalRef(clazz);
	ret = _registerCallbacks(env, libName, NULL, weak_thiz);
	return ret;
}

int initDRMManagerMulti (JNIEnv * env,
						 jobject clazz,
						 jobject nexPlayerInstance,
						 jstring libName)
{
	LOGI("[initDRMManagerMulti] Start \n");
	int ret = 1;
	jobject weak_thiz = env->NewGlobalRef(clazz);
	ret = _registerCallbacks(env, libName, nexPlayerInstance, weak_thiz);
	return ret;
}

JNINativeMethod gMethods[] = {
	{"initDRMManagerMulti", "(Ljava/lang/Object;Ljava/lang/String;)I", (void *) initDRMManagerMulti },
	{"initDRMManager", "(Ljava/lang/String;)I", (void *) initDRMManager }
};

int registerMethods(JNIEnv* env) {
	static const char* const kClassName =
	"com/nexstreaming/hlsaes128getkeysample/NexHLSAES128DRMManager";
	jclass clazz;
	
	/* look up the class */
	clazz = env->FindClass(kClassName);
	if (clazz == NULL) {
		LOGE("Can't find class %s\n", kClassName);
		return 1;
	}
	
	/* register all the methods */
	if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods)
							 / sizeof(gMethods[0])) != JNI_OK) {
		LOGE("Failed registering methods for %s\n", kClassName);
		return 1;
	}
	
	gCachedID.NexHLSAES128CBClass = (jclass)env->NewGlobalRef(clazz);
	
	gCachedID.issup_callback = env->GetMethodID(clazz, "isSupportKeyAttr", "(Ljava/lang/String;Ljava/lang/String;)Z");
	if(gCachedID.issup_callback == NULL)
	{
		LOGE("Cannot find callback!");
		return 1;
	}
	
	gCachedID.getkey_callback = env->GetMethodID(clazz, "getKeyFromExternal", "(Ljava/lang/String;)[B");
	if(gCachedID.getkey_callback == NULL)
	{
		LOGE("Cannot find callback!");
		return 1;
	}
	
	/* fill out the rest of the ID cache */
	return 0;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	jint result = -1;
	
	g_VM = vm;
	
	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("ERROR: GetEnv failed\n");
		goto bail;
	}
	//assert(env != NULL);
	
	if (registerMethods(env) != 0) {
		LOGE("ERROR: WideVine_SampleEncJNI native registration failed\n");
		goto bail;
	}
	
	/* success -- return valid version number */
	result = JNI_VERSION_1_4;
	
bail: return result;
}

