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


#define NEXPLAYERENGINE_LIB "../libs/armeabi/libnexplayerengine.so"
#define NEXPLAYERENGINE_GETPDBLOCK_CALLBACK_FUNC "nexPlayerSWP_RegisterGetPDBlockCallBackFunc"
#define NEXPLAYERENGINE_GETPDBLOCK_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterGetPDBlockCallBackFunc_Multi"
                                                        

#define  LOG_TAG    "GETPDBLOCK_SAMPLE"

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


static int GetPDBlockCallbackFunc(char* 			pBlockBuf,
											long long 		ulOffset,
											int 				uiBlockSize,
											void*			pUserData)
{
	LOGI("[GetPDBlockCallbackFunc ] block :%p Offset %lld Size : %d  UserData: %p\n", pBlockBuf,ulOffset, uiBlockSize, pUserData );
	return 0;	
}								 

jint Java_com_nexstreaming_getpdblocksample_GetPDBlockManager_initManager (JNIEnv * env,
																		   jobject clazz,
																		   jstring libName)
{
    LOGI("[initManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(NEXPLAYERGetPDBlockCallbackFunc pCallbackFunc, void *pUserData);
	
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
    fptr = (int (*)(NEXPLAYERGetPDBlockCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_GETPDBLOCK_CALLBACK_FUNC);
    
    LOGI("[initDRMManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManager] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(GetPDBlockCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}

jint Java_com_nexstreaming_getpdblocksample_GetPDBlockManager_initManagerMulti (JNIEnv * env,
														   	   jobject clazz,
																		   jobject nexPlayerInstance,
														   	   jstring libName)
{
    LOGI("[initManagerMulti] Start \n");
    
    void *handle = NULL;

    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERGetPDBlockCallbackFunc pCallbackFunc, void *pUserData); 
	
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
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERGetPDBlockCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_GETPDBLOCK_CALLBACK_FUNC_MULTI);
    
    LOGI("[initManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManagerMulti] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, GetPDBlockCallbackFunc, NULL);

    dlclose(handle); 
   
    return 0;
}
