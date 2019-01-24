/** \file nexplayer_jni.h
 *  \brief Main header file for the NexPlayer&trade;&nbsp;DRM API on Android
 */

#ifndef _NEXPLAYER_JNI_H_
#define _NEXPLAYER_JNI_H_

#include "nexplayer_Callback.h"

/**
 * \mainpage NexPlayer&trade;&nbsp;SDK&nbsp;for&nbsp;Android&nbsp;Native&nbsp;Interface&nbsp;Registration&nbsp;API
 *
 * \section legal Legal Notices
 *
 * \par Disclaimer for Intellectual property
 * <i>This product is designed for general purpose, and accordingly the customer is
 * responsible for all or any of intellectual property licenses required for
 * actual application. NexStreaming Corp. does not provide any
 * indemnification for any intellectual properties owned by third party.</i>
 *
 * \par Copyright
 * Copyright for all documents, drawings and programs related with this
 * specification are owned by NexStreaming Corp. All or any part of the
 * specification shall not be reproduced nor distributed without prior written
 * approval by NexStreaming Corp. Content and configuration of all or any
 * part of the specification shall not be modified nor distributed without prior
 * written approval by NexStreaming Corp.
 *
 * \par
 * &copy; Copyright 2010-2015 NexStreaming Corp. All rights reserved.
 *
 * \section abstract Abstract
 *
 * This document describes the NexPlayer&trade;&nbsp;SDK for Android <em>Native Interface Registration API</em>.  It was formerly called the NexPlayer&trade;&nbsp;DRM API but the name
 * has changed to more accurately reflect the information included in the document, which includes not only DRM API related content but also other content related to the native interface. 
 *
 * This is a JNI (Java Native Interface) API that allows applications to perform
 * DRM descrambling.  The main Java API for the NexPlayer&trade;&nbsp;engine is
 * documented separately.
 *
 * To see how this interface can be implemented to perform DRM descrambling, please
 * see the included sample code for more details.
 *
 * \section overview DRM Descrambling Overview
 *
 * NexPlayer&trade;&nbsp;supports DRM descrambling by allowing the application to
 * register one or more callback functions.  Each callback receives pointers to
 * input and output buffers; it descrambles the data in the input buffer and
 * places the descrambled data in the output buffer.
 *
 * Different callbacks can be registered to allow access to data at different
 * points in the decoding process.  The exact point at which the descrambling must
 * occur differs between the different DRM schemes.  In addition, some callbacks
 * provide additional parameters necessary for a given type of DRM.  See the
 * individual function descriptions for more information.
 *
 * The normal flow is as follows:
 * -# Your application (written in Java) calls your JNI library.
 * -# Your JNI library in turn calls the appropriate NexPlayer&trade;&nbsp;API
 *    functions to register the necessary callbacks.
 * -# Your application calls the NexPlayer&trade;&nbsp;API normally to play
 *    content; when necessary, the player will automatically call the registered
 *    callbacks to descramble the content.
 *
 * <b>CAUTION:</b> In many cases, the input and output buffer pointers point
 * to the same location.  Your code should be able to handle cases where they
 * point to the same location, and cases when they are different.  For example
 * a typical no-op descrambling function that just outputs what it is given
 * as input might be written as follows:
 *
 * \code
 * static int wmdrmDescrambleCallbackFunc(unsigned char*    pInputBuffer,
 *                                        unsigned int      uiInputBufferSize,
 *                                        unsigned char*    pOutputBuffer,
 *                                        unsigned int*     puiOutputBufferSize,
 *                                        void *            pUserData)
 * {
 *  if(pInputBuffer == pOutputBuffer) {
 *      *puiOutputBufferSize = uiInputBufferSize;
 *  } else {
 *      *puiOutputBufferSize = uiInputBufferSize;
 *      memcpy(pOutputBuffer, pInputBuffer, uiInputBufferSize);
 *  }
 *  return 0;
 * }
 *
 * \endcode
 *
 * \see ::nexPlayerSWP_RegisterDRMDescrambleCallBackFunc for general DRM
 * \see ::nexPlayerSWP_RegisterWMDRMDescrambleCallBackFunc for WM-DRM
 * \see ::nexPlayerSWP_RegisterHLSTSDescrambleCallBackFunc for HLS/TS DRM
 * \see ::nexPlayerSWP_RegisterSmoothStreamFragmentDescrambleCallBackFunc for Smooth Streaming fragment-based DRM
 * \see ::nexPlayerSWP_RegisterSmoothStreamPlayReadyDescrambleCallBackFunc for Smooth Streaming PlayReady DRM
 * \see ::nexPlayerSWP_RegisterPiffPlayReadyDescrambleCallBackFunc for PIFF PlayReady DRM
 * \see ::nexPlayerSWP_RegisterAsfPlayReadyDescrambleCallBackFunc for ASF PlayReady DRM
 * \see ::nexPlayerSWP_RegisterMPDDescrambleCallbackFunc for encrypted manifests and playlists
 * \see ::nexPlayerSWP_RegisterDeceUVDescrambleCallBackFunc for DECE UV DRM (with CFF content)
 *
 * \section rfio Remote File I/O
 *
 * NexPlayer&trade;&nbsp;also provides remote file I/O callbacks that allow
 * an application to provide custom open, close, read, and write implementations.
 * This allows an application to retrieve the file data from another source, or
 * to perform DRM descrambling on the data as it is read.
 *
 * \see ::nexPlayerSWP_RegisterRemoteFileIOInterface for details
 *
 * \section include Header File
 *
 * The main header file for the NexPlayer&trade;&nbsp;DRM API on Android is
 * <i>nexplayer_jni.h</i>, which you should include anywhere you need to
 * call the callback registration functions:
 *
 * \code
 * #include "nexplayer_jni.h"
 * \endcode
 *
 *
 * \section dllsec Dynamic Library Security
 *
 * NexPlayer&trade;&nbsp;uses a series of dynamic libraries, in order to allow unnecessary components to be
 * removed from an application, thus reducing the size of the application.
 *
 * Because these libraries are loaded dynamically, a user may attempt to gain access to decrypted data
 * by replacing these dynamic libraries.
 *
 * In order to prevent tampering, applications that support DRM should verify that the libraries being
 * loaded are the correct libraries that were distributed with the application.
 *
 * In order to accomplish this, an application can register callback functions which are used in place of the
 * normal system calls to load the dynamic libraries.  This allows the application to verify that the
 * correct libraries are present before calling through to the system to perform the dynamic load.
 *
 * These callbacks are registered using ::nexPlayerSWP_RegisterDLAPICallbackFunc.
 *
 * \see ::nexPlayerSWP_RegisterDLAPICallbackFunc for details.
 *
 */

#ifdef __cplusplus
extern "C"
{
#endif


    /**
     * \ingroup     ppmacro
     * \brief       Payload DRM type
     *
     * This is a possible value that can be used when calling ::nexPlayerSWP_RegisterWMDRMDescrambleCallBackFunc
     */
#define NXWMDRM_PAYLOAD_TYPE    0x01

    /**
     * \ingroup     ppmacro
     * \brief       Packet DRM type
     *
     * This is a possible value that can be used when calling ::nexPlayerSWP_RegisterWMDRMDescrambleCallBackFunc
     */
#define NXWMDRM_PACKET_TYPE     0x10

    /**
     * \ingroup     ppmacro
     * \brief       Frame DRM type
     *
     * This is a possible value that can be used when calling ::nexPlayerSWP_RegisterWMDRMDescrambleCallBackFunc
     */
#define NXWMDRM_FRAME_TYPE      0x20


    /**
     * \ingroup apifunc
     * \brief Registers a general DRM descrambling callback function.
     *
     * \param[in]  pDescrambleCallbackFunc  Callback function to register.
     *
     * \param[in]  pUserData                Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERDRMDescrambleCallbackFunc NEXPLAYERDRMDescrambleCallbackFunc\endlink
     */
    int nexPlayerSWP_RegisterDRMDescrambleCallBackFunc(NEXPLAYERDRMDescrambleCallbackFunc pDescrambleCallbackFunc,
                                                       void *pUserData);

    /**
     * \ingroup apifunc
     * \brief Registers a WMDRM descrambling callback function.
     *
     * \param[in] uiDRMType
     *              The type of DRM. This is one of the following constants:
     *              - ::NXWMDRM_PAYLOAD_TYPE <b>(0x01)</b>
     *              - ::NXWMDRM_PACKET_TYPE <b>(0x10)</b>
     *              - ::NXWMDRM_FRAME_TYPE <b>(0x20)</b>
     *
     * \param[in]  pDescrambleCallbackFunc  Callback function to register.
     *
     * \param[in]  pUserData                Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERWMDRMDescrambleCallbackFunc NEXPLAYERWMDRMDescrambleCallbackFunc\endlink
     */
    int nexPlayerSWP_RegisterWMDRMDescrambleCallBackFunc(unsigned int uiDRMType, NEXPLAYERWMDRMDescrambleCallbackFunc pDescrambleCallbackFunc,
                                                         void *pUserData);
    /**
     * \ingroup apifunc
     * \brief Registers an HLS/TS descrambling callback function.
     *
     * \param[in]  pDescrambleCallbackFunc  Callback function to register.
     *
     * \param[in]  pUserData                Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERHLSTSDescrambleCallbackFunc NEXPLAYERHLSTSDescrambleCallbackFunc\endlink
     */
    int nexPlayerSWP_RegisterHLSTSDescrambleCallBackFunc(NEXPLAYERHLSTSDescrambleCallbackFunc pDescrambleCallbackFunc,
                                                         void *pUserData);

    /**
     * \ingroup apifunc
     * \brief Registers a Smooth Streaming fragment descrambling callback function.
     *
     * \param[in]  pDescrambleCallbackFunc  Callback function to register.
     *
     * \param[in]  pUserData                Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERSmoothStreamFragmentDescrambleCallbackFunc NEXPLAYERSmoothStreamFragmentDescrambleCallbackFunc\endlink
     */
    int nexPlayerSWP_RegisterSmoothStreamFragmentDescrambleCallBackFunc(NEXPLAYERSmoothStreamFragmentDescrambleCallbackFunc pDescrambleCallbackFunc,
                                                                        void *pUserData);


    /**
     * \ingroup apifunc
     * \brief Registers a Smooth Streaming PlayReady descrambling callback function.
     *
     * \param[in]  pDescrambleCallbackFunc  Callback function to register.
     *
     * \param[in]  pUserData                Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERSmoothStreamPlayReadyDescrambleCallbackFunc NEXPLAYERSmoothStreamPlayReadyDescrambleCallbackFunc\endlink
     */
    int nexPlayerSWP_RegisterSmoothStreamPlayReadyDescrambleCallBackFunc(NEXPLAYERSmoothStreamPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc,
                                                                         void *pUserData);

    /**
     * \ingroup apifunc
     * \brief Registers a PIFF PlayReady descrambling callback function.
     *
     * \param[in]  pDescrambleCallbackFunc  Callback function to register.
     *
     * \param[in]  pUserData                Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERPiffPlayReadyDescrambleCallbackFunc NEXPLAYERPiffPlayReadyDescrambleCallbackFunc\endlink
     */
    int nexPlayerSWP_RegisterPiffPlayReadyDescrambleCallBackFunc(NEXPLAYERPiffPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc,
                                                                 void *pUserData);


    /**
     * \ingroup apifunc
     * \brief Registers an ASF PlayReady descrambling callback function.
     *
     * \param[in]  pDescrambleCallbackFunc  Callback function to register
     *
     * \param[in]  ulDescrambleType
     *                  The type of DRM. This is one of the following constants:
     *                  - ::NXWMDRM_PAYLOAD_TYPE <b>(0x01)</b>
     *                  - ::NXWMDRM_PACKET_TYPE <b>(0x10)</b>
     *                  - ::NXWMDRM_FRAME_TYPE <b>(0x20)</b>
     *
     * \param[in]  pUserData                Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERAsfPlayReadyDescrambleCallbackFunc NEXPLAYERAsfPlayReadyDescrambleCallbackFunc\endlink
     */
    int nexPlayerSWP_RegisterAsfPlayReadyDescrambleCallBackFunc(NEXPLAYERAsfPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc,
                                                                unsigned long ulDescrambleType,
                                                                void *pUserData);
	
	/**
	 * \ingroup apifunc
	 * \brief Registers a callback function that retrieves an encryption key from an HLS playlist over HTTPS to be passed
	 *        to NexPlayer&trade;&nbsp;for descrambling.
	 *.
	 * \param[in]	pGetKeyExtFunc	Callback function to register.
	 * \param[in]	pUserData		Additional data to pass to callback function when it is called.
	 *
	 * \returns
	 *     - 0 if the operation succeeded.
	 *     - a non-zero error code if the operation failed.
	 *
	 * \since version 6.2.2
	 * \see \link Nexplayer_Callback.h#NEXPLAYERGetKeyExtCallbackFunc NEXPLAYERGetKeyExtCallbackFunc\endlink
	 *
	 */
	int nexPlayerSWP_RegisterGetKeyExtCallBackFunc(NEXPLAYERGetKeyExtCallbackFunc pGetKeyExtFunc, void* pUserData);

    /**
     * \ingroup apifunc
     * \brief Registers a set of callback functions for Remote File I/O.
     *
     * See \ref rfio for more information.
     *
     * \param[in]  pRemoteFileIOInterface   Structure containing pointers to functions to register.
     *
     * \param[in]  pUserData                Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link Nexplayer_Callback.h#NEXPLAYERRemoteFile_OpenFt NEXPLAYERRemoteFile_OpenFt\endlink
     * \see \link Nexplayer_Callback.h#NEXPLAYERRemoteFile_CloseFt NEXPLAYERRemoteFile_CloseFt\endlink
     * \see \link Nexplayer_Callback.h#NEXPLAYERRemoteFile_ReadFt NEXPLAYERRemoteFile_ReadFt\endlink
     * \see \link Nexplayer_Callback.h#NEXPLAYERRemoteFile_Read64Ft NEXPLAYERRemoteFile_Read64Ft\endlink
     * \see \link Nexplayer_Callback.h#NEXPLAYERRemoteFile_SeekFt NEXPLAYERRemoteFile_SeekFt\endlink
     * \see \link Nexplayer_Callback.h#NEXPLAYERRemoteFile_Seek64Ft NEXPLAYERRemoteFile_Seek64Ft\endlink
     * \see \link Nexplayer_Callback.h#NEXPLAYERRemoteFile_WriteFt NEXPLAYERRemoteFile_WriteFt\endlink
     * \see \link Nexplayer_Callback.h#NEXPLAYERRemoteFile_SizeFt NEXPLAYERRemoteFile_SizeFt\endlink
     */
    int nexPlayerSWP_RegisterRemoteFileIOInterface(NEXPLAYERRemoteFileIOInterface *pRemoteFileIOInterface, void *pUserData);  

/**
 * \ingroup apifunc
 * \brief Registers a set of callback functions for the NexPlayer&trade;&nbsp;HTTP stack.
 *
 * \param	hEngine	                      The handle of the NexPlayer&trade;&nbsp;engine.
 * \param[in]  	pNexHTTPDownloaderInterface   Structure containing pointers for functions to register.
 * \param[in]  	pUserData                     Additional data to pass to callback function when it is called.
 *
 * \see  \link nexplayer_Callback.h#NEXPLAYERNexHTTPDownloaderInterface NEXPLAYERNexHTTPDownloaderInterface\endlink
 *
 * \returns
 *     - 0 if the operation succeeded.
 *     - a non-zero error code if the operation failed.
 *
 * \since version 6.23
 */
    int nexPlayerSWP_RegisterNexHTTPDownloaderInterface(NEXPLAYERNexHTTPDownloaderInterface *pNexHTTPDownloaerInterface, void *pUserData); 

    /**
     * \ingroup apifunc
     * \brief Registers a set of callback functions to use in place of the normal dynamic library system calls.
     *
     * NexPlayer&trade;&nbsp;uses a series of dynamic libraries, in order to allow unnecessary components to be
     * removed from an application, thus reducing the size of the application.  In order to prevent tampering,
     * DRM solutions can handle the loading of this dynamic libraries themselves, allowing verification of the
     * library.
     *
     * To facilitate this, NexPlayer&trade;&nbsp;allows applications to substitute their own functions for
     * loading and managing dynamic libraries.  These callbacks use exactly the same interface as the
     * normal system calls.
     *
     * \param[in]  pDLOpenFunc    Pointer to replacement function for \c dlopen
     *
     * \param[in]  pDLSymFunc     Pointer to replacement function for \c dlsym
     *
     * \param[in]  pDLCloseFunc   Pointer to replacement function for \c dlclose
     *
     * \param[in]  pDLErrorFunc   Pointer to replacement function for \c dlerror
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERDLOpenCallbackFunc NEXPLAYERDLOpenCallbackFunc\endlink
     * \see \link nexplayer_Callback.h#NEXPLAYERDLSymCallbackFunc NEXPLAYERDLSymCallbackFunc\endlink
     * \see \link nexplayer_Callback.h#NEXPLAYERDLCloseCallbackFunc NEXPLAYERDLCloseCallbackFunc\endlink
     * \see \link nexplayer_Callback.h#NEXPLAYERDLErrorCallbackFunc NEXPLAYERDLErrorCallbackFunc\endlink
     *
     */
    int nexPlayerSWP_RegisterDLAPICallbackFunc( NEXPLAYERDLOpenCallbackFunc pDLOpenFunc, NEXPLAYERDLSymCallbackFunc pDLSymFunc, NEXPLAYERDLCloseCallbackFunc pDLCloseFunc, NEXPLAYERDLErrorCallbackFunc pDLErrorFunc);



    /**
     * \ingroup apifunc
     * \brief Registers a callback function to receive a pointer to set HTTP authorization information.
     *
     *  This register replaces the \c GetHttpCredential callback function used in previous versions of the NexPlayer&trade;&nbsp;SDK,
     *  which only handled HTTP 401 requests.
     *
     *  This function allows the player to handle different HTTP authentication failure status codes
     *  (HTTP 401 or HTTP 407) individually and to set specific authorization information to respond
     *  to these server requests.
     *
     *
     * \param[in] pGetHttpAuthInfofunc	Callback function to register.
     *
     * \param[in] pUserData		Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERGetHttpAuthInfoCallbackFunc NEXPLAYERGetHttpAuthInfoCallbackFunc\endlink
     */

    int nexPlayerSWP_RegisterGetHttpAuthInfoCallbackFunc(NEXPLAYERGetHttpAuthInfoCallbackFunc pGetHttpAuthInfofunc, void * pUserData);

    /**
     * \ingroup apifunc
     * \brief Registers a callback function to receive HLS playlist contents every time a new HLS playlist is received.
     *
     * \param[in]  pGetPlaylistInfoFunc     Callback function to register.
     *
     * \param[in]  pUserData                Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERGetPlaylistInfoCallbackFunc NEXPLAYERGetPlaylistInfoCallbackFunc\endlink
     */
    int nexPlayerSWP_RegisterGetPlaylistInfoCallBackFunc(NEXPLAYERGetPlaylistInfoCallbackFunc pGetPlaylistInfoFunc, void *pUserData);

    /**
     *  \ingroup apifunc
     *  \brief Registers a callback function to receive a pointer for Progressive Download(PD) block and block's size.
     *         PD data is decrypted using this information.
     *
     * \param[in]  pGetPDBlockFunc    Callback function to register.
     *
     * \param[in]  pUserData          Additional data to pass to callback function when it is called.
     *
     *\returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERGetPDBlockCallbackFunc NEXPLAYERGetPDBlockCallbackFunc\endlink
     */
    int nexPlayerSWP_RegisterGetPDBlockCallBackFunc(NEXPLAYERGetPDBlockCallbackFunc pGetPDBlockFunc, void* pUserData);

	/**
     *  \ingroup apifunc
     *  \brief Registers a callback function to receive a pointer of Progressive Download(PD) Header and header's size. Header data is parsed using this information.
     *
     * \param[in]  pPDEnvelopHeaderParsingCallbackFunc    Callback function to register.
     *
     * \param[in]  pUserData          Additional data to pass to the callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERPDEnvelopHeaderParsingCallbackFunc NEXPLAYERPDEnvelopHeaderParsingCallbackFunc\endlink
     */
	int nexPlayerSWP_RegisterPDEnvelopHeaderParsingCallBackFunc(NEXPLAYERPDEnvelopHeaderParsingCallbackFunc pPDEnvelopHeaderParsingCallbackFunc, void* pUserData);

    /**
     * \ingroup apifunc
     * \brief Registers a callback function to receive a pointer to handle encrypted manifests or playlists.
     *
     * The related callback function is called when the top level playlist or manifest is received, and
     * if decryption is required, it handles the relevant callbacks.
     *
     * Please see the sample code for additional information on how to use these callbacks.
     *
     *
     * \param[in] pMPDDescrambleFunc	Callback function to register.
     *
     * \param[in] pUserData				Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERMPDDescrambleCallbackFunc NEXPLAYERMPDDescrambleCallbackFunc\endlink
     */
    int nexPlayerSWP_RegisterMPDDescrambleCallbackFunc(NEXPLAYERMPDDescrambleCallbackFunc pMPDDescrambleFunc, void* pUserData);


    /**
     * \ingroup apifunc
     * \brief Registers a DECE UV(Ultra Violet) descrambling callback function for CFF(Common File Format) content.
     * 
     * \param[in]  pDescrambleCallbackFunc  Callback function to register.
     *
     * \param[in]  pUserData                Additional data to pass to callback function when it is called.
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERDeceUVDescrambleCallbackFunc NEXPLAYERDeceUVDescrambleCallbackFunc\endlink
     *
     * \since version 5.11
     */
    int nexPlayerSWP_RegisterDeceUVDescrambleCallBackFunc(NEXPLAYERDeceUVDescrambleCallbackFunc pDescrambleCallbackFunc,
                                                                 void *pUserData);
	
	/**
	 * \ingroup apifunc
     * \brief Registers a callback function to handle AES128 encrypted HLS content.
     *
     * \param[in]	pDecryptSegmentCallbackFunc  Callback function to register.
     * \param[in]	pUserData	Additional data to pass to callback function when it is called.
	 *
     * \see \link nexplayer_Callback.h#NEXPLAYERHLSAES128DescrambleCallbackFunc NEXPLAYERHLSAES128DescrambleCallbackFunc\endlink
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \since version 6.3
	 */
	int nexPlayerSWP_RegisterHLSAES128DescrambleCallBackFunc(NEXPLAYERHLSAES128DescrambleCallbackFunc pDecryptSegmentCallbackFunc, void *pUserData);
	
	/**
     *
     * \ingroup apifunc
     * \brief This registers a callback function to handle AES128 encrypted HLS content with byte range.
     *
     * \param[in]   pHLSAES128DescrambleWithByteRangeCallbackFunc  Callback function to register.
     * \param[in]   pUserData   Additional data to pass to callback function when it is called.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERHLSAES128DescrambleWithByteRangeCallbackFunc NEXPLAYERHLSAES128DescrambleWithByteRangeCallbackFunc\endlink
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \since version 6.51
	 */
	int nexPlayerSWP_RegisterHLSAES128DescrambleWithByteRangeCallBackFunc(NEXPLAYERHLSAES128DescrambleWithByteRangeCallbackFunc pHLSAES128DescrambleWithByteRangeCallbackFunc, void *pUserData);
	
	/**
	 *  \ingroup apifunc
	 *  \brief Registers a callback function that retrieves stored HTTP data for offline playback.
	 *
	 * \param[in] pHTTPRetrieveCallbackFunc  Callback function to register.
	 * \param[in] pUserData					Additional data to pass to callback function when it is called.
	 *
	 * \see \link Nexplayer_Callback.h#NEXPLAYERHTTPRetrieveDataCallbackFunc NEXPLAYERHTTPRetrieveDataCallbackFunc\endlink
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
	 * \since version 6.6
	 */
	int nexPlayerSWP_RegisterHTTPRetrieveDataCallBackFunc(NEXPLAYERHTTPRetrieveDataCallbackFunc pHTTPRetrieveCallbackFunc, void *pUserData);
	
	/**
	 *  \ingroup apifunc
	 *  \brief Registers a callback function that stores received HTTP data for offline playback.
	 *
	 * \param[in] pHTTPStoreCallbackFunc	Callback function to register.
	 * \param[in] pUserData					Additional data to pass to callback function when it is called.
	 *
	 * \see \link nexplayer_Callback.h#NEXPLAYERHTTPStoreDataCallbackFunc NEXPLAYERHTTPStoreDataCallbackFunc\endlink
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
	 * \since version 6.6
	 */
	int nexPlayerSWP_RegisterHTTPStoreDataCallBackFunc(NEXPLAYERHTTPStoreDataCallbackFunc pHTTPStoreCallbackFunc, void *pUserData);

	// Not currently supported.
	int nexPlayerSWP_RegisterDashDRMSessionOpen(NEXPLAYERDashDrmSessionOpenCallbackFunc pOpenCallbackFunc, void* pUserData);

	// Not currently supported.
	int nexPlayerSWP_RegisterDashDRMSessionClose(NEXPLAYERDashDrmSessionCloseCallbackFunc pCloseCallbackFunc, void* pUserData);

	// Not currently supported.
	int nexPlayerSWP_RegisterDashDRMSessionSetCencBox(NEXPLAYERDashDrmSessionSetCencBoxCallbackFunc pSetCencBoxCallbackFunc, void* pUserData);

	// Not currently supported.
	int nexPlayerSWP_RegisterDashDRMSessionDecryptIsobmffFrame(NEXPLAYERDashDrmSessionDecryptIsobmffFrameCallbackFunc pDecryptIsobmffFrameCallbackFunc, void* pUserData);
	
	int nexPlayerSWP_RegisterHLSTSDescrambleWithByteRangeCallBackFunc(NEXPLAYERHLSTSDescrambleWithByteRangeCallbackFunc pHLSTSDecrypWithByteRangeCallbackFunc, void *pUserData);
	
	//internal only
	int nexPlayerSWP_RegisterHLSSampleEncDecryptionSampleCallBackFunc(NEXPLAYERHLSSampleEncDecryptSampleFunc pHLSSampleEncDecryptSample, void *pUserData);
	int nexPlayerSWP_RegisterHLSEncPrepareKeyCallBackFunc(NEXPLAYERHLSEncPrepareKeyFunc pHLSEncPrepareKey, void *pUserData);
	int nexPlayerSWP_RegisterSendMessageToExternalModule(NEXPLAYERSendMessageToExternalModuleFunc pMsgCallback, void *pUserData);
	
    /**
     *
     * \ingroup apifunc
     * \brief Registers a callback function that verifies whether or not a key attribute is supported by the DRM module.
     *
     * \param[in] pHLSIsSupportKey          Callback function to register.
     * \param[in] pUserData                 Additional data to pass to callback function when it is called.
     *
     * \see \link nexplayer_Callback.h#NEXPLAYERHLSIsSupportKeyCallbackFunc NEXPLAYERHLSIsSupportKeyCallbackFunc\endlink
     *
     * \returns
     *     - 0 if the operation succeeded.
     *     - a non-zero error code if the operation failed.
     *
     * \since version 6.49
     */
	int nexPlayerSWP_RegisterHLSIsSupportKeyCallBackFunc(NEXPLAYERHLSIsSupportKeyCallbackFunc pHLSIsSupportKey, void *pUserData);
	
	int nexPlayerSWP_RegisterCENCDecryptionSampleCallBackFunc(NEXPLAYERMediaDrmDecryptSampleCallbackFunc pCENCDecryptSample, void *pUserData);

    int nexPlayerSWP_RegisterMediaDrmCallBackFunc(  NEXPLAYERDrmTypeAcceptedCallbackFunc pDrmTypeAccept, 
                                                    NEXPLAYERInitMediaDrmCallbackFunc pInitMediaDrm,
                                                    NEXPLAYERMediaDrmDecryptSampleCallbackFunc pDecryptSample,
                                                    NEXPLAYERDeinitMediaDrmCallbackFunc pDeinitMediaDrm,
                                                    void *pUserData);

	int nexPlayerSWP_RegisterMediaDrmCallBackFunc_Multi(void* nexPlayerInstance,
		NEXPLAYERDrmTypeAcceptedCallbackFunc pDrmTypeAccept,
		NEXPLAYERInitMediaDrmCallbackFunc pInitMediaDrm,
		NEXPLAYERMediaDrmDecryptSampleCallbackFunc pDecryptSample,
		NEXPLAYERDeinitMediaDrmCallbackFunc pDeinitMediaDrm,
		void *pUserData);
	
	//Multi-Instance
	
   /** 
	* For internal use only. Please do not use.
	*/
	int nexPlayerSWP_RegisterDRMDescrambleCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERDRMDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
    int nexPlayerSWP_RegisterWMDRMDescrambleCallBackFunc_Multi(void* nexPlayerClassInstance, unsigned int uiDRMType, NEXPLAYERWMDRMDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData);
    
   /** 
	* For internal use only. Please do not use.
	*/
	int nexPlayerSWP_RegisterHLSTSDescrambleCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERHLSTSDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
    int nexPlayerSWP_RegisterSmoothStreamFragmentDescrambleCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERSmoothStreamFragmentDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
    int nexPlayerSWP_RegisterSmoothStreamPlayReadyDescrambleCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERSmoothStreamPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData);
    
   /** 
	* For internal use only. Please do not use.
	*/
    int nexPlayerSWP_RegisterPiffPlayReadyDescrambleCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERPiffPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
    int nexPlayerSWP_RegisterAsfPlayReadyDescrambleCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERAsfPlayReadyDescrambleCallbackFunc pDescrambleCallbackFunc, unsigned long ulDescrambleType, void *pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
	int nexPlayerSWP_RegisterGetKeyExtCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERGetKeyExtCallbackFunc pGetKeyExtFunc, void* pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
    int nexPlayerSWP_RegisterRemoteFileIOInterface_Multi(void* nexPlayerClassInstance, NEXPLAYERRemoteFileIOInterface *pRemoteFileIOInterface, void *pUserData);        // JDKIM 2010/08/02
	
   /** 
	* For internal use only. Please do not use.
	*/
    int nexPlayerSWP_RegisterGetHttpAuthInfoCallbackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERGetHttpAuthInfoCallbackFunc pGetHttpAuthInfofunc, void * pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
    int nexPlayerSWP_RegisterGetPlaylistInfoCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERGetPlaylistInfoCallbackFunc pGetPlaylistInfoFunc, void *pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
    int nexPlayerSWP_RegisterGetPDBlockCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERGetPDBlockCallbackFunc pGetPDBlockFunc, void* pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
    int nexPlayerSWP_RegisterMPDDescrambleCallbackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERMPDDescrambleCallbackFunc pMPDDescrambleFunc, void* pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
    int nexPlayerSWP_RegisterDeceUVDescrambleCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERDeceUVDescrambleCallbackFunc pDescrambleCallbackFunc, void *pUserData);
    
   /** 
	* For internal use only. Please do not use.
	*/
	int nexPlayerSWP_RegisterHLSAES128DescrambleCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERHLSAES128DescrambleCallbackFunc pDecryptSegmentCallbackFunc, void *pUserData);
	
   /** 
	 * \For internal use only. Please do not use.
	 */
	int nexPlayerSWP_RegisterHLSAES128DescrambleWithByteRangeCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERHLSAES128DescrambleWithByteRangeCallbackFunc pHLSAES128DescrambleWithByteRangeCallbackFunc, void *pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
	int nexPlayerSWP_RegisterHTTPRetrieveDataCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERHTTPRetrieveDataCallbackFunc pHTTPRetrieveCallbackFunc, void *pUserData);
	
   /** 
	* For internal use only. Please do not use.
	*/
	int nexPlayerSWP_RegisterHTTPStoreDataCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERHTTPStoreDataCallbackFunc pHTTPStoreCallbackFunc, void *pUserData);
	
	// Not currently supported.
	int nexPlayerSWP_RegisterDashDRMSessionOpen_Multi(void* nexPlayerClassInstance, NEXPLAYERDashDrmSessionOpenCallbackFunc pOpenCallbackFunc, void* pUserData);
	// Not currently supported.
	int nexPlayerSWP_RegisterDashDRMSessionClose_Multi(void* nexPlayerClassInstance, NEXPLAYERDashDrmSessionCloseCallbackFunc pCloseCallbackFunc, void* pUserData);
	// Not currently supported.
	int nexPlayerSWP_RegisterDashDRMSessionSetCencBox_Multi(void* nexPlayerClassInstance, NEXPLAYERDashDrmSessionSetCencBoxCallbackFunc pSetCencBoxCallbackFunc, void* pUserData);
	// Not currently supported.
	int nexPlayerSWP_RegisterDashDRMSessionDecryptIsobmffFrame_Multi(void* nexPlayerClassInstance, NEXPLAYERDashDrmSessionDecryptIsobmffFrameCallbackFunc pDecryptIsobmffFrameCallbackFunc, void* pUserData);
	
	int nexPlayerSWP_RegisterHLSTSDescrambleWithByteRangeCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERHLSTSDescrambleWithByteRangeCallbackFunc pHLSTSDecrypWithByteRangeCallbackFunc, void *pUserData);
	
	int nexPlayerSWP_RegisterHLSEncPrepareKeyCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERHLSEncPrepareKeyFunc pHLSEncPrepareKey, void *pUserData);
	int nexPlayerSWP_RegisterHLSSampleEncDecryptionSampleCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERHLSSampleEncDecryptSampleFunc pHLSSampleEncDecryptSample, void *pUserData);
	int nexPlayerSWP_RegisterSendMessageToExternalModule_Multi(void* nexPlayerClassInstance, NEXPLAYERSendMessageToExternalModuleFunc pMsgCallback, void *pUserData);

	/**
	* For internal use only. Please do not use.
	*/
	int nexPlayerSWP_RegisterPDEnvelopHeaderParsingCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERPDEnvelopHeaderParsingCallbackFunc pPDEnvelopHeaderParsingCallbackFunc, void* pUserData);

	int nexPlayerSWP_RegisterHLSIsSupportKeyCallBackFunc_Multi(void* nexPlayerClassInstance, NEXPLAYERHLSIsSupportKeyCallbackFunc pHLSIsSupportKey, void *pUserData);
	
	int nexPlayerSDK_IsSupportSWWideVine();

#ifdef __cplusplus
}
#endif

#endif  // _NEXPLAYER_JNI_H_
