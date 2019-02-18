/**
   File : PDEnvelopHeaderParsingSample_jni.c

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
#define NEXPLAYERENGINE_PDENVELOPHEADERPARSING_CALLBACK_FUNC "nexPlayerSWP_RegisterPDEnvelopHeaderParsingCallBackFunc"
#define NEXPLAYERENGINE_PDENVELOPHEADERPARSING_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterPDEnvelopHeaderParsingCallBackFunc_Multi"
                                                        

#define  LOG_TAG    "PDENVELOPHEADERPARSING_SAMPLE"

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


static int PDEnvelopHeaderParsingCallbackFunc(char* pData,
											  long long qOffset,
											  int iDataSize,
											  unsigned int* puContentOffset,
											  void* pUserData)
{
	LOGI("[PDEnvelopHeaderParsingCallbackFunc] pData(0x%p(%d)) offset(%lld)\n", pData, iDataSize, qOffset);
	return 0;
}

jint Java_com_nexstreaming_pdenvelopheaderparsingsample_PDEnvelopHeaderParsingManager_initManager(JNIEnv * env, jobject clazz, jstring libName)
{
    LOGI("[initManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(NEXPLAYERPDEnvelopHeaderParsingCallbackFunc pInitCallbackFunc, void *pUserData);
	
    const char *str;
    str = (*env)->GetStringUTFChars(env, libName, NULL);
    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
		
		LOGI("[initDRMManager] libName[%p]:%s", handle, str);
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
    fptr = (int (*)(NEXPLAYERPDEnvelopHeaderParsingCallbackFunc pInitCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_PDENVELOPHEADERPARSING_CALLBACK_FUNC);

    if (fptr)
    {
		LOGI("[initDRMManager] fptr = %p", fptr);
		/* Register DRM descramble function */
		(*fptr)(PDEnvelopHeaderParsingCallbackFunc, NULL);
    }
	else
	{
		LOGI("[initDRMManager] error=%s", dlerror());
	}
	
    dlclose(handle);
	
    return 0;
}

jint Java_com_nexstreaming_pdenvelopheaderparsingsample_PDEnvelopHeaderParsingManager_initManagerMulti(JNIEnv * env, jobject clazz, jobject nexPlayerInstance, jstring libName)
{
    LOGI("[initManagerMulti] Start \n");
    
    void *handle = NULL;

    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERPDEnvelopHeaderParsingCallbackFunc pInitCallbackFunc, void *pUserData); 
	
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
	fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERPDEnvelopHeaderParsingCallbackFunc pInitCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_PDENVELOPHEADERPARSING_CALLBACK_FUNC_MULTI);
    
    LOGI("[initManagerMulti] fptr = %p", fptr);
    if (fptr)
    {
		/* Register DRM descramble function */
        (*fptr)((void*)nexPlayerInstance, PDEnvelopHeaderParsingCallbackFunc, NULL);
    }
	else
	{
		LOGI("[initManagerMulti] error=%s", dlerror());
	}

    dlclose(handle); 
   
    return 0;
}
