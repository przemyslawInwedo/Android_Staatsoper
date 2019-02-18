//
//  GetKeyExtSample_jni.c
//  NexPlayerSDK_HW
//
//  Created by Lee Ian on 13. 6. 7..
//  Copyright (c) 2013년 Nexstreaming. All rights reserved.
//

#include <stdio.h>
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
#define NEXPLAYERENGINE_GETKEYEXT_CALLBACK_FUNC "nexPlayerSWP_RegisterGetKeyExtCallBackFunc"
#define NEXPLAYERENGINE_GETKEYEXT_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterGetKeyExtCallBackFunc_Multi"

#define  LOG_TAG    "GETKEYEXT_SAMPLE"

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

static int GetKeyExtCallbackFunc(char*	pKeyUrl,
										   unsigned long	dwKeyUrlLen,
										   unsigned char* pKeyBuf,
										   unsigned long dwKeyBufSize,
										   unsigned long* pdwKeySize,
										   unsigned long pUserData)
{
	LOGI("[	GetKeyExtCallbackFunc ] pKeyUrl: %s(%ld), pKeyBuf: %p(%ld),  pdwKeySize: %p\n", pKeyUrl, dwKeyUrlLen, pKeyBuf, dwKeyBufSize, pdwKeySize );
	return 0;
}

jint Java_com_nexstreaming_getkeyextsample_GetKeyExtManager_initManager (JNIEnv * env,
																		 jobject clazz,
																		 jstring libName)
{
    LOGI("[initManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(NEXPLAYERGetKeyExtCallbackFunc pCallbackFunc, void *pUserData);
	
    const char *str;
    str = (*env)->GetStringUTFChars(env, libName, NULL);
    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
		
		LOGI("[initManager] libName[%p]:%s",handle, str);
	}
    else
	{
	    /* Load Default NexPlayerEngine library */
	    handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[initManager] error=%s", dlerror());
        return -1;
    }
    
    /* Get DRM register function pointer*/
    fptr = (int (*)(NEXPLAYERGetKeyExtCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_GETKEYEXT_CALLBACK_FUNC);
    
    LOGI("[initManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManager] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(GetKeyExtCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}

jint Java_com_nexstreaming_getkeyextsample_GetKeyExtManager_initManagerMulti (JNIEnv * env,
																 jobject clazz,
																		 jobject nexPlayerInstance,
																 jstring libName)
{
    LOGI("[initManagerMulti] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERGetKeyExtCallbackFunc pCallbackFunc, void *pUserData);
	
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
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERGetKeyExtCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_GETKEYEXT_CALLBACK_FUNC_MULTI);
    
    LOGI("[initManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManagerMulti] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, GetKeyExtCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}