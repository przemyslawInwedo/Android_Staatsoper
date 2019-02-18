//
//  HLSAES128DescrambleSample_jni.c
//  NexPlayerSDK_HW
//
//  Created by Lee Ian on 13. 6. 14..
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
#define NEXPLAYERENGINE_HLSAES128DESCRAMBLE_CALLBACK_FUNC "nexPlayerSWP_RegisterHLSAES128DescrambleCallBackFunc"
#define NEXPLAYERENGINE_HLSAES128DESCRAMBLE_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterHLSAES128DescrambleCallBackFunc_Multi"


#define  LOG_TAG    "HLSAES128Descramble_SAMPLE"

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

static int HLSAES128DescrambleCallbackFunc(unsigned char*				pInBuf,
												unsigned long				dwInBufSize,
												unsigned char*				pOutBuf,
												unsigned long*				pdwOutBufSize,
												char*						pSegmentUrl,
												char*						pMpdUrl,
												char*						pKeyAttr,
												unsigned long				dwSegmentSeq,
												unsigned char*				pKey,
												unsigned long				dwKeySize,
												void*						pUserData)
{
	LOGI("[nexPLAYERHLSAES128Descramble] InputBuf(%p(%ld)), OutputBuf(%p(%ld)), segURL : %s, mpdUrl : %s, KeyAttr : %s, SegSeq : %ld\n",
		 pInBuf, dwInBufSize, pOutBuf, *pdwOutBufSize, pSegmentUrl, pMpdUrl, pKeyAttr, dwSegmentSeq);
	return 0;
}



jint Java_com_nexstreaming_hlsaes128descramblesample_HLSAES128DescrambleManager_initManager (JNIEnv * env,
																							 jobject clazz,
																							 jstring libName)
{
    LOGI("[initManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(NEXPLAYERHLSAES128DescrambleCallbackFunc pCallbackFunc, void *pUserData);
	
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
    fptr = (int (*)(NEXPLAYERHLSAES128DescrambleCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HLSAES128DESCRAMBLE_CALLBACK_FUNC);
    
    LOGI("[initManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManager] error=%s", dlerror());
    }
    
	LOGI("[initManager] Callback ptr : %p", HLSAES128DescrambleCallbackFunc);
	
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(HLSAES128DescrambleCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}

jint Java_com_nexstreaming_hlsaes128descramblesample_HLSAES128DescrambleManager_initManagerMulti (JNIEnv * env,
																								  jobject clazz,
																								  jobject nexPlayerInstance,
																		 jstring libName)
{
    LOGI("[initManagerMulti] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERHLSAES128DescrambleCallbackFunc pCallbackFunc, void *pUserData);
	
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
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERHLSAES128DescrambleCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HLSAES128DESCRAMBLE_CALLBACK_FUNC_MULTI);
    
    LOGI("[initManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManagerMulti] error=%s", dlerror());
    }
    
	LOGI("[initManagerMulti] Callback ptr : %p", HLSAES128DescrambleCallbackFunc);
	
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, HLSAES128DescrambleCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}