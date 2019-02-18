/**
 *  \file nexHD_Callback.h
 *  \brief Header file for definitions related to HTTP Downloader callback functions in the NexPlayer&trade;&nbsp;.
 */
#include "NexTypeDef.h"

#ifndef _NEXHD_CALLBACK_H_
#define _NEXHD_CALLBACK_H_


#ifdef __cplusplus
extern "C"
{
#endif

/**   \defgroup hdcbtypes  HTTP Downloader Callback Types
 *  Callback function typedefs that are part of the HTTP Downloader module in the NexPlayer&trade;&nbsp;API.
 */

/** \defgroup hdtypes  HTTP Downloader Types
 *  Types that are part of the HTTP Downloader module in the NexPlayer&trade;&nbsp;SDK. */

/**
 * \ingroup hdtypes
 * \brief  This is a pointer to the handle of NexPlayer&trade;&nbsp;HTTP stack callback.
 *
 * \since version 6.26
 */
typedef NXVOID*									NEXHD_CALLBACK_HANDLE;

/**
 * \ingroup hdtypes
 * \brief  This is a type definition for NexPlayer&trade;&nbsp;HTTP stack callback message ID.
 *
 * \since version 6.26
 */
typedef NXUINT32								NEXHD_CALLBACK_MSG_ID;

/**
 * \ingroup hdtypes
 * \brief       A structure holding NexPlayer&trade;&nbsp;HTTP stack Send Message callback parameters.
 *
 * \param pMsg                       Complete request header and body.
 * \param uMsgSize                   Byte length of \c pMsg.
 * \param pUrl                       Absolute url including the scheme. (ex: https://abc.com/a/temp.file)
 * \param qwRangeFrom                NEXHD_INVALID_VALUE: Ignore qwRangeFrom.
 * \param qwRangeTo                  NEXHD_INVALID_VALUE: Ignore qwRangeTo.
 * \param uConnectionTime            Socket connection timeout [msec].
 * \param uDownloadTimeout           Download timeout [msec].
 * \param uRecvTimeout               Data receive timeout [msec].
 * \param pProxyAddr                 Proxy server address. Set NULL if proxy server isn't needed.
 * \param uProxyPort                 Proxy server port number.
 * \param bRetryToOriginOnProxyError \c TRUE: If failed to connect to the proxy server,
 *                                   then automatically retry to connect to the original server.
 *
 * \since version 6.26
 */
typedef struct
{
	NXCHAR*										pMsg;							// Complete request header and body.
	NXUINT32									uMsgSize;						// Byte length of pMsg.

	NXCHAR*										pUrl;							// Absolute url including the scheme. (ex: https://abc.com/a/temp.file)
	NXUINT64									qwRangeFrom;					// NEXHD_INVALID_VALUE: Ignore qwRangeFrom.
	NXUINT64									qwRangeTo;						// NEXHD_INVALID_VALUE: Ignore qwRangeTo.

	// Property.
	NXUINT32									uConnectionTimeout;				// [msec]
	NXUINT32									uDownloadTimeout;				// [msec]
	NXUINT32									uRecvTimeout;			// [msec]

	NXCHAR*										pProxyAddr;						// NULL: No proxy server.
	NXUINT32									uProxyPort;
	NXBOOL										bRetryToOriginOnProxyError;		// TRUE: If failed to connect to the proxy server, then automatically retry to connect to the original server.
} NEXHD_CALLBACK_SENDMSG_PARAM;

/**
 * \ingroup hdtypes
 * \brief This enumeration defines the possible error codes of the \c nexHTTPDownloader callback.
 *
 * \since version 6.26
 */
typedef enum
{
	eNEXHD_CALLBACK_ERRCODE_SUCCESS				= 0x00000000,
	eNEXHD_CALLBACK_ERRCODE_GENERAL_ERROR		= 0x00000001,
	eNEXHD_CALLBACK_ERRCODE_INVALID_HANDLE		= 0x00000002,
	eNEXHD_CALLBACK_ERRCODE_INVALID_MSG_ID		= 0x00000003,
	eNEXHD_CALLBACK_ERRCODE_INVALID_PARAMETER	= 0x00000004,
	eNEXHD_CALLBACK_ERRCODE_ABNORMAL_OPERATION	= 0x00000021,
	eNEXHD_CALLBACK_ERRCODE_MEMORY_FAIL			= 0x00000111,		// Memory allocation failed.
	eNEXHD_CALLBACK_ERRCODE_RESOURCE_FAIL		= 0x00000112,		// Failed to create Resource. (Semaphore, Mutex)
	eNEXHD_CALLBACK_ERRCODE_CALCELED			= 0x00000201,
	eNEXHD_CALLBACK_ERRCODE_TIMEOUT_CONNECT		= 0x00000311,
	eNEXHD_CALLBACK_ERRCODE_TIMEOUT_DOWNLOAD	= 0x00000312,		// Timeout during overall download.
	eNEXHD_CALLBACK_ERRCODE_TIMEOUT_RECV		= 0x00000313,		// Timeout during each data recv.
	eNEXHD_CALLBACK_ERRCODE_SOCKET_ERROR		= 0x00000411,		// General socket error not identified by defined error codes.
	eNEXHD_CALLBACK_ERRCODE_SOCKET_OPEN_FAIL	= 0x00000421,
	eNEXHD_CALLBACK_ERRCODE_CONNECT_FAIL		= 0x00000431,
	eNEXHD_CALLBACK_ERRCODE_DNS_FAIL			= 0x00000433,
	eNEXHD_CALLBACK_ERRCODE_SSL_CERT_FAIL		= 0x00000451,
	eNEXHD_CALLBACK_ERRCODE_CONNECTION_CLOSE	= 0x00000511,
	eNEXHD_CALLBACK_ERRCODE_SEND_FAIL			= 0x00000521,
	eNEXHD_CALLBACK_ERRCODE_RECV_FAIL			= 0x00000531,
} NEXHD_CALLBACK_ERRCODE;

/**
 * \ingroup hdcbtypes
 * \brief Callback function to receive HTTP header.
 *
 * \param[in] a_pHandle      NexPlayer&trade;&nbsp;HTTP stack callback handle.
 * \param[in] a_uMsgID       Handle for the specific message.
 * \param[in] a_pHeader	     HTTP header data.
 * \param[in] a_uHeaderSize  HTTP header data size.
 * \param[in] a_pUserData    The user data passed when the callback was originally registered.
 *
 * \returns  Zero if successful otherwise a non-zero error code.
 *
 * \since version 6.26
 */
typedef NEXHD_CALLBACK_ERRCODE (*NEXHD_CALLBACK_HEADER_RECV)	(NEXHD_CALLBACK_HANDLE a_pHandle, NEXHD_CALLBACK_MSG_ID a_uMsgID, NXCHAR* a_pHeader, NXUINT32 a_uHeaderSize, NXVOID* a_pUserData);

/**
 * \ingroup hdcbtypes
 * \brief Callback function to receive HTTP data.
 *
 * \param[in] a_pHandle      NexPlayer&trade;&nbsp;HTTP stack callback handle.
 * \param[in] a_uMsgID       Handle for the specific message.
 * \param[in] a_pData	     HTTP data.
 * \param[in] a_uHeaderSize  HTTP data size.
 * \param[in] a_pUserData    The user data passed when the callback was originally registered.
 *
 * \returns  Zero if successful otherwise a non-zero error code.
 *
 * \since version 6.26
 */
typedef NEXHD_CALLBACK_ERRCODE (*NEXHD_CALLBACK_DATA_RECV)		(NEXHD_CALLBACK_HANDLE a_pHandle, NEXHD_CALLBACK_MSG_ID a_uMsgID, NXUINT8* a_pData, NXUINT32 a_uDataSize, NXVOID* a_pUserData);

/**
 * \ingroup hdcbtypes
 * \brief Callback function to receive HTTP data finish.
 *
 * \param[in] a_pHandle      NexPlayer&trade;&nbsp;HTTP stack callback handle.
 * \param[in] a_uMsgID       Handle for the specific message.
 * \param[in] a_eResult	     Result of data receive process.
 * \param[in] a_pUserData    The user data passed when the callback was originally registered.
 *
 * \returns  Zero if successful otherwise a non-zero error code.
 *
 * \since version 6.26
 */
typedef NEXHD_CALLBACK_ERRCODE (*NEXHD_CALLBACK_RECV_FINISH)	(NEXHD_CALLBACK_HANDLE a_pHandle, NEXHD_CALLBACK_MSG_ID a_uMsgID, NEXHD_CALLBACK_ERRCODE a_eResult, NXVOID* a_pUserData);

/**
 * \ingroup hdtypes
 * \brief       A structure holding the NexPlayer&trade;&nbsp;HTTP stack callback functions.
 *
 * \param pHeaderRecvCB              HTTP Header receive Callback.
 * \param pDataRecvCB                HTTP Data receive Callback.
 * \param pRecvFinishCB              HTTP receive finish Callback.
 * \param pUserData					 The user data that will be passed (in the \c a_pUserData parameter) when each callback is called.
 *
 * \since version 6.26
*/
typedef struct
{
	NEXHD_CALLBACK_HEADER_RECV					pHeaderRecvCB;
	NEXHD_CALLBACK_DATA_RECV					pDataRecvCB;
	NEXHD_CALLBACK_RECV_FINISH					pRecvFinishCB;
	NXVOID										*pUserData;
} NEXHD_CALLBACK_SENDMSG_CBLIST;

/**
 * \ingroup hdtypes
 * \brief This enumeration defines the SetInfo types.
 *
 * \since version 6.26
 */
typedef enum
{
	eNEXHD_CALLBACK_SETINFO_RESERVED				= 0x00000000,

	// Static information.
	eNEXHD_CALLBACK_SETINFO_CONNECT					= 0x00003101,
	eNEXHD_CALLBACK_SETINFO_CONNECTED				= 0x00003102,
	eNEXHD_CALLBACK_SETINFO_MSG_SENT				= 0x00003104
} NEXHD_CALLBACK_SETINFO_TYPE;

/**
 * \ingroup hdtypes
 * \brief       A structure holding reserved data for the SetInfo callback.
 *
 * \note        This structure is not currently used.
 *
 * \since version 6.26
*/
typedef struct
{
	NXUINT32						uReserved;
} NEXHD_CALLBACK_SETINFO_RESERVED;

/**
 * \ingroup hdtypes
 * \brief       A structure holding the connect callback parameters.
 *
 * \param[in] uMsgID       Handle for the specific message.
 * \param[in] pUrl         Server Url
 * \param[in] pAddr        Server Address
 * \param[in] uPort        Server port
 *
 * \since version 6.26
 */
typedef struct
{
	NEXHD_CALLBACK_MSG_ID			uMsgID;		// IN
	NXCHAR							*pUrl;		// IN
	NXCHAR							*pAddr;		// IN
	NXUINT32						uPort;		// IN
} NEXHD_CALLBACK_PARAM_CONNECT;		// eNEXHD_CALLBACK_SETINFO_RESERVED

/**
 * \ingroup hdcbtypes
 * \brief Callback function to indicate the status of the server connection.
 *
 * \param[in] a_pParam       Connect callback parameter.
 * \param[in] a_pUserData    The user data passed when the callback was originally registered.
 *
 * \returns  Zero if successful otherwise a non-zero error code.
 *
 * \since version 6.26
 */
typedef NEXHD_CALLBACK_ERRCODE (*NEXHD_CALLBACK_CONNECT)(NEXHD_CALLBACK_PARAM_CONNECT *a_pParam, NXVOID *a_pUserData);

/**
 * \ingroup hdtypes
 * \brief       A structure holding SetInfo connect callback information.
 *
 * \param[in] pCB       Callback function to register.
 * \param[in] a_pUserData    The user data passed when the callback was originally registered.
 *
 * \since version 6.26
*/
typedef struct
{
	NEXHD_CALLBACK_CONNECT			pCB;
	NXVOID							*pUserData;
} NEXHD_CALLBACK_SETINFO_CONNECT;	// eNEXHD_CALLBACK_SETINFO_CB_CONNECT

/**
 * \ingroup hdtypes
 * \brief       A structure holding the connected callback parameters.
 *
 * \param[in] uMsgID       Handle for the specific message.
 * \param[in] pUrl         The server URL
 * \param[in] pAddr        The server address
 * \param[in] uPort        The server port
 *
 * \since version 6.26
*/
typedef struct
{
	NEXHD_CALLBACK_MSG_ID			uMsgID;		// IN
	NXCHAR							*pUrl;		// IN
	NXCHAR							*pAddr;		// IN
	NXUINT32						uPort;		// IN
} NEXHD_CALLBACK_PARAM_CONNECTED;

/**
 * \ingroup hdcbtypes
 * \brief Callback function to indicate the status of the server connection.
 *
 * \param[in] a_pParam       Connected callback parameter.
 * \param[in] a_pUserData    The user data passed when the callback was originally registered.
 *
 * \returns  Zero if successful otherwise a non-zero error code.
 *
 * \since version 6.26
 */
typedef NEXHD_CALLBACK_ERRCODE (*NEXHD_CALLBACK_CONNECTED)(NEXHD_CALLBACK_PARAM_CONNECTED *a_pParam, NXVOID *a_pUserData);

/**
 * \ingroup hdtypes
 * \brief       A structure holding the SetInfo connected callback information.
 *
 * \param[in] pCB            Callback function to register.
 * \param[in] a_pUserData    The user data passed when the callback was originally registered.
 *
 * \since version 6.26
 */
typedef struct
{
	NEXHD_CALLBACK_CONNECTED		pCB;
	NXVOID							*pUserData;
} NEXHD_CALLBACK_SETINFO_CONNECTED;	// eNEXHD_CALLBACK_SETINFO_CB_CONNECTED

/**
 * \ingroup hdtypes
 * \brief       A structure holding the message sent callback parameters.
 *
 * \param[in] uMsgID       A handle for the specific message.
 * \param[in] pUrl         The URL of the server.
 * \param[in] pMsg         The message sent.
 * \param[in] uMsgSize     The size of the message sent.
 *
 * \since version 6.26
*/
typedef struct
{
	NEXHD_CALLBACK_MSG_ID			uMsgID;		// IN
	NXCHAR							*pUrl;		// IN
	NXCHAR							*pMsg;		// IN
	NXUINT32						uMsgSize;	// IN
} NEXHD_CALLBACK_PARAM_MSG_SENT;

/**
 * \ingroup hdcbtypes
 * \brief Callback function to indicate the status of the message sent.
 *
 * \param[in] a_pParam       Connected callback parameter.
 * \param[in] a_pUserData    The user data passed when the callback was originally registered.
 *
 * \returns  Zero if successful otherwise a non-zero error code.
 *
 * \since version 6.26
 */
typedef NEXHD_CALLBACK_ERRCODE (*NEXHD_CALLBACK_MSG_SENT)(NEXHD_CALLBACK_PARAM_MSG_SENT *a_pParam, NXVOID *a_pUserData);

/**
 * \ingroup hdtypes
 * \brief       A structure holding the SetInfo message sent callback information.
 *
 * \param[in] pCB            Callback function to register.
 * \param[in] a_pUserData    The user data passed when the callback was originally registered.
 *
 * \since version 6.26
*/
typedef struct
{
	NEXHD_CALLBACK_MSG_SENT			pCB;
	NXVOID							*pUserData;
} NEXHD_CALLBACK_SETINFO_MSG_SENT;	// eNEXHD_CALLBACK_SETINFO_CB_MSG_SENT

/**
 * \ingroup hdtypes
 * \brief       A union of the SetInfo callback information.
 *
 * \param stReserved       Reserved data.
 * \param stCBConnect      Server connect callback information.
 * \param stCBConnected    Server connected callback information.
 * \param stMsgSent        Message sent callback information.
 *
 * \since version 6.26
 */
typedef union
{
	NEXHD_CALLBACK_SETINFO_RESERVED					stReserved;				// eNEXHD_CALLBACK_SETINFO_RESERVED
	NEXHD_CALLBACK_SETINFO_CONNECT					stCBConnect;			// eNEXHD_CALLBACK_SETINFO_CONNECT
	NEXHD_CALLBACK_SETINFO_CONNECTED				stCBConnected;			// eNEXHD_CALLBACK_SETINFO_CONNECTED
	NEXHD_CALLBACK_SETINFO_MSG_SENT					stCBMsgSent;			// eNEXHD_CALLBACK_SETINFO_MSG_SENT
} NEXHD_CALLBACK_SETINFO_PARCEL;

/**
 * \ingroup hdtypes
 * \brief       An enumeration for future use.
 */
typedef enum
{
	eNEXHD_CALLBACK_GETINFO_RESERVED				= 0x00000000,
} NEXHD_CALLBACK_GETINFO_TYPE;

/**
 * \ingroup hdtypes
 * \brief       A structure for future use.
 */
typedef struct
{
	NXUINT32						uReserved;
} NEXHD_CALLBACK_GETINFO_RESERVED;	// eNEXHD_CALLBACK_GETINFO_RESERVED

/**
 * \ingroup hdtypes
 * \brief       A union for future use.
 */
typedef union
{
	NEXHD_CALLBACK_GETINFO_RESERVED	stReserved;		// eNEXHD_CALLBACK_SETINFO_RESERVED
} NEXHD_CALLBACK_GETINFO_PARCEL;



#ifdef __cplusplus
}
#endif

#endif  // _NEXPLAYER_JNI_H_
