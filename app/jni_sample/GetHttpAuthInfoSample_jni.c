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
#define NEXPLAYERENGINE_GETHTTPAUTHINFO_CALLBACK_FUNC "nexPlayerSWP_RegisterGetHttpAuthInfoCallbackFunc"
#define NEXPLAYERENGINE_GETHTTPAUTHINFO_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterGetHttpAuthInfoCallbackFunc_Multi"
                                                        

#define  LOG_TAG    "GETHTTPAUTHINFO_SAMPLE"

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

#define HTTPAUTHINFO_STRING "id: test1\r\npw: 12345\r\n"

static int GetHttpAuthInfoCallbackFunc(
    									unsigned long 		ulStatusCode, 		// [in] HTTP Status Code. (For convenience.)
									char* 			pResponse,			// [in] HTTP Response.
									unsigned long		ulResponseSize, 		// [in] Size of the pResponse.
									char*			pAuthInfo,			// [out] Copy the auth info here. 
																		// THIS MUST BE a NULL-terminated string.
																		// Each line MUST BE a well-formed HTTP line, that is each line MUST BE ended with single "\r\n".
																		// ex) "Authorization: AAAAA\r\n"
									unsigned long		ulAuthInfoBufSize,	// [out] The size of pAuthInfo in bytes.
									unsigned long*	pulNewBufSize,		// [out] If the buffer is not enough to copy the auth info, then set the new size here, and return 1.
									void*			pUserData)

{
	unsigned int uiLen = 0;

	LOGI("[	GetHttpAuthInfoCallbackFunc ]\n");

	uiLen = strlen(HTTPAUTHINFO_STRING);

	if (ulAuthInfoBufSize < uiLen + 1)
	{
	          *pulNewBufSize = uiLen + 1;
	          return 1;                                          // 1 means that the buffer is not enough. And if 1 is returned, then this callback will be called again with dwBufSize as same value with *pdwNewBufSize.
	}

	strcpy(pAuthInfo, HTTPAUTHINFO_STRING);	// Copy credentials to pBuf. Do be careful that the credential MUST BE a NULL terminated string, and each line MUST BE ended with "\r\n".
	return 0;               // 0 means that the credential is set correctly. All the other return value except 0 and 1 will be treated as an error.
}

jint Java_com_nexstreaming_gethttpauthinfosample_GetHttpAuthInfoManager_initManager (JNIEnv * env,
																					 jobject clazz,
																					 jstring libName)
{
    LOGI("[GetHttpAuthInfoManager initManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(NEXPLAYERGetHttpAuthInfoCallbackFunc pGetHttpAuthInfoFunc, void *pUserData);
	
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
    fptr = (int (*)(NEXPLAYERGetHttpAuthInfoCallbackFunc pGetHttpAuthInfoFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_GETHTTPAUTHINFO_CALLBACK_FUNC);
    
    LOGI("[initManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManager] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(GetHttpAuthInfoCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}
	
jint Java_com_nexstreaming_gethttpauthinfosample_GetHttpAuthInfoManager_initManagerMulti (JNIEnv * env,
														   	   jobject clazz,
																					 jobject nexPlayerInstance,
														   	   jstring libName)
{
    LOGI("[GetHttpAuthInfoManager initManagerMulti] Start \n");
    
    void *handle = NULL;

    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERGetHttpAuthInfoCallbackFunc pGetHttpAuthInfoFunc, void *pUserData); 
	
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
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERGetHttpAuthInfoCallbackFunc pGetHttpAuthInfoFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_GETHTTPAUTHINFO_CALLBACK_FUNC_MULTI);
    
    LOGI("[initManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManagerMulti] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, GetHttpAuthInfoCallbackFunc, NULL);

    dlclose(handle); 
   
    return 0;
}
