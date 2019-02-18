//
//  HLSIsSupportKeySample_jni.c
//
//  Created by Lee Ian on 2016. 7. 5..
//  Copyright © 2016년 Nexstreaming. All rights reserved.
//


#include "dlfcn.h"
#include "nexplayer_jni.h"


#include <jni.h>
#include <assert.h>
#include <ctype.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <android/log.h>


#define NEXPLAYERENGINE_LIB "/data/data/com.nexstreaming.nexplayersample/lib/libnexplayerengine.so"
#define NEXPLAYERENGINE_HLS_IS_SUPPORT_KEY_CALLBACK_FUNC "nexPlayerSWP_RegisterHLSIsSupportKeyCallBackFunc"
#define NEXPLAYERENGINE_HLS_IS_SUPPORT_KEY_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterHLSIsSupportKeyCallBackFunc_Multi"

#define  LOG_TAG    "HLS_IS_SUPPORT_KEY_CALLBACK_SAMPLE"

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


static int HLSIsSupportKeyCallbackFunc(char* pMpdUrl,
												 char* pKeyAttr,
												 void* pUserData)
{
	LOGI("[	HLSIsSupportKeyCallbackFunc ] pMpdUrl:%s, pKeyAttr:%s, UserData:%p \n",
		 pMpdUrl, pKeyAttr, pUserData );
	
	return 1;
}

jint Java_com_nexstreaming_hlsissupportkey_HLSISSUPPORTKEYManager_initDRMManager (JNIEnv * env,
																									 jobject clazz,
																									 jstring libName)
{
	LOGI("[initDRMManager] Start \n");
	
	void *handle = NULL;
	
	int (*fptr)(NEXPLAYERHLSIsSupportKeyCallbackFunc pHlsIsSupportKeyCallbackFunc, void *pUserData);
	
	const char *str;
	str = (*env)->GetStringUTFChars(env, libName, NULL);
	if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
		
		LOGI("[initDRMManager] libName[%p]:%s",handle, str);
	}
	else
	{
		/* Load Default NexPlayerEngine library */
		handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
	LOGI("initializeAgent : nextreaming handle=%p", handle);
	if (handle == NULL)
	{
		LOGI("[initDRMManager] error=%s", dlerror());
		return -1;
	}
	
	/* Get DRM register function pointer*/
	fptr = (int (*)(NEXPLAYERHLSIsSupportKeyCallbackFunc pHlsIsSupportKeyCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HLS_IS_SUPPORT_KEY_CALLBACK_FUNC);
	
	LOGI("[initDRMManager] fptr = %p", fptr);
	if (fptr == NULL)
	{
		LOGI("[initDRMManager] error=%s", dlerror());
	}
	
	/* Register DRM descramble function */
	if (fptr != NULL)
		(*fptr)(HLSIsSupportKeyCallbackFunc, NULL);
	
	dlclose(handle);
	
	return 0;
}

jint Java_com_nexstreaming_hlsissupportkey_HLSISSUPPORTKEYManager_initDRMManagerMulti (JNIEnv * env,
																									jobject clazz,
																									jobject nexPlayerInstance,
																									jstring libName)
{
	LOGI("[initDRMManagerMulti] Start \n");
	
	void *handle = NULL;
	
	int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERHLSIsSupportKeyCallbackFunc pHlsIsSupportKeyCallbackFunc, void *pUserData);
	
	const char *str;
	str = (*env)->GetStringUTFChars(env, libName, NULL);
	if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
		
		LOGI("[initDRMManagerMulti] libName[%p]:%s",handle, str);
	}
	else
	{
		/* Load Default NexPlayerEngine library */
		handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
	LOGI("initializeAgent : nextreaming handle=%p", handle);
	if (handle == NULL)
	{
		LOGI("[initDRMManagerMulti] error=%s", dlerror());
		return -1;
	}
	
	/* Get DRM register function pointer*/
	fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERHLSIsSupportKeyCallbackFunc pHlsIsSupportKeyCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HLS_IS_SUPPORT_KEY_CALLBACK_FUNC_MULTI);
	
	LOGI("[initDRMManagerMulti] fptr = %p", fptr);
	if (fptr == NULL)
	{
		LOGI("[initDRMManagerMulti] error=%s", dlerror());
	}
	
	/* Register DRM descramble function */
	if (fptr != NULL)
		(*fptr)((void*)nexPlayerInstance, HLSIsSupportKeyCallbackFunc, NULL);
	
	dlclose(handle);
	
	return 0;
}