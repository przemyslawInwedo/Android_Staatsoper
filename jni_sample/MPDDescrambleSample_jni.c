/**
   File : GetAESKeyFuncSample_jni.c
   Copyright (c) 2010 Nextreaming Inc, all rights reserved.
 */


#include "dlfcn.h"
#include "nexplayer_jni.h"


#include <jni.h>
#include <assert.h>
#include <ctype.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <android/log.h>


#define NEXPLAYERENGINE_LIB "../libs/armeabi/libnexplayerengine.so"
#define NEXPLAYERENGINE_MPDDESCRAMBLE_CALLBACK_FUNC "nexPlayerSWP_RegisterMPDDescrambleCallbackFunc"
#define NEXPLAYERENGINE_MPDDESCRAMBLE_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterMPDDescrambleCallbackFunc_Multi"
                                                        

#define  LOG_TAG    "MPDDESCRAMBLE_SAMPLE"

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

static int MPDDescrambleCallbackFunc(
    												char*				pMpdUrl,		// [in] Original Url of the top-level Mpd. (In case of redirection, this will be the url before redirection.)
												unsigned long				dwMpdUrlLen,	// [in]
												char*						pMpd,			// [in/out] Top-level MPD.( manifest, playlist)
												unsigned long				dwMpdLen,		// [in] MPD size.
												unsigned long*				pdwNewMpdLen,	// [out] The size of decrypted MPD.
												void*					pUserData)
{

	unsigned int uiLen = 0;
	LOGI("[MPDDescrambleCallbackFunc] (URL :(len : %ld) %s\n, In : %p, %ld  OutLen : %ld\n", dwMpdUrlLen, pMpdUrl, pMpd, dwMpdLen, *pdwNewMpdLen);
	*pdwNewMpdLen = dwMpdLen;

	return 1;               // 0 means that the credential is set correctly. All the other return value except 0 and 1 will be treated as an error.
}								 

jint Java_com_nexstreaming_mpddescramblesample_MPDDescrambleManager_initManager (JNIEnv * env,
																				 jobject clazz,
																				 jstring libName)
{
    LOGI("[MPDDescrambleManager initManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(NEXPLAYERMPDDescrambleCallbackFunc pMPDDescrambleFunc, void *pUserData);
	
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
    fptr = (int (*)(NEXPLAYERMPDDescrambleCallbackFunc pMPDDescrambleFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_MPDDESCRAMBLE_CALLBACK_FUNC);
    
    LOGI("[initManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManager] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(MPDDescrambleCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}
	
jint Java_com_nexstreaming_mpddescramblesample_MPDDescrambleManager_initManagerMulti (JNIEnv * env,
														   	   jobject clazz,
																				 jobject nexPlayerInstance,
														   	   jstring libName)
{
    LOGI("[MPDDescrambleManager initManagerMulti] Start \n");
    
    void *handle = NULL;

    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERMPDDescrambleCallbackFunc pMPDDescrambleFunc, void *pUserData); 
	
    const char *str;
    str = (*env)->GetStringUTFChars(env, libName, NULL);
    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);  
		
		LOGI("[initManagerMulti] libName[%p]:%s",handle, str);
	}
    else
	{
	    /* Load Default NexPlayerEngine library */
	    handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}

	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[initManagerMulti] error=%s", dlerror());
        return -1;
    }
   
    /* Get DRM register function pointer*/   
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERMPDDescrambleCallbackFunc pMPDDescrambleFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_MPDDESCRAMBLE_CALLBACK_FUNC_MULTI);
    
    LOGI("[initManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManagerMulti] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, MPDDescrambleCallbackFunc, NULL);

    dlclose(handle); 
   
    return 0;
}
