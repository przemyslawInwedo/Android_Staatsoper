//
//  NexHLSDRMManager.hpp
//  NexPlayerSDK_HW
//
//  Created by Lee Ian on 09/11/2016.
//  Copyright © 2016 Nexstreaming. All rights reserved.
//

#ifndef NexHLSDRMManager_h
#define NexHLSDRMManager_h

#include "nexplayer_Callback.h"

// IS_SUPPORT_KEY's return value will be..
#define SUPPORTING 0 // which means it will decrypt here.
#define NOT_SUPPORTING 1 // which means it will decrypted by nxprotocol.
/** 
 * \defgroup enum Enumerations
 * \defgroup apifunc C-level API Functions
 */

 /**
  * \ingroup enum
  * @brief This is an Enumeration for indicating what pFunc is.
  * If you want to use NexHLSDRM, then you MUST implement  NEXPLAYERHLSIsSupportKeyCallbackFunc into your DRM module, and return SUPPORTING if you can handle that HLS link.
  * If not, then you have to return NOT_SUPPORTING and NexPlayer will handle it.
  */
typedef enum {
	// For NEXPLAYERGetKeyExtCallbackFunc
	IS_SUPPORT_KEY_EXT_KEY		= 0,
	GET_KEY_EXT 				= 1,
	
	// For NEXPLAYERHLSAES128DescrambleCallbackFunc or NEXPLAYERHLSAES128DescrambleWithByteRangeCallbackFunc
	IS_SUPPORT_KEY_AES			= 10,
	AES128DEC					= 11,
	AES128BRDEC					= 12,
	
	// For NEXPLAYERHLSSampleEncDecryptSampleFunc
	IS_SUPPORT_KEY_SAMPLE_ENC	= 20,
	SAMPLE_ENC_DEC_SAMPLE_ENC	= 21,
	ENC_PREP_KEY_SAMPLE_ENC		= 22,
	
	// For WVDRM
	IS_SUPPORT_KEY_WV		= 30,
	SAMPLE_ENC_DEC_WV		= 31,
	ENC_PREP_KEY_WV			= 32,
	MSG_FROM_EXT_WV			= 33
}NEXHLSDRM_CALLBACK_INDEX;

#ifdef __cplusplus
extern "C"
{
#endif
	
/**
 *
 *  \ingroup apifunc
 *  \brief Registers a callback function which is supported by NexHLSDRM.
 *
 * \param[in] what			Callback index which will be added.
 * \param[in] pFunc			Callback function to register.
 * \param[in] pUserData		Additional data to pass to a callback function when it is called.
 *
 * \returns
 *     - 0 if the operation succeeded.
 *     - a non-zero error code if the operation failed.
 *
 * \since version 6.6
 */
int NexHLSDRM_AddCallback(NEXHLSDRM_CALLBACK_INDEX what, void* pFunc, void* pUserData);

/**
 * For internal use only. Please do not use.
 */
int NexHLSDRM_AddCallback_Multi(NEXHLSDRM_CALLBACK_INDEX what, void *pFunc, void *pNexPlayerInstance, void *pUserData);
	
#ifdef __cplusplus
}
#endif
#endif /* NexHLSDRMManager_h */
