/**
   File : SmoothStreamPlayReadyDescrambleSample_jni.c

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
#define NEXPLAYERENGINE_SMOOTHSTREAMPLAYREADY_DESCRAMBLE_CALLBACK_FUNC "nexPlayerSWP_RegisterSmoothStreamPlayReadyDescrambleCallBackFunc"
#define NEXPLAYERENGINE_SMOOTHSTREAMPLAYREADY_DESCRAMBLE_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterSmoothStreamPlayReadyDescrambleCallBackFunc_Multi"
                                                        

#define  LOG_TAG    "SSPIFF_DRM_SAMPLE"

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


static unsigned long SmoothStreamPlayReadyDescrambleCallbackFunc(
											unsigned char* pInputBuffer,
											unsigned long   dwInputBufferSize, 
											unsigned char*  pOutputBuffer,
											unsigned long*  pdwOutputBufferSize,
											unsigned char*  pSampleEncBox,
											unsigned long   dwSampleEncBoxLen,
											unsigned long   dwSampleIDX,
											unsigned long   dwTrackID,
											void*           pUserData) 
{
	LOGI("[	SmoothStreamPlayReadyDescrambleCallbackFunc ] inputBuffer:%p(%lu), outputBuffer:%p, pSampleEncBox:%p(%lu), SampleIDX(%lu), TrackID(%lu), UserData:%p \n",
				pInputBuffer, dwInputBufferSize, pOutputBuffer, 
				pSampleEncBox, dwSampleEncBoxLen, dwSampleIDX, 
				dwTrackID, pUserData );
				
	if(pInputBuffer == pOutputBuffer)
	{
		*pdwOutputBufferSize = dwInputBufferSize;		
	}
	else
	{
		*pdwOutputBufferSize = dwInputBufferSize;
		memcpy(pOutputBuffer, pInputBuffer, dwInputBufferSize);	
	}	
				
	return 0;	
}								 

jint Java_com_nexstreaming_smoothstreamplayreadydescramblesample_SmoothStreamPlayReadyDRMManager_initDRMManager (JNIEnv * env,
																												 jobject clazz,
																												 jstring libName)
{
    LOGI("[initDRMManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(NEXPLAYERSmoothStreamPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData);
	
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
    fptr = (int (*)(NEXPLAYERSmoothStreamPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_SMOOTHSTREAMPLAYREADY_DESCRAMBLE_CALLBACK_FUNC);
    
    LOGI("[initDRMManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManager] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(SmoothStreamPlayReadyDescrambleCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}

jint Java_com_nexstreaming_smoothstreamplayreadydescramblesample_SmoothStreamPlayReadyDRMManager_initDRMManagerMulti (JNIEnv * env,
														   	   jobject clazz,
																												 jobject nexPlayerInstance,
														   	   jstring libName)
{
    LOGI("[initDRMManagerMulti] Start \n");
    
    void *handle = NULL;

    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERSmoothStreamPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData); 
	
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
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERSmoothStreamPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_SMOOTHSTREAMPLAYREADY_DESCRAMBLE_CALLBACK_FUNC_MULTI);
    
    LOGI("[initDRMManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManagerMulti] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, SmoothStreamPlayReadyDescrambleCallbackFunc, NULL);		

    dlclose(handle); 
   
    return 0;
}