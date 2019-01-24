//
//  HttpRetrieveStoreDataCallbackSample_jni.c
//  NexPlayerSDK_HW
//
//  Created by Lee Ian on 13. 10. 2..
//  Copyright (c) 2013 Nexstreaming. All rights reserved.
//

#include <stdio.h>
#include <string.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>
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
#define NEXPLAYERENGINE_HTTPRETRIEVEDATA_CALLBACK_FUNC "nexPlayerSWP_RegisterHTTPRetrieveDataCallBackFunc"
#define NEXPLAYERENGINE_HTTPSTOREDATA_CALLBACK_FUNC "nexPlayerSWP_RegisterHTTPStoreDataCallBackFunc"
#define NEXPLAYERENGINE_HTTPRETRIEVEDATA_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterHTTPRetrieveDataCallBackFunc_Multi"
#define NEXPLAYERENGINE_HTTPSTOREDATA_CALLBACK_FUNC_MULTI "nexPlayerSWP_RegisterHTTPStoreDataCallBackFunc_Multi"

#define  LOG_TAG    "HTTPDataCallback_SAMPLE"

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

#define INVALID_8BYTE 0xFFFFFFFFFFFFFFFF
#define INVALID_4BYTE 0xFFFFFFFF

const char*	g_storeCachFolder = NULL;
const char*	g_retrieveCachFolder = NULL;

static void _STRUTIL_ReplaceChar(char* pStr, char pTarget, char pDst)
{
	char *pPos = pStr;
	while (*pPos != '\0')
	{
		if (*pPos == pTarget)
		{
			*pPos = pDst;
		}
		pPos++;
	}
}

static unsigned char _ConvUrlToPath(char* pUrl, unsigned long long qwOffset, unsigned long long qwLen, char* pRootPath, char* pPathBuf)
{
	char* pPos = strstr(pUrl, "://");
	if (!pPos)
	{
		LOGE("[_ConvUrlToPath] pPos is null\n");
		return 0;
	}
	
	if(pRootPath == NULL)
	{
		LOGE("[_ConvUrlToPath] pRootPath is NULL");
		return 0;
	}
	
	pPos += strlen("://");
	
	if (qwOffset != INVALID_8BYTE && qwLen != INVALID_8BYTE)
	{		
		sprintf(pPathBuf, "%s%s_%lld_%lld", pRootPath, pPos, qwOffset, qwLen);
	}
	else
	{
		sprintf(pPathBuf, "%s%s", pRootPath, pPos);
	}
	
	pPos = pPathBuf + strlen(pRootPath);
	
	// Just trick! Replace special character in pUrl to '_'
	_STRUTIL_ReplaceChar(pPos, '\\', '_');
	_STRUTIL_ReplaceChar(pPos, '/', '_');
	_STRUTIL_ReplaceChar(pPos, ':', '_');
	_STRUTIL_ReplaceChar(pPos, '*', '_');
	_STRUTIL_ReplaceChar(pPos, '?', '_');
	_STRUTIL_ReplaceChar(pPos, '=', '_');
	_STRUTIL_ReplaceChar(pPos, '<', '_');
	_STRUTIL_ReplaceChar(pPos, '>', '_');
	_STRUTIL_ReplaceChar(pPos, '|', '_');

	LOGI("[_ConvUrlToPath] URL(%s), Offset(%lld), Len(%lld)", pUrl, qwOffset, qwLen);
	LOGI("[_ConvUrlToPath] Path(%s))", pPathBuf);	
	
	return 1;
}

#if 0
char * findEntry(const char *cachFolder, const char *url)
{    
	char *pEntry = NULL;
	char strDB[512];
	char strLine[1024];
	char strURI[1024], strEntry[512];
	int i, j;
	
	sprintf(strDB, "%scache.db", cachFolder);
	FILE *db = fopen(strDB, "rt");
	
	if(db != NULL)
	{
		// Read one line
		i=0, j=0;
		
		
		char c = fgetc(db);
		while(c != (char)EOF)
		{
			if(c == '\n')
			{
				strLine[i] = 0;
				// End of line. Compare url
				scanf("%s %s", strURI, strEntry);
				
				LOGI("Entry[%d] URI:%s Entry:%s\n", j, strURI, strEntry);
				i=0;
				j++;
			} 
			else
			{
				strLine[i++] = c;
			}
		
			c = fgetc(db);	
		}
	}
  	
  return pEntry;	
}
#endif

#define _EXTIF_SUCCESS 0
#define _EXTIF_ERROR 1

#define	DEFAULT_READ_BUFF_LEN		20*1024*1024
char * g_pDataBuf = NULL;
int    g_nDataBufLen = 0;

static int HTTPRetrieveDataCallbackFunc(char* pUrl,
										unsigned long long dwOffset,
										unsigned long long dwLength,
										char** ppOutputBuffer,
										unsigned long long* pdwSize,
										void* pUserData)
{
	LOGI("[HTTPRetrieveDataCallbackFunc] URL(%s), Offset(0x%llx), Length : %lld, ppOutputBuffer : %p, Size : %lld\n",
		 pUrl, dwOffset, dwLength, *ppOutputBuffer, *pdwSize);

	char pPath[4096] = {0, };
//	unsigned long qwDataSize	= 0;
	FILE* hFile = NULL;
	
//	char *pEntry = findEntry(g_retrieveCachFolder, pUrl);
	
	if (_ConvUrlToPath(pUrl, dwOffset, dwLength, (char*)g_retrieveCachFolder, pPath) == 0)
	{
		return _EXTIF_ERROR;
	}
	
	hFile = fopen(pPath, "rb");
	if (hFile != NULL)
	{
		int nRead = 0;
		fseeko(hFile, 0, SEEK_END);
		unsigned long qwTotFileSize = ftello(hFile);
		fseeko(hFile, 0, SEEK_SET);
		unsigned long qwTotReadSize	= 0;
		
		LOGI("[HTTPRetrieveDataCallbackFunc %4d] TotalFileSize(%ld)\n", __LINE__, qwTotFileSize);
		
		if (qwTotFileSize == 0)
		{
			LOGE("[HTTPRetrieveDataCallbackFunc %4d] _EXTIF_RetrieveCacheData(%lld, %lld): FileSize is 0! [%s]\n", __LINE__, dwOffset, dwLength, pUrl);
			fclose(hFile);
			return _EXTIF_ERROR;
		}

		if(dwLength != INVALID_8BYTE && qwTotFileSize != dwLength)
		{
			LOGE("[HTTPRetrieveDataCallbackFunc %4d] FileSize and request size are different(Total:%lu, Read:%llu)\n", __LINE__, qwTotFileSize, dwLength);
			fclose(hFile);	
			return _EXTIF_ERROR;			
		}

		if(qwTotFileSize > g_nDataBufLen)
		{
			g_nDataBufLen = qwTotFileSize < DEFAULT_READ_BUFF_LEN ? DEFAULT_READ_BUFF_LEN : qwTotFileSize;

			if(g_pDataBuf != NULL)
			{
				free(g_pDataBuf);
				g_pDataBuf = NULL;
			}

			LOGI("[HTTPRetrieveDataCallbackFunc %4d] Malloc read buffer (size:%d)\n", __LINE__, g_nDataBufLen);
		}

		if(g_pDataBuf == NULL)
		{
			g_pDataBuf = (char *)malloc(g_nDataBufLen);
		}
		
		qwTotReadSize = fread(g_pDataBuf, 1, qwTotFileSize, hFile);	
		if(qwTotReadSize != qwTotFileSize)
		{
			LOGE("[HTTPRetrieveDataCallbackFunc %4d] File Read Failure(%d)\n", __LINE__, nRead);
			fclose(hFile);	
			return _EXTIF_ERROR;			
		}
		
		fclose(hFile);
		
		LOGI("[HTTPRetrieveDataCallbackFunc %4d] Read: %ld, FileSize: %ld, Url[%s], _EXTIF_RetrieveCacheData(%lld, %lld): \n",
						__LINE__, qwTotReadSize, qwTotFileSize, pUrl, dwOffset, dwLength );
		*ppOutputBuffer = g_pDataBuf;
		*pdwSize = qwTotReadSize;
		
		return _EXTIF_SUCCESS;	// success
	}
	else
	{
		LOGE("[HTTPRetrieveDataCallbackFunc %4d] File Open Error(%s)\n", __LINE__, pPath);
	}
	
	return _EXTIF_ERROR;
}

static int HTTPStoreDataCallbackFunc(char* pUrl,
									 unsigned long long dwOffset,
									 unsigned long long dwLength,
									 char* pBuffer,
									 unsigned long long dwSize,
									 void* pUserData)
{
	LOGI("[HTTPStoreDataCallbackFunc] URL(%s), Offset(0x%llx), Length : %lld, pBuffer : %p, Size : %lld\n",
		 pUrl, dwOffset, dwLength, pBuffer, dwSize);

	char pPath[4096] = {0, };
	FILE* hFile = NULL;
	
	if (_ConvUrlToPath(pUrl, dwOffset, dwLength, (char *)g_storeCachFolder, pPath) == 0)
	{
		return _EXTIF_ERROR;
	}
	
	LOGI("[HTTPStoreDataCallbackFunc %4d] _EXTIF_StoreCacheData: Range(%lld, %lld), size: %lld, [%s]\n", __LINE__, dwOffset, dwLength, dwSize, pUrl);
	
	hFile = fopen(pPath, "wb");
	if (hFile != NULL)
	{
		unsigned long qwDataSize	= 0;
		if (dwOffset != INVALID_8BYTE && dwLength != INVALID_8BYTE)		// Partial Resource.
		{
			qwDataSize = dwLength;
		}
		else	// Entire Resource.
		{
			qwDataSize = dwSize;
		}
		fwrite(pBuffer, 1, (unsigned long)qwDataSize, hFile);
		fclose(hFile);
		
		return _EXTIF_SUCCESS;	// success
	}
	
	return _EXTIF_ERROR;
}

jint Java_com_nexstreaming_httpretrievestoresample_HTTPRetrieveDataManager_initManager (JNIEnv * env,
																						jobject clazz,
																							 jstring libName,
																							 jstring cachFolder)
{
    LOGI("[HTTPRetrieveDataManager.initManager] Start \n");
    
    void *handle = NULL;

    if(cachFolder == NULL || libName == NULL)
    {
    	LOGE("[HTTPRetrieveDataManager.initManager] cachFolder or libName is NULL!");
		return -1;
    }
    
    g_retrieveCachFolder = (*env)->GetStringUTFChars(env, cachFolder, NULL);
	if(g_retrieveCachFolder == NULL)
	{
		LOGE("[HTTPRetrieveDataManager.initManager] cachFolder is NULL!");
		return -1;
	}
	else
	{
		LOGI("[HTTPRetrieveDataManager.initManager] Retrieve Cach Folder : %s\n", g_retrieveCachFolder);
	}
		
    int (*fptr)(NEXPLAYERHTTPRetrieveDataCallbackFunc pCallbackFunc, void *pUserData);
    
    const char *str;
    str = (*env)->GetStringUTFChars(env, libName, NULL);
    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
	
		LOGI("[HTTPRetrieveDataManager.initManager] libName[%p]:%s",handle, str);
	}
    else
	{
		/* Load Default NexPlayerEngine library */
		handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[HTTPRetrieveDataManager.initManager] error=%s", dlerror());
        return -1;
    }
    
    /* Get DRM register function pointer*/
    fptr = (int (*)(NEXPLAYERHTTPRetrieveDataCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HTTPRETRIEVEDATA_CALLBACK_FUNC);
    
    LOGI("[HTTPRetrieveDataManager.initManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[HTTPRetrieveDataManager.initManager] error=%s", dlerror());
    }
    
		LOGI("[HTTPRetrieveDataManager.initManager] Callback ptr : %p", HTTPRetrieveDataCallbackFunc);
	
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(HTTPRetrieveDataCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}

jint Java_com_nexstreaming_httpretrievestoresample_HTTPStoreDataManager_initManager (JNIEnv * env,
																					 jobject clazz,
																				   jstring libName,
																				   jstring cachFolder)
{
    LOGI("[HTTPStoreDataManager.initManager] Start \n");
    
    void *handle = NULL;

    if(cachFolder == NULL || libName == NULL)
    {
    	LOGE("[HTTPStoreDataManager.initManager] cachFolder or libName is NULL!");
		return -1;
    }
    
    g_storeCachFolder = (*env)->GetStringUTFChars(env, cachFolder, NULL);;
		if(g_storeCachFolder == NULL)
		{
			LOGE("[HTTPStoreDataManager.initManager] cachFolder is NULL!");
			return -1;
		}
		else
		{
			LOGI("[HTTPStoreDataManager.initManager] Store Cach Folder : %s\n", g_storeCachFolder);
		}
	
    int (*fptr)(NEXPLAYERHTTPStoreDataCallbackFunc pCallbackFunc, void *pUserData);
    
    const char *str;
    str = (*env)->GetStringUTFChars(env, libName, NULL);
    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
		
		LOGI("[HTTPStoreDataManager.initManager] libName[%p]:%s",handle, str);
	}
    else
	{
	    /* Load Default NexPlayerEngine library */
	    handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[HTTPStoreDataManager.initManager] error=%s", dlerror());
        return -1;
    }
    
    /* Get DRM register function pointer*/
    fptr = (int (*)(NEXPLAYERHTTPStoreDataCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HTTPSTOREDATA_CALLBACK_FUNC);
    
    LOGI("[HTTPStoreDataManager.initManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[HTTPStoreDataManager.initManager] error=%s", dlerror());
    }
    
	LOGI("[HTTPStoreDataManager.initManager] Callback ptr : %p", HTTPStoreDataCallbackFunc);
	
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(HTTPStoreDataCallbackFunc, NULL);
	
    dlclose(handle);
    

	
    return 0;
}

jint Java_com_nexstreaming_httpretrievestoresample_HTTPRetrieveDataManager_initManagerMulti (JNIEnv * env,
																						jobject clazz,
																						jobject nexPlayerInstance,
																						jstring libName,
																						jstring cachFolder)
{
    LOGI("[HTTPRetrieveDataManager.initManagerMulti] Start \n");
    
    void *handle = NULL;

    if(cachFolder == NULL || libName == NULL)
    {
    	LOGE("[HTTPRetrieveDataManager.initManagerMulti] cachFolder or libName is NULL!");
		return -1;
    }
    
    g_retrieveCachFolder = (*env)->GetStringUTFChars(env, cachFolder, NULL);
	if(g_retrieveCachFolder == NULL)
	{
		LOGE("[HTTPRetrieveDataManager.initManagerMulti] cachFolder is NULL!");
		return -1;
	}
	else
	{
		LOGI("[HTTPRetrieveDataManager.initManagerMulti] Retrieve Cach Folder : %s\n", g_retrieveCachFolder);
	}
	
    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERHTTPRetrieveDataCallbackFunc pCallbackFunc, void *pUserData);
    
    const char *str;
    str = (*env)->GetStringUTFChars(env, libName, NULL);
    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
		
		LOGI("[HTTPRetrieveDataManager.initManagerMulti] libName[%p]:%s",handle, str);
	}
    else
	{
		/* Load Default NexPlayerEngine library */
		handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[HTTPRetrieveDataManager.initManagerMulti] error=%s", dlerror());
        return -1;
    }
    
    /* Get DRM register function pointer*/
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERHTTPRetrieveDataCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HTTPRETRIEVEDATA_CALLBACK_FUNC_MULTI);
    
    LOGI("[HTTPRetrieveDataManager.initManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[HTTPRetrieveDataManager.initManagerMulti] error=%s", dlerror());
    }
    
	LOGI("[HTTPRetrieveDataManager.initManagerMulti] Callback ptr : %p", HTTPRetrieveDataCallbackFunc);
	
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, HTTPRetrieveDataCallbackFunc, NULL);
	
    dlclose(handle);
	
    return 0;
}

jint Java_com_nexstreaming_httpretrievestoresample_HTTPStoreDataManager_initManagerMulti (JNIEnv * env,
																					 jobject clazz,
																					 jobject nexPlayerInstance,
																					 jstring libName,
																					 jstring cachFolder)
{
    LOGI("[HTTPStoreDataManager.initManagerMulti] Start \n");
    
    void *handle = NULL;

    if(cachFolder == NULL || libName == NULL)
    {
    	LOGE("[HTTPStoreDataManager.initManagerMulti] cachFolder or libName is NULL!");
		return -1;
    }
    
    g_storeCachFolder = (*env)->GetStringUTFChars(env, cachFolder, NULL);
	if(g_storeCachFolder == NULL)
	{
		LOGE("[HTTPStoreDataManager.initManagerMulti] cachFolder is NULL!");
		return -1;
	}
	else
	{
		LOGI("[HTTPStoreDataManager.initManagerMulti] Store Cach Folder : %s\n", g_storeCachFolder);
	}
	
    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERHTTPStoreDataCallbackFunc pCallbackFunc, void *pUserData);
    
    const char *str = NULL;

    if(libName != NULL)
    {
    	str = (*env)->GetStringUTFChars(env, libName, NULL);
    }

    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
		
		LOGI("[HTTPStoreDataManager.initManagerMulti] libName[%p]:%s",handle, str);
	}
    else
	{
	    /* Load Default NexPlayerEngine library */
	    handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[HTTPStoreDataManager.initManagerMulti] error=%s", dlerror());
        return -1;
    }
    
    /* Get DRM register function pointer*/
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERHTTPStoreDataCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HTTPSTOREDATA_CALLBACK_FUNC_MULTI);
    
    LOGI("[HTTPStoreDataManager.initManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[HTTPStoreDataManager.initManagerMulti] error=%s", dlerror());
    }
    
	LOGI("[HTTPStoreDataManager.initManagerMulti] Callback ptr : %p", HTTPStoreDataCallbackFunc);
	
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, HTTPStoreDataCallbackFunc, NULL);
	
    dlclose(handle);
    
	
	
    return 0;
}

jint Java_com_nexstreaming_httpretrievestoresample_HTTPRetrieveDataManager_deinitManager (JNIEnv * env,
																						jobject clazz,
																							 jstring libName)
{
    LOGI("[HTTPRetrieveDataManager.deinitManager] Start \n");
    
    void *handle = NULL;
    int (*fptr)(NEXPLAYERHTTPRetrieveDataCallbackFunc pCallbackFunc, void *pUserData);
    
    const char *str = NULL;

    if(libName != NULL)
    {
    	str = (*env)->GetStringUTFChars(env, libName, NULL);
    }

    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
	
		LOGI("[HTTPRetrieveDataManager.deinitManager] libName[%p]:%s",handle, str);
	}
    else
	{
		/* Load Default NexPlayerEngine library */
		handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
		
	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[HTTPRetrieveDataManager.deinitManager] error=%s", dlerror());
        return -1;
    }
    
    /* Get DRM register function pointer*/
    fptr = (int (*)(NEXPLAYERHTTPRetrieveDataCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HTTPRETRIEVEDATA_CALLBACK_FUNC);
    
    LOGI("[HTTPRetrieveDataManager.deinitManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[HTTPRetrieveDataManager.deinitManager] error=%s", dlerror());
    }
	
    /* UnRegister DRM descramble function */
    if (fptr != NULL)
        (*fptr)(NULL, NULL);
	
    dlclose(handle);

    if(g_pDataBuf)
    {
    	free(g_pDataBuf);
    	g_pDataBuf = NULL;

    	LOGI("[HTTPRetrieveDataManager.deinitManager] Free Read Buffer.");
    }
	
    return 0;
}

jint Java_com_nexstreaming_httpretrievestoresample_HTTPStoreDataManager_deinitManager (JNIEnv * env,
																					 jobject clazz,
																				   jstring libName)
{
    LOGI("[HTTPStoreDataManager.deinitManager] Start \n");
    
    void *handle = NULL;
    int (*fptr)(NEXPLAYERHTTPStoreDataCallbackFunc pCallbackFunc, void *pUserData);
    
    const char *str = NULL;
    if(libName != NULL)
    {
    	str = (*env)->GetStringUTFChars(env, libName, NULL);
    }
    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
		
		LOGI("[HTTPStoreDataManager.deinitManager] libName[%p]:%s",handle, str);
	}
    else
	{
	    /* Load Default NexPlayerEngine library */
	    handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[HTTPStoreDataManager.deinitManager] error=%s", dlerror());
        return -1;
    }
    
    /* Get DRM register function pointer*/
    fptr = (int (*)(NEXPLAYERHTTPStoreDataCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HTTPSTOREDATA_CALLBACK_FUNC);
    
    LOGI("[HTTPStoreDataManager.deinitManager] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[HTTPStoreDataManager.deinitManager] error=%s", dlerror());
    }

	
    /* Register DRM descramble function */
    if (fptr != NULL)
        (*fptr)(NULL, NULL);
	
    dlclose(handle);
    

	
    return 0;
}

jint Java_com_nexstreaming_httpretrievestoresample_HTTPRetrieveDataManager_deinitManagerMulti (JNIEnv * env,
																						jobject clazz,
																						jobject nexPlayerInstance,
																						jstring libName)
{
    LOGI("[HTTPRetrieveDataManager.deinitManagerMulti] Start \n");
    
    void *handle = NULL;
    
    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERHTTPRetrieveDataCallbackFunc pCallbackFunc, void *pUserData);
    
    const char *str = NULL;
    if(libName)
    {
    	str = (*env)->GetStringUTFChars(env, libName, NULL);
    }
    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
		
		LOGI("[HTTPRetrieveDataManager.deinitManagerMulti] libName[%p]:%s",handle, str);
	}
    else
	{
		/* Load Default NexPlayerEngine library */
		handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[HTTPRetrieveDataManager.deinitManagerMulti] error=%s", dlerror());
        return -1;
    }
    
    /* Get DRM register function pointer*/
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERHTTPRetrieveDataCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HTTPRETRIEVEDATA_CALLBACK_FUNC_MULTI);
    
    LOGI("[HTTPRetrieveDataManager.deinitManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[HTTPRetrieveDataManager.deinitManagerMulti] error=%s", dlerror());
    }
    
    /* UnRegister DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, NULL, NULL);
	
    dlclose(handle);

    if(g_pDataBuf)
    {
    	free(g_pDataBuf);
    	g_pDataBuf = NULL;

    	LOGI("[HTTPRetrieveDataManager.deinitManagerMulti] Free Read Buffer.");
    }
	
    return 0;
}

jint Java_com_nexstreaming_httpretrievestoresample_HTTPStoreDataManager_deinitManagerMulti (JNIEnv * env,
																					 jobject clazz,
																					 jobject nexPlayerInstance,
																					 jstring libName)
{
    LOGI("[deinitManagerMulti] Start \n");
    
    void *handle = NULL;
	
    int (*fptr)(void* nexPlayerClassInstance, NEXPLAYERHTTPStoreDataCallbackFunc pCallbackFunc, void *pUserData);
    
    const char *str = NULL;
    if(libName != NULL)
    {
    	str = (*env)->GetStringUTFChars(env, libName, NULL);
    }
    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
		
		LOGI("[deinitManagerMulti] libName[%p]:%s",handle, str);
	}
    else
	{
	    /* Load Default NexPlayerEngine library */
	    handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[deinitManagerMulti] error=%s", dlerror());
        return -1;
    }
    
    /* Get DRM register function pointer*/
    fptr = (int (*)(void* nexPlayerClassInstance, NEXPLAYERHTTPStoreDataCallbackFunc pCallbackFunc, void *pUserData))dlsym(handle, NEXPLAYERENGINE_HTTPSTOREDATA_CALLBACK_FUNC_MULTI);
    
    LOGI("[deinitManagerMulti] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[deinitManagerMulti] error=%s", dlerror());
    }
    
    /* UnRegister DRM descramble function */
    if (fptr != NULL)
        (*fptr)((void*)nexPlayerInstance, NULL, NULL);
	
    dlclose(handle);
    
    return 0;
}

