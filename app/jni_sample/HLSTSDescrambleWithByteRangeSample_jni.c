/**
   File : HLSTsDescrambleSample_jni.c

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


#define NEXPLAYERENGINE_LIB "/data/data/com.nexstreaming.nexplayersample/lib/libnexplayerengine.so"
#define NEXPLAYERENGINE_HLSTS_DESCRAMBLE_WITHBYTERANGE_CALLBACK_FUNC "nexPlayerSWP_RegisterHLSTSDescrambleWithByteRangeCallBackFunc"
#define NEXPLAYERENGINE_HLSTS_DESCRAMBLE_WITHBYTERANGE_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterHLSTSDescrambleWithByteRangeCallBackFunc_Multi"

#define  LOG_TAG    "HLSTS_DESCRAMBLE_BYTERANGE_DRM_SAMPLE"

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

static int HLSTSDescrambleWithByteRangeCallbackFunc(unsigned char*	pInputBuffer,
															   unsigned int			uiInputBufferSize,
															   unsigned char*		pOutputBuffer,
															   unsigned int*			puiOutputBufferSize,
															   char*           			pMediaFileURL,
															   long long			qByteRangeOffset,
															   long long			qByteRangeLength,
															   char*           			pPlaylistURL,
															   void*				pUserData)
{
	LOGI("[	HLSTSDescrambleWithByteRangeCallbackFunc ] inputBuffer[%d]:%p, outputBuffer;%p, Byterange[%lld, %lld] UserData:%p \n",
				uiInputBufferSize, pInputBuffer, pOutputBuffer, qByteRangeOffset, qByteRangeLength, pUserData );
				
	if(pInputBuffer == pOutputBuffer)
	{
		*puiOutputBufferSize = uiInputBufferSize;
	}
	else
	{
		*puiOutputBufferSize = uiInputBufferSize;
		memcpy(pOutputBuffer, pInputBuffer, uiInputBufferSize);
	}
	
	return 0;	
}								 

jint Java_com_nexstreaming_hlstsdescramblewithbyterange_HLSTSWITHBYTERANGEDRMManager_initDRMManager (JNIEnv * env,
																				 jobject clazz,
																				 jstring libName)
{
    LOGI("[initDRMManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(NEXPLAYERHLSTSDescrambleWithByteRangeCallbackFunc pDescrambleCallbackFunc, void *pUserData);
	
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
    fptr = (int (*)(NEXPLAYERHLSTSDescrambleWithByteRangeCallbackFunc pDescrambleCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HLSTS_DESCRAMBLE_WITHBYTERANGE_CALLBACK_FUNC);
    
    LOGI("[initDRMManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManager] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(HLSTSDescrambleWithByteRangeCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}

jint Java_com_nexstreaming_hlsdecryptsegmentsample_HLSDecryptSegmentDRMManager_initDRMManagerMulti (JNIEnv * env,
														   	   jobject clazz,
																				 jobject nexPlayerInstance,
														   	   jstring libName)
{
    LOGI("[initDRMManagerMulti] Start \n");
    
    void *handle = NULL;

    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERHLSTSDescrambleWithByteRangeCallbackFunc pDescrambleCallbackFunc, void *pUserData);
	
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
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERHLSTSDescrambleWithByteRangeCallbackFunc pDescrambleCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HLSTS_DESCRAMBLE_WITHBYTERANGE_CALLBACK_FUNC_MULTI);
    
    LOGI("[initDRMManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManagerMulti] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, HLSTSDescrambleWithByteRangeCallbackFunc, NULL);

    dlclose(handle); 
   
    return 0;
}