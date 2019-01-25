//
//  HLSAES128DescrambleWithByteRangeSample_jni.c
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

#define NEXPLAYERENGINE_LIB "../libs/armeabi/libnexplayerengine.so"
#define NEXPLAYERENGINE_HLSAES128DESCRAMBLE_WITH_BYTE_RANGE_CALLBACK_FUNC "nexPlayerSWP_RegisterHLSAES128DescrambleWithByteRangeCallBackFunc"
#define NEXPLAYERENGINE_HLSAES128DESCRAMBLE_WITH_BYTE_RANGE_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterHLSAES128DescrambleWithByteRangeCallBackFunc_Multi"


#define  LOG_TAG    "HLSAES128DescrambleWithByteRange_SAMPLE"

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

static int HLSAES128DescrambleWithByteRangeCallbackFunc(unsigned char*		pInBuf,			// [in] Input segment.
													   unsigned int			dwInBufSize,	// [in] Input segment size.
													   unsigned char*		pOutBuf,		// [out] decrypted segment.
													   unsigned int*		pdwOutBufSize,	// [out] The size of decrypted segment.
													   char*				pSegmentUrl,	// [in] Segment Url.
													   long long			qByteRangeOffset,	// [in] If qByteRangeOffset is -1, then it means that byte range is not used.
													   long long			qByteRangeLength,	// [in] If qByteRangeLength is 0, then it means that byte range is not used.
													   char*				pMpdUrl,		// [in] Original Url of the currently playing Mpd.
													   char*				pKeyAttr,		// [in] KeyInfo Attribute of the Segment.
													   unsigned int			dwSegmentSeq,	// [in]
													   unsigned char*		pKey,			// [in] Key. (Has meaning only when dwKeySize is bigger than 0)
													   unsigned int			dwKeySize,		// [in] Key size. (0 if no key is downloaded.)
													   void*				pUserData)		// [in]
{
	LOGI("[HLSAES128DescrambleWithByteRangeCallbackFunc] InputBuf(%p(%d)), OutputBuf(%p(%d)), segURL : %s, ByteRange %lld, Offset %lld, mpdUrl : %s, KeyAttr : %s, SegSeq : %d\n",
		 pInBuf, dwInBufSize, pOutBuf, *pdwOutBufSize, pSegmentUrl, qByteRangeLength, qByteRangeOffset, pMpdUrl, pKeyAttr, dwSegmentSeq);
	return 0;
}



jint Java_com_nexstreaming_hlsaes128descramblewithbyterangesample_HLSAES128DescrambleWithByteRangeManager_initManager (JNIEnv * env,
																														 jobject clazz,
																														 jstring libName)
{
    LOGI("[initManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(NEXPLAYERHLSAES128DescrambleWithByteRangeCallbackFunc pCallbackFunc, void *pUserData);
    
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
    fptr = (int (*)(NEXPLAYERHLSAES128DescrambleWithByteRangeCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HLSAES128DESCRAMBLE_WITH_BYTE_RANGE_CALLBACK_FUNC);
    
    LOGI("[initManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManager] error=%s", dlerror());
    }
    
	LOGI("[initManager] Callback ptr : %p", HLSAES128DescrambleWithByteRangeCallbackFunc);
	
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(HLSAES128DescrambleWithByteRangeCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}

jint Java_com_nexstreaming_hlsaes128descramblewithbyterangesample_HLSAES128DescrambleWithByteRangeManager_initManagerMulti (JNIEnv * env,
																															jobject clazz,
																															jobject nexPlayerInstance,
																															jstring libName)
{
    LOGI("[initManagerMulti] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERHLSAES128DescrambleWithByteRangeCallbackFunc pCallbackFunc, void *pUserData);
	
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
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERHLSAES128DescrambleWithByteRangeCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HLSAES128DESCRAMBLE_WITH_BYTE_RANGE_CALLBACK_FUNC_MULTI);
    
    LOGI("[initManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[initManagerMulti] error=%s", dlerror());
    }
    
	LOGI("[initManagerMulti] Callback ptr : %p", HLSAES128DescrambleWithByteRangeCallbackFunc);
	
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, HLSAES128DescrambleWithByteRangeCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}