//
// NexHDSample_jni.c
//  NexPlayerSDK_HW
//
//  Created by Daniel Kwon on 14. 08. 06.
//  Copyright (c) 2014 Nexstreaming. All rights reserved.
//

#include "dlfcn.h"
#include "nexplayer_jni.h"


#include <jni.h>
#include <assert.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <android/log.h>


#define NEXPLAYERENGINE_LIB "/data/data/com.nexstreaming.nexplayersample/lib/libnexplayerengine.so"
#define NEXPLAYERENGINE_NEXHTTPDOWNLOADER_REGISTER_FUNC "nexPlayerSWP_RegisterNexHTTPDownloaderInterface"

#define  LOG_TAG    "NEX_HD_SAMPLE"

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


typedef struct
{
	NEXHD_CALLBACK_SETINFO_RESERVED		m_stReserved;
	NEXHD_CALLBACK_SETINFO_CONNECT		m_stCBConnect;
	NEXHD_CALLBACK_SETINFO_CONNECTED	m_stCBConnected;
	NEXHD_CALLBACK_SETINFO_MSG_SENT		m_stCBMsgSent;	
	void *								m_pUserData;
} NexHDLStruct;

int nexHDL_Create(	void **			a_ppHandle, 	//[OUT]
					unsigned int *	uTaskPriority, 	//[IN]
					unsigned int *	uTaskStackSize, //[IN]
					void*			_pUserData) 	//[IN]
{
	LOGI("[nexHDL_Create] start....");
	if(a_ppHandle)
	{
		NexHDLStruct *pHDL = (NexHDLStruct*)malloc(sizeof(NexHDLStruct));
		if(pHDL)
		{
			memset(pHDL, 0, sizeof(NexHDLStruct));
			*a_ppHandle = (void *)pHDL;
		}
		
		LOGI("[nexHDL_Create] handle (%p)", pHDL);
	}
	LOGI("[nexHDL_Create] end....");
	return 0;	
}

int nexHDL_Destory(	void* a_pHandle, //[IN]
						void*a_pUserData )	// [IN]
{
	if(a_pHandle)
	{
		free(a_pHandle);
	}
	return 0;
}

int nexHDL_CreateMsg(	void* 			a_pHandle,  	//[IN]
						unsigned int *	a_uMsgID, 		//[OUT]
						void *			a_pUserData )	// [IN]
{
	LOGI("[nexHDL_CreateMsg] start....");
	NexHDLStruct *pHDL = (NexHDLStruct *)a_pHandle;
	
	LOGI("[nexHDL_CreateMsg] pHDL: %p", pHDL);
	
	*a_uMsgID = 10;
	LOGI("[nexHDL_CreateMsg] end....");
	return 0;
}												

int nexHDL_DestroyMsg(	void* 			a_pCore, 		//[IN]
						unsigned int 	a_uMsgID, 		//[IN]
						void *			a_pUserData )	// [IN]
{
	LOGI("[nexHDL_DestroyMsg] start....");
	
	NexHDLStruct *pHDL = (NexHDLStruct *)a_pCore;
	LOGI("[nexHDL_DestroyMsg] pHDL: %p", pHDL);
	
	LOGI("[nexHDL_DestroyMsg] end....");	
	
	return 0;
}												
												
int nexHDL_SendMsg(	void* 							a_pHandle,  	//[IN]
					unsigned int 					a_uMsgID, 		//[IN]
					NEXHD_CALLBACK_SENDMSG_PARAM *	a_pMsgParam,	//[IN]
					NEXHD_CALLBACK_SENDMSG_CBLIST *	a_pCBList, 		//[IN]
					void *							a_pUserData )	//[IN]
{
	char *pHeader = NULL;
	
	LOGI("[nexHDL_SendMsg] start....");
	NexHDLStruct *pHDL = (NexHDLStruct *)a_pHandle;
	
	if(a_pMsgParam)
	{
		if(a_pMsgParam->pMsg)
		{
			LOGI("Msg:%s", a_pMsgParam->pMsg);
		}
		
		if(a_pMsgParam->pUrl)
		{
			LOGI("URL:%s", a_pMsgParam->pUrl);
		}

		// Start to connect to server.
		if(pHDL->m_stCBConnect.pCB)
		{
			NEXHD_CALLBACK_PARAM_CONNECT stConnectParam;
			stConnectParam.uMsgID = a_uMsgID;
			stConnectParam.pAddr = "127.0.0.1";
			stConnectParam.pUrl = a_pMsgParam->pUrl;
			stConnectParam.uPort = 80;
			pHDL->m_stCBConnect.pCB(&stConnectParam, pHDL->m_stCBConnect.pUserData);
		}

		// Connected to Server.
		if(pHDL->m_stCBConnected.pCB)
		{
			NEXHD_CALLBACK_PARAM_CONNECTED stConnectedParam;
			stConnectedParam.uMsgID = a_uMsgID;
			stConnectedParam.pAddr = a_pMsgParam->pUrl;
			stConnectedParam.pUrl = a_pMsgParam->pUrl;
			stConnectedParam.uPort = 80;
			pHDL->m_stCBConnected.pCB(&stConnectedParam, pHDL->m_stCBConnected.pUserData);
		}		

		// Generate Message and send message.
		if(pHDL->m_stCBMsgSent.pCB)
		{
			NEXHD_CALLBACK_PARAM_MSG_SENT stMsgSentParam;
			stMsgSentParam.uMsgID = a_uMsgID;
			stMsgSentParam.pUrl = a_pMsgParam->pUrl;
			stMsgSentParam.pMsg = a_pMsgParam->pMsg;
			stMsgSentParam.uMsgSize= strlen(a_pMsgParam->pMsg);
			pHDL->m_stCBMsgSent.pCB(&stMsgSentParam, pHDL->m_stCBMsgSent.pUserData);
		}		
		
		pHeader = (char *)malloc(1024);
		
		sprintf(pHeader, "%s", "HTTP/1.1 200 OK\r\n"\
								"Date: Fri, 29 Aug 2014 05:41:09 GMT \r\n"\
								"Content-Type: application/vnd.apple.mpegurl \r\n"\
								"PRO1:Accept-Ranges: bytes \r\n"\
								"Server: FlashCom/3.5.7 \r\n"\
								"Cache-Control: no-cache \r\n"\
								"Content-Length: 153 \r\n"\
								"\r\n");
		
		if(a_pCBList && a_pCBList->pHeaderRecvCB)
		{
			LOGI("Call HeaderRedvCB start.");
			a_pCBList->pHeaderRecvCB(a_pHandle, a_uMsgID, pHeader, strlen(pHeader), a_pCBList->pUserData);
			LOGI("Call HeaderRedvCB end.");
		}
		
		sprintf(pHeader, "%s", "#EXTM3U\r\n\
#EXT-X-VERSION:3\r\n\
#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=2781247,CODECS=\"avc1.77.31, mp4a.40.2\",RESOLUTION=1280x720\r\n\
chunklist_w4673171.m3u8\r\n");

		if(a_pCBList && a_pCBList->pDataRecvCB)
		{
			LOGI("Call DataRedvCB start.");
			a_pCBList->pDataRecvCB(a_pHandle, a_uMsgID, (unsigned char*)pHeader, strlen(pHeader), a_pCBList->pUserData);
			LOGI("Call DataRedvCB end.");
	}

		if(a_pCBList && a_pCBList->pRecvFinishCB)
		{
			LOGI("Call RecvFinishCB start.");
			a_pCBList->pRecvFinishCB(a_pHandle, a_uMsgID, eNEXHD_CALLBACK_ERRCODE_SUCCESS, a_pCBList->pUserData);
			LOGI("Call RecvFinishCB end.");
		}
				
		free(pHeader);
	}
	
	LOGI("[nexHDL_SendMsg] end....");
	return 0;
}

int nexHDL_CancelMsg(	void* 			a_pHandle,  	//[IN]
						unsigned int 	a_uMsgID, 		//[IN]
						void *			a_pUserData )	// [IN]
{
	LOGI("[nexHDL_CancelMsg] start....");
	NexHDLStruct *pHDL = (NexHDLStruct *)a_pHandle;
	LOGI("[nexHDL_CancelMsg] pHDL: %p", pHDL);
	
	return 0;
}

int nexHDL_PauseMsg(void* 			a_pHandle,  	//[IN]
					unsigned int 	a_uMsgID, 		//[IN]
					void *			a_pUserData )	// [IN]
{
	LOGI("[nexHDL_PauseMsg] start....");
	NexHDLStruct *pHDL = (NexHDLStruct *)a_pHandle;
	LOGI("[nexHDL_PauseMsg] pHDL: %p", pHDL);
	
	return 0;
}

int nexHDL_ResumeMsg(	void* 			a_pHandle,  	//[IN]
						unsigned int 	a_uMsgID, 		//[IN]
						void *			a_pUserData )	// [IN]
{
	LOGI("[nexHDL_ResumeMsg] start....");
	NexHDLStruct *pHDL = (NexHDLStruct *)a_pHandle;
	LOGI("[nexHDL_ResumeMsg] pHDL: %p", pHDL);
	
	return 0;
}

int nexHDL_SetInfo(	void* 							a_pHandle,  	//[IN]
					NEXHD_CALLBACK_SETINFO_TYPE 	a_eType, 		//[IN]
					NEXHD_CALLBACK_SETINFO_PARCEL *	a_pParcel,		//[OUT]
					void* 							a_pUserData )	// [IN]
{
	LOGI("[nexHDL_SetInfo] start.   eType(%d)", a_eType);
	NexHDLStruct *pHDL = (NexHDLStruct *)a_pHandle;
	
	switch (a_eType)
	{
	case eNEXHD_CALLBACK_SETINFO_RESERVED:
	{
		LOGI("[eNEXHD_CALLBACK_SETINFO_RESERVED] reserved(%d)", a_pParcel->stReserved.uReserved);
		pHDL->m_stReserved = a_pParcel->stReserved;

		break;
	}
	case eNEXHD_CALLBACK_SETINFO_CONNECT:
	{
		LOGI("[eNEXHD_CALLBACK_SETINFO_CONNECT] Connect Callback(%p), UserData(%p)", a_pParcel->stCBConnect.pCB, a_pParcel->stCBConnect.pUserData);
		pHDL->m_stCBConnect = a_pParcel->stCBConnect;
		
		break;
	}
	case eNEXHD_CALLBACK_SETINFO_CONNECTED:
	{
		LOGI("[eNEXHD_CALLBACK_SETINFO_CONNECTED] Connected Callback(%p), UserData(%p)", a_pParcel->stCBConnected.pCB, a_pParcel->stCBConnected.pUserData);
		pHDL->m_stCBConnected = a_pParcel->stCBConnected;
		break;
	}
	case eNEXHD_CALLBACK_SETINFO_MSG_SENT:
	{
		LOGI("[eNEXHD_CALLBACK_SETINFO_MSG_SENT] Message Sent Callback(%p), UserData(%p)", a_pParcel->stCBMsgSent.pCB, a_pParcel->stCBMsgSent.pUserData);
		pHDL->m_stCBMsgSent = a_pParcel->stCBMsgSent;
		break;
	}
	}
	return 0;
}	

int nexHDL_GetInfo(	void* 							a_pHandle,  	//[IN]
					NEXHD_CALLBACK_GETINFO_TYPE 	a_eType, 		//[IN]
					NEXHD_CALLBACK_GETINFO_PARCEL *	a_pParcel,		//[OUT]
					void* 							a_pUserData )	// [IN]
{
	LOGI("[nexHDL_GetInfo] start....");
	NexHDLStruct *pHDL = (NexHDLStruct *)a_pHandle;
	LOGI("[nexHDL_GetInfo] pHDL: %p", pHDL);
	
	return 0;
}														 

jint Java_com_nexstreaming_nexhdsample_NexHDManager_initManager(JNIEnv * env,
																jobject clazz,
																jstring libName)
{
    LOGI("[initDRMManager] Start \n");
    
    void *handle = NULL;
	
    int (*fptr_register)(NEXPLAYERNexHTTPDownloaderInterface *pNexHTTPDownloaerInterface, void *pUserData);

    NEXPLAYERNexHTTPDownloaderInterface interface = {0,};
	interface.Create		= nexHDL_Create;
	interface.Destroy 		= nexHDL_Destory;
	interface.CreateMsg 	= nexHDL_CreateMsg;
	interface.DestroyMsg 	= nexHDL_DestroyMsg;
	interface.SendMsg 		= nexHDL_SendMsg;
	interface.CancelMsg 	= nexHDL_CancelMsg;
	interface.PauseMsg 		= nexHDL_PauseMsg;
	interface.ResumeMsg 	= nexHDL_ResumeMsg;
	interface.SetInfo 		= nexHDL_SetInfo;
	interface.GetInfo 		= nexHDL_GetInfo;

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
    
    /* Get NexHD function pointer*/
    fptr_register= (int (*)(NEXPLAYERNexHTTPDownloaderInterface *pNexHTTPDownloaerInterface, void *pUserData))dlsym(handle, NEXPLAYERENGINE_NEXHTTPDOWNLOADER_REGISTER_FUNC);
    
    LOGI("[initDRMManager] fptr = %p", fptr_register);
    if (fptr_register == NULL)
    {
        LOGI("[initDRMManager] error=%s", dlerror());
    }
    
    /* Register NexHD create function */
    if (fptr_register != NULL)
    {
        (*fptr_register)(&interface, NULL);
	}
	
    dlclose(handle);
    return 0;
}