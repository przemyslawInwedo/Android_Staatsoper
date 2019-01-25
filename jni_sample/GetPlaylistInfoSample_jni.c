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
#define NEXPLAYERENGINE_GETPLAYLISTINFO_CALLBACK_FUNC "nexPlayerSWP_RegisterGetPlaylistInfoCallBackFunc"
#define NEXPLAYERENGINE_GETPLAYLISTINFO_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterGetPlaylistInfoCallBackFunc_Multi"
                                                        

#define  LOG_TAG    "GETPLAYLISTINFO_SAMPLE"

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


static unsigned int GetPlaylistGetInfoCallbackFunc(		char* 		pUrl,
														char* 		pPlaylist,
														unsigned int 	uiPlaylistSize,
														void*		pUserData)
{
	LOGI("[GetPlaylistGetInfoCallbackFunc ] URL :%s Size : %d\n %s\n", 	pUrl, uiPlaylistSize, pPlaylist );
	return 0;	
}								 


jint Java_com_nexstreaming_getplaylistinfosample_GetPlaylistInfoManager_initManager (JNIEnv * env,
																					 jobject clazz,
																					 jstring libName)
{
    LOGI("[initManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(NEXPLAYERGetPlaylistInfoCallbackFunc pCallbackFunc, void *pUserData);
	
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
    fptr = (int (*)(NEXPLAYERGetPlaylistInfoCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_GETPLAYLISTINFO_CALLBACK_FUNC);
    
    LOGI("[initDRMManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManager] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(GetPlaylistGetInfoCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}


jint Java_com_nexstreaming_getplaylistinfosample_GetPlaylistInfoManager_initManagerMulti (JNIEnv * env,
														   	   jobject clazz,
																					 jobject nexPlayerInstance,
														   	   jstring libName)
{
    LOGI("[initManagerMulti] Start \n");
    
    void *handle = NULL;

    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERGetPlaylistInfoCallbackFunc pCallbackFunc, void *pUserData); 
	
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
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERGetPlaylistInfoCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_GETPLAYLISTINFO_CALLBACK_FUNC);
    
    LOGI("[initManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManagerMulti] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, GetPlaylistGetInfoCallbackFunc, NULL);

    dlclose(handle); 
   
    return 0;
}
