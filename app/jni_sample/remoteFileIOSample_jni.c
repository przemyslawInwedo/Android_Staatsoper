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
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#define NEXPLAYERENGINE_LIB "/data/data/com.nexstreaming.nexplayersample/lib/libnexplayerengine.so"
#define	NEXPLAYERENGINE_REGISTER_REMOTE_FILE_IO_FUNC  "nexPlayerSWP_RegisterRemoteFileIOInterface"

#define  LOG_TAG    "REMOTE_FILE_IO_SAMPLE"

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


NEXFileHandle nexRemoteFile_OpenFt ( char* pFileName, NEXFileMode iMode, void *pUserData )
{
	NEXFileHandle hRetFile;
	char* flags;
	
	LOGI("nexRemoteFile_OpenFt" );

//	if ( iMode & NEX_FILE_CREATE )
//	{
//		flags |= O_CREAT;
//	}
//
//	if ( ( iMode & NEX_FILE_READ ) && !(iMode & NEX_FILE_WRITE) )
//		flags |= O_RDONLY;
//	else if ( !( iMode & NEX_FILE_READ ) && (iMode & NEX_FILE_WRITE) )
//		flags |= O_WRONLY;
//	else
//		flags |= O_RDWR;
	
	switch (iMode) {
		case NEX_FILE_READ:
			flags = "rb";
			break;
		case NEX_FILE_WRITE:
		case NEX_FILE_READWRITE:
			flags = "ab";
			break;
		case NEX_FILE_CREATE:
			flags = "wb";
			break;
		default:
			break;
	}
	
	hRetFile = (void*)fopen( pFileName, flags );
	
	LOGI( "File handle is 0x%p.", hRetFile );

	return hRetFile;
}

int nexRemoteFile_CloseFt ( NEXFileHandle hFile, void *pUserData )
{
	LOGI("in Remote File Close, %p", hFile );
	return fclose( (FILE*)hFile );
}

int nexRemoteFile_ReadFt ( NEXFileHandle hFile, void *pBuf, unsigned int uiSize, unsigned int uiCount, void *pUserData )
{
	int nRead = fread(pBuf, uiSize, uiCount, (FILE*)hFile);
	LOGI("in RemoteFile Read, %d, %d", uiSize*uiCount, nRead );
	return nRead;	
}

long long nexRemoteFile_Read64Ft ( NEXFileHandle hFile, void *pBuf, unsigned long long uiSize, unsigned long long uiCount, void *pUserData )
{
	long long nRead = (long long)read( hFile, pBuf, uiSize*uiCount );
	LOGI("in RemoteFile Read64, %llu, %lld", uiSize*uiCount, nRead );
	return nRead;	
}

int nexRemoteFile_SeekFt ( NEXFileHandle hFile, int iOffset, NEXFileSeekOrigin iOrigin, void *pUserData )
{
	int origin;

	switch ( iOrigin )
	{
	case NEX_SEEK_BEGIN : origin = SEEK_SET; break;
	case NEX_SEEK_CUR : origin = SEEK_CUR; break;
	case NEX_SEEK_END : origin = SEEK_END; break;
	}
	
	int iRet = fseek( (FILE*)hFile, iOffset, origin );
	LOGI("in RemoteFile Seek, %p, %d, %d, %d", hFile, iOffset, origin, iRet );
	return iRet;	
	
}

long long nexRemoteFile_Seek64Ft ( NEXFileHandle hFile, long long iOffset, NEXFileSeekOrigin iOrigin, void *pUserData )
{
	int origin;

	switch ( iOrigin )
	{
	case NEX_SEEK_BEGIN : origin = SEEK_SET; break;
	case NEX_SEEK_CUR : origin = SEEK_CUR; break;
	case NEX_SEEK_END : origin = SEEK_END; break;
	}
	
	long long iRet = (long long)fseeko((FILE*)hFile, (off_t)iOffset, origin);
	LOGI("in RemoteFile Seek64, %p, %lld, %d, %lld", hFile, iOffset, origin, iRet );
	return iRet;
}

long long nexRemoteFile_SizeFt ( NEXFileHandle hFile, void *pUserData )
{
	off_t nCurrentPos;
	off_t nSize;
	
	nCurrentPos = ftello((FILE*)hFile);
	fseeko((FILE*)hFile, 0, SEEK_END);
	nSize = ftello((FILE*)hFile);
	fseeko((FILE*)hFile, nCurrentPos, SEEK_SET);

	LOGI("in RemoteFile Size, %p, %ld", hFile, nSize );
	
	return (long long)nSize;
}

jint Java_com_nexstreaming_remoteFileIO_remoteFileIOManager_registerRemoteFileIO (  JNIEnv * env, 
																			   	   jobject clazz,
																			   	   jstring libName)
{
    LOGI("[registerRemoteFileIO] Start \n");
    
    void *handle = NULL;
    int (*fptr)(NEXPLAYERRemoteFileIOInterface *pRemoteFileIOInterface, void *pUserData); 
	
    const char *str;
    str = (*env)->GetStringUTFChars(env, libName, NULL);
    if(str != NULL)
	{
		handle = dlopen(str, RTLD_LAZY);
		
		LOGI("[registerRemoteFileIO] libName[%p]:%s",handle, str);
	}
    else
    {
	    /* Load Default NexPlayerEngine library */
	    handle = dlopen(NEXPLAYERENGINE_LIB, RTLD_LAZY);
	}
	
    LOGI("initializeAgent : nextreaming handle=%p", handle);
    if (handle == NULL)
    {
        LOGI("[registerRemoteFileIO] error=%s", dlerror());
        return -1;
    }
    
	/* Get remote file IO interface register function pointer*/
    fptr = (int (*)(NEXPLAYERRemoteFileIOInterface *pRemoteFileIOInterface, void *pUserData))dlsym(handle, NEXPLAYERENGINE_REGISTER_REMOTE_FILE_IO_FUNC);

    LOGI("[registerRemoteFileIO] fptr = %p", fptr);
    if (fptr == NULL)
    {
        LOGI("[registerRemoteFileIO] error=%s", dlerror());
    }
    
    /* Register remote file IO inferface function */
    if (fptr != NULL)
    {
    	void * pUserData = NULL;
    	NEXPLAYERRemoteFileIOInterface stFileIO;
    	
    	stFileIO.Open 	= nexRemoteFile_OpenFt;
    	stFileIO.Close 	= nexRemoteFile_CloseFt;
    	stFileIO.Seek 	= nexRemoteFile_SeekFt;
    	stFileIO.Seek64 = nexRemoteFile_Seek64Ft;
    	stFileIO.Read 	= nexRemoteFile_ReadFt;
		stFileIO.Read64 = nexRemoteFile_Read64Ft;
		stFileIO.Write 	= NULL;
		stFileIO.Size 	= nexRemoteFile_SizeFt;    	
    	
        (*fptr)(&stFileIO, pUserData);
	}
    dlclose(handle); 
 
   
    return 0;
}
