/**
   File : drmSample_jni.c

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


#define		_SUPPORT_WMDRM_

#define NEXPLAYERENGINE_LIB "../libs/armeabi/libnexplayerengine.so"
#define NEXPLAYERENGINE_DESCRAMBLE_CALLBACK_FUNC "nexPlayerSWP_RegisterDRMDescrambleCallBackFunc"
#define	NEXPLAYERENGINE_WMDRMDESCRAMBLE_CALLBACK_FUNC  "nexPlayerSWP_RegisterWMDRMDescrambleCallBackFunc"
#define NEXPLAYERENGINE_DESCRAMBLE_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterDRMDescrambleCallBackFunc_Multi"
#define	NEXPLAYERENGINE_WMDRMDESCRAMBLE_CALLBACK_FUNC_MULTI  "nexPlayerSWP_RegisterWMDRMDescrambleCallBackFunc_Multi"

#define  LOG_TAG    "DRM_SAMPLE"

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

#ifndef _SUPPORT_WMDRM_
static int drmDescrambleCallbackFunc ( 	unsigned int	uiType,				// 0:Video, 1:Audio
										 unsigned char*	pInputBuffer,
										 unsigned int	uiInputBufferSize,
										 unsigned char*	pOutputBuffer,
										 unsigned int*	puiOutputBufferSize,
										 void *			pUserData)
{
	LOGI("[	drmDescrambleCallbackFunc ] type:%d, inputBuffer[%d]:%p, outputBuffer;%p, UserData:%p \n",
				uiType, uiInputBufferSize, pInputBuffer, pOutputBuffer, pUserData );
				
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
#else

static int wmdrmDescrambleCallbackFunc(  unsigned char*	pInputBuffer,
									   unsigned int	uiInputBufferSize,
									   unsigned char*	pOutputBuffer,
									   unsigned int*	puiOutputBufferSize,
									   unsigned char*   pIVBuffer,
									   unsigned long    dwIVBufferSize,
									   void *			pUserData)
{
	LOGI("[	wmdrmDescrambleCallbackFunc ] inputBuffer[%d]:%p, outputBuffer;%p, UserData:%p \n",
				uiInputBufferSize, pInputBuffer, pOutputBuffer, pUserData );
				
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
#endif

jint Java_com_nexstreaming_drmsample_DRMManager_initDRMManager (JNIEnv * env,
																jobject clazz,
																jstring libName)
{
    LOGI("[initDRMManager] Start \n");
    
    void *handle = NULL;
	
#ifndef _SUPPORT_WMDRM_
    int (*fptr)(NEXPLAYERDRMDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData);
#else
	int (*fptr)(unsigned int uiDRMType, NEXPLAYERWMDRMDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData);
#endif	// _SUPPORT_WMDRM_
	
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
    
#ifndef _SUPPORT_WMDRM_
    /* Get DRM register function pointer*/
    
    fptr = (int (*)(NEXPLAYERDRMDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_DESCRAMBLE_CALLBACK_FUNC);
    
    LOGI("[initDRMManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManager] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(drmDescrambleCallbackFunc, NULL);
	
    dlclose(handle);
#else	// _SUPPORT_WMDRM_
	/* Get WMDRM register function pointer*/
    fptr = (int (*)(unsigned int uiDRMType, NEXPLAYERWMDRMDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_WMDRMDESCRAMBLE_CALLBACK_FUNC);
	
    LOGI("[initDRMManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManager] error=%s", dlerror());
    }
    
    /* Register WMDRM descramble function */
    if (fptr != NULL)
        (*fptr)( /*NXWMDRM_PAYLOAD_TYPE */ NXWMDRM_PACKET_TYPE, wmdrmDescrambleCallbackFunc, NULL);
	
    dlclose(handle);
#endif	// _SUPPORT_WMDRM_
	
    return 0;
}

jint Java_com_nexstreaming_drmsample_DRMManager_initDRMManagerMulti (JNIEnv * env,
														   	   jobject clazz,
																jobject nexPlayerInstance,
														   	   jstring libName)
{
    LOGI("[initDRMManagerMulti] Start \n");
    
    void *handle = NULL;

#ifndef _SUPPORT_WMDRM_    
    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERDRMDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData);
#else
	int (*fptr)(void* nexPlayerClassInstance, unsigned int uiDRMType, NEXPLAYERWMDRMDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData); 
#endif	// _SUPPORT_WMDRM_
	
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
    
#ifndef _SUPPORT_WMDRM_
    /* Get DRM register function pointer*/
    
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERDRMDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_DESCRAMBLE_CALLBACK_FUNC_MULTI);
    
    LOGI("[initDRMManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManagerMulti] error=%s", dlerror());
    }
    
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, drmDescrambleCallbackFunc, NULL);

    dlclose(handle); 
#else	// _SUPPORT_WMDRM_
	/* Get WMDRM register function pointer*/
    fptr = (int (*)(void* nexPlayerClassInstance, unsigned int uiDRMType, NEXPLAYERWMDRMDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_WMDRMDESCRAMBLE_CALLBACK_FUNC_MULTI);

    LOGI("[initDRMManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initDRMManagerMulti] error=%s", dlerror());
    }
    
    /* Register WMDRM descramble function */
    if (fptr != NULL)
        (*fptr)( /*NXWMDRM_PAYLOAD_TYPE */ (void*)nexPlayerInstance, NXWMDRM_PACKET_TYPE, wmdrmDescrambleCallbackFunc, NULL);

    dlclose(handle); 
#endif	// _SUPPORT_WMDRM_    
   
    return 0;
}