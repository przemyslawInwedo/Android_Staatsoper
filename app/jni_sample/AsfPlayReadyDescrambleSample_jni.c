/**
   File : AsfPlayReadyDescrambleSample_jni.c
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
#define NEXPLAYERENGINE_ASFPLAYREADY_DESCRAMBLE_CALLBACK_FUNC "nexPlayerSWP_RegisterAsfPlayReadyDescrambleCallBackFunc"
#define NEXPLAYERENGINE_ASFPLAYREADY_DESCRAMBLE_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterAsfPlayReadyDescrambleCallBackFunc_Multi"
                                                        

#define  LOG_TAG    "ASFPLAYREADY_DRM_SAMPLE"

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

static int AsfPlayReadyDescrambleCallbackFunc(
											unsigned char* 	pInputBuffer,
											unsigned long		dwInputBufferSize, 
											unsigned char*	pOutputBuffer,
											unsigned long*	pdwOutputBufferSize,
											unsigned char*	pIVBuffer,
											unsigned long		dwIVBufferSize,
											void*			pUserData)

{
	LOGI("[	AsfPlayReadyDescrambleCallbackFunc ] inputBuffer:%p(%ld), outputBuffer:%p,  IVBuffer:%p(%ld), userData(%p)\n",
				pInputBuffer, dwInputBufferSize, pOutputBuffer, 
				pIVBuffer, dwIVBufferSize, pUserData );

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

jint Java_com_nexstreaming_asfplayreadydescramblesample_AsfPlayReadyDRMManager_initDRMManager (JNIEnv * env,
																							   jobject clazz,
																							   jstring libName)
{
    LOGI("[initDRMManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(NEXPLAYERAsfPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc, unsigned long ulDescrambleType, void *pUserData);
	
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
    fptr = (int (*)(NEXPLAYERAsfPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc, unsigned long ulDescrambleType, void *pUserData))dlsym(handle, NEXPLAYERENGINE_ASFPLAYREADY_DESCRAMBLE_CALLBACK_FUNC);
    
    LOGI("[initDRMManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManager] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(AsfPlayReadyDescrambleCallbackFunc,NXWMDRM_PACKET_TYPE, NULL);
	
    dlclose(handle);
	
    return 0;
}

jint Java_com_nexstreaming_asfplayreadydescramblesample_AsfPlayReadyDRMManager_initDRMManagerMulti (JNIEnv * env,
														   	   jobject clazz,
																jobject nexPlayerInstance,
														   	   jstring libName)
{
    LOGI("[initDRMManagerMulti] Start \n");
    
    void *handle = NULL;

    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERAsfPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc, unsigned long ulDescrambleType, void *pUserData); 
	
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
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERAsfPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc, unsigned long ulDescrambleType, void *pUserData))dlsym(handle, NEXPLAYERENGINE_ASFPLAYREADY_DESCRAMBLE_CALLBACK_FUNC_MULTI);
    
    LOGI("[initDRMManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManagerMulti] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, AsfPlayReadyDescrambleCallbackFunc,NXWMDRM_PACKET_TYPE, NULL);

    dlclose(handle); 
   
    return 0;
}
