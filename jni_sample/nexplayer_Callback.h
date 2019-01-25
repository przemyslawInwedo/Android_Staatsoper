/** 
 * \file nexplayer_Callback.h
 * 
 */

#ifndef _NEXPLAYER_CALLBACK_H_
#define _NEXPLAYER_CALLBACK_H_

#include "nexHD_Callback.h"

#ifdef __cplusplus
extern "C"
{
#endif


	/** \defgroup cbtypes Callback Types
	 *
	 * Callback function typedeffs that are part of the NexPlayer&trade;&nbsp;API.
     */

    /** \defgroup types Types
     *
     * Types that are part of the NexPlayer&trade;&nbsp;API.
     */

    /**
     * \ingroup cbtypes
     * \brief   General callback function for descrambling DRM encrypted content.
     *
     * When registered, this callback function will be called for each frame of
     * audio or video data before that frame is decoded.  This provides a general
     * opportunity for descrambling the frame.  For types of DRM where descrambling
     * must occur at a different point in the playback process or where additional
     * information is needed, more specific functions are provided and should be
     * used instead of this one where necessary.
     *
     * \param[in]  uiType               The type of frame to be descrambled:
     *                                      - <b>0:</b> Video
     *                                      - <b>1:</b> Audio
     *
     * \param[in]  pInputBuffer         The encrypted data to be descrambled.
     *
     * \param[in]  uiInputBufferSize    The size of the encrypted data, in bytes.
     *
     * \param[out] pOutputBuffer        The location at which to place the descrambled
     *                                  output data.  This may point to the same
     *                                  location as the input buffer, or it may point
     *                                  to a separate location. The size available for
     *                                  the output buffer is the same as the size of
     *                                  the input buffer.
     *
     * \param[out] puiOutputBufferSize  The size of the decrypted data.  The callback
     *                                  must set this value.  This may be equal to or
     *                                  smaller than \c uiInputBufferSize, but not larger.
     *
     * \param[in]  pUserData            The user data passed when the callback was
     *                                  originally registered.
     *
     * \returns                         The callback should return zero if the data
     *                                  was successfully descrambled.  In the case of
     *                                  an error, it should return -1.
     */

    typedef int ( *NEXPLAYERDRMDescrambleCallbackFunc ) (unsigned int   uiType,
                                                         unsigned char* pInputBuffer,
                                                         unsigned int   uiInputBufferSize,
                                                         unsigned char* pOutputBuffer,
                                                         unsigned int*  puiOutputBufferSize,
                                                         void *         pUserData);

    /**
     * \ingroup cbtypes
     * \brief   Callback function for descrambling WM-DRM encrypted content.
     *
     *
     * \param[in]  pInputBuffer         The encrypted data to be descrambled.
     *
     * \param[in]  uiInputBufferSize    The size of the encrypted data, in bytes.
     *
     * \param[out] pOutputBuffer        The location at which to place the descrambled
     *                                  output data.  This may point to the same
     *                                  location as the input buffer, or it may point
     *                                  to a separate location. The size available for
     *                                  the output buffer is the same as the size of
     *                                  the input buffer.
     *
     * \param[out] puiOutputBufferSize  The size of the decrypted data.  The callback
     *                                  must set this value.  This may be equal to or
     *                                  smaller than \c uiInputBufferSize, but not larger.
     *
     * \param[in]  pUserData            The user data passed when the callback was
     *                                  originally registered.
     *
     * \returns                         The callback should return zero if the data
     *                                  was successfully descrambled.  In the case of
     *                                  an error, it should return -1.
     */
    typedef int ( *NEXPLAYERWMDRMDescrambleCallbackFunc ) (unsigned char*   pInputBuffer,
                                                           unsigned int     uiInputBufferSize,
                                                           unsigned char*   pOutputBuffer,
                                                           unsigned int*    puiOutputBufferSize,
                                                           unsigned char*   pIVBuffer,
                                                           unsigned long    dwIVBufferSize,
                                                           void *           pUserData);

    /**
     * \ingroup cbtypes
     * \brief   Callback function for descrambling HLS-TS encrypted content (HTTP
     *          Live Streaming content encrypted at the segment level).
     *
     *
     * This callback is called every time an HLS segment is received.  The segment
     * may be either a TS file or an audio file.  The player does not attempt to
     * detect whether a segment is encrypted, but rather passes all segments
     * directly to the callback, if one is registered.
     *
     * \param[in]  pInputBuffer         The segment (TS file or audio file) that
     *                                  has been received.
     *
     * \param[in]  uiInputBufferSize    The size of the data at pInputBuffer, in bytes.
     *
     * \param[out] pOutputBuffer        The location at which to place the descrambled
     *                                  output data.  This may point to the same
     *                                  location as the input buffer, or it may point
     *                                  to a separate location. The size available for
     *                                  the output buffer is the same as the size of
     *                                  the input buffer.  That is, the decrypted data
     *                                  may be smaller than the encrypted data, but
     *                                  not larger.
     *
     * \param[out] puiOutputBufferSize  The size of the decrypted data.  The callback
     *                                  must set this value.  This may be equal to or
     *                                  smaller than \c uiInputBufferSize, but not larger.
     *
     * \param[in]  pMediaFileURL        The URL of the segment media file (TS file or
     *                                  audio file).
     *
     * \param[in]  pPlaylistURL         The URL of the immediate parent playlist (the
     *                                  playlist directly referecing the media file).
     *
     * \param[in]  pUserData            The user data passed when the callback was
     *                                  originally registered.
     *
     * \returns                         The callback should return zero if the data
     *                                  was successfully descrambled.  In the case of
     *                                  an error, it should return -1.
     */
    typedef unsigned long ( *NEXPLAYERHLSTSDescrambleCallbackFunc ) (unsigned char* pInputBuffer,
                                                                     unsigned int   uiInputBufferSize,
                                                                     unsigned char* pOutputBuffer,
                                                                     unsigned int*  puiOutputBufferSize,
                                                                     void*          pMediaFileURL,
                                                                     void*          pPlaylistURL,
                                                                     void*          pUserData);

    /**
     * \ingroup cbtypes
     * \brief   Callback function for receiving updates to the HLS playlist data.
     *
     *
     * This is called every time that the player receives an HLS playlist.  This
     * can happen in several cases:
     *
     * - When the initial (master) playlist is received.
     * - When the player switches to a new track and loads the playlist for that track.
     * - While playing live content, if the server updates the playlist.
     *
     * Whenever ::NEXPLAYERHLSTSDescrambleCallbackFunc is called with a TS or audio
     * file to be descrambled, that TS or audio file will always be from the
     * playlist most recently received by this callback.
     *
     * \param[in]  pUrl                 The URL of the playlist.
     *
     * \param[in]  pPlaylist            The contents of the playlist, as text.
     *
     * \param[in]  uiPlaylistSize       The size of pPlayList, in bytes, not including any terminating null.
     *
     * \param[in]  pUserData            The user data passed when the callback was
     *                                  originally registered.
     *
     * \returns                         The callback should return zero for success
     *                                  and -1 in case of an error.
     */
    typedef unsigned int (*NEXPLAYERGetPlaylistInfoCallbackFunc)(   char* pUrl,
                                                                    char* pPlaylist,
                                                                    unsigned int uiPlaylistSize,
                                                                    void* pUserData);

    /**
     * \ingroup cbtypes
     * \brief   Callback function for descrambling Smooth Streaming fragments.
     *
     * When registered, this callback function is called every time a Smooth
     * Streaming fragment is received, regardless of whether or not the
     * fragment is encrypted.  The callback should perform any necessary
     * descrambling on the fragment, and return the descrambled fragment.
     *
     * \param[in]  pInputBuffer         The fragment that has just been received.
     *
     * \param[in]  uiInputBufferSize    The size of the fragment, in bytes
     *
     * \param[out] pOutputBuffer        The location at which to place the descrambled
     *                                  output data.  This may point to the same
     *                                  location as the input buffer, or it may point
     *                                  to a separate location. The size available for
     *                                  the output buffer is the same as the size of
     *                                  the input buffer.
     *
     * \param[out] puiOutputBufferSize  The size of the decrypted data.  The callback
     *                                  must set this value.  This may be equal to or
     *                                  smaller than \c uiInputBufferSize, but not larger.
     *
     * \param[in]  pUserData            The user data passed when the callback was
     *                                  originally registered.
     *
     * \returns                         The callback should return zero if the data
     *                                  was successfully descrambled.  In the case of
     *                                  an error, it should return -1.
     */
    typedef unsigned long ( *NEXPLAYERSmoothStreamFragmentDescrambleCallbackFunc ) (unsigned char*  pInputBuffer,
                                                                                    unsigned int    uiInputBufferSize,
                                                                                    unsigned char*  pOutputBuffer,
                                                                                    unsigned int*   puiOutputBufferSize,
                                                                                    void *          pUserData);


    /**
     * \ingroup cbtypes
     * \brief   Callback function for descrambling Smooth Streaming PlayReady encrypted content.
     *
     * When registered, this callback function is called every time Smooth Streaming PlayReady encrypted content
     * is received.
     *
     * \param[in]  pInputBuffer         The encrypted data to be descrambled.
     *
     * \param[in]  dwInputBufferSize    The size of the encrypted data, in \c bytes.
     *
     * \param[out] pOutputBuffer        The location at which to place the descrambled
     *                                  output data.  This may point to the same
     *                                  location as the input buffer, or it may point
     *                                  to a separate location. The size available for
     *                                  the output buffer is the same as the size of
     *                                  the input buffer.
     *
     * \param[out] pdwOutputBufferSize  The size of the decrypted data.  The callback
     *                                  must set this value.  This may be equal to or
     *                                  smaller than \c dwInputBufferSize, but not larger.
     *
     * \param[in]  pSampleEncBox        The \c SampleEncryptionBox, as detailed in the
     *                                  <em>[MS-SSTR] Smooth Streaming Protocol Specification</em>.
     *
     * \param[in]  dwSampleEncBoxLen    The length, in bytes, of the data at \c pSampleEncBox.
     *
     * \param[in]  dwSampleIDX          The index of the media object (frame or sample, depending
     *                                  on media format) being descrambled.
     *
     * \param[in]  dwTrackID            Media Track ID, from \c TfhdBox, as defined in the
     *                                  <em>[MS-SSTR] Smooth Streaming Protocol Specification</em>.
     *
     * \param[in]  pUserData            The user data passed when the callback was
     *                                  originally registered.
     *
     * \returns                         The callback should return zero if the data
     *                                  was successfully descrambled.  In the case of
     *                                  an error, it should return -1.
     */
    typedef unsigned long ( *NEXPLAYERSmoothStreamPlayReadyDescrambleCallbackFunc ) (
                                                                           unsigned char*   pInputBuffer,
                                                                           unsigned long    dwInputBufferSize,
                                                                           unsigned char*   pOutputBuffer,
                                                                           unsigned long*   pdwOutputBufferSize,
                                                                           unsigned char*   pSampleEncBox,
                                                                           unsigned long    dwSampleEncBoxLen,
                                                                           unsigned long    dwSampleIDX,
                                                                           unsigned long    dwTrackID,
                                                                           void*            pUserData);

    /**
     * \ingroup cbtypes
     * \brief   Callback function for descrambling PlayReady encrypted content in a PIFF file.
     *
     * \param[in]  pInputBuffer         The encrypted data to be descrambled.
     *
     * \param[in]  dwInputBufferSize    The size of the encrypted data, in bytes.
     *
     * \param[out] pOutputBuffer        The location at which to place the descrambled
     *                                  output data.  This may point to the same
     *                                  location as the input buffer, or it may point
     *                                  to a separate location. The size available for
     *                                  the output buffer is the same as the size of
     *                                  the input buffer.  That is, the decrypted data
     *                                  may be smaller than the encrypted data, but
     *                                  not larger.
     *
     * \param[out] pdwOutputBufferSize  The size of the decrypted data.  The callback
     *                                  must set this value.  This may be equal to or
     *                                  smaller than \c dwInputBufferSize, but not larger.
     *
     * \param[in]  pSampleEncBox        The \c SampleEncryptionBox, as detailed in the
     *                                  <em>[MS-SSTR] Smooth Streaming Protocol Specification</em>.
     *
     * \param[in]  dwSampleEncBoxLen    The length, in bytes, of the data at \c pSampleEncBox.
     *
     * \param[in]  dwSampleIDX          The index of the media object (frame or sample, depending
     *                                  on media format) being descrambled.
     *
     * \param[in]  dwTrackID            Media Track ID, from \c TfhdBox, as defined in the
     *                                  <em>[MS-SSTR] Smooth Streaming Protocol Specification</em>.
     *
     * \param[in]  pUserData            The user data passed when the callback was
     *                                  originally registered.
     *
     * \returns                         The callback should return zero if the data
     *                                  was successfully descrambled.  In the case of
     *                                  an error, it should return -1.
     */
    typedef int ( *NEXPLAYERPiffPlayReadyDescrambleCallbackFunc ) (
                                                                   unsigned char*   pInputBuffer,
                                                                   unsigned long    dwInputBufferSize,
                                                                   unsigned char*   pOutputBuffer,
                                                                   unsigned long*   pdwOutputBufferSize,
                                                                   unsigned char*   pSampleEncBox,
                                                                   unsigned long    dwSampleEncBoxLen,
                                                                   unsigned long    dwSampleIDX,
                                                                   unsigned long    dwTrackID,
                                                                   void*            pUserData);

    /**
     * \ingroup cbtypes
     * \brief   Callback function for descrambling DECE UV(Ultra Violet) encrypted content in a CFF file.
     *
     * \param[in]  pInputBuffer         The encrypted data to be descrambled.
     *
     * \param[in]  dwInputBufferSize    The size of the encrypted data, in \c bytes.
     *
     * \param[out] pOutputBuffer        The location at which to place the descrambled
     *                                  output data.  This may point to the same
     *                                  location as the input buffer, or it may point
     *                                  to a separate location. The size available for
     *                                  the output buffer is the same as the size of
     *                                  the input buffer.  That is, the decrypted data
     *                                  may be smaller than the encrypted data, but
     *                                  not larger.
     *
     * \param[out] pdwOutputBufferSize  The size of the decrypted data.  The callback
     *                                  must set this value.  This may be equal to or
     *                                  smaller than \c dwInputBufferSize, but not larger.
     *
     * \param[in]  pSampleEncBox        The \c SampleEncryptionBox, as detailed in the
     *                                  <em>[UV] Ultra Violet Protocol Specification</em>.
     *
     * \param[in]  dwSampleEncBoxLen    The length, in bytes, of the data in \c pSampleEncBox.
     *
     * \param[in]  dwSampleIDX          The index of the media object (frame or sample, depending
     *                                  on the media format) being descrambled.
     *
     * \param[in]  dwTrackID            The Media Track ID, from \c TfhdBox, as defined in the
     *                                  <em>[UV] Ultra Violet Protocol Specification</em>.
     *
     * \param[in]  pUserData            The user data passed when the callback was
     *                                  originally registered.
     *
     * \returns                         The callback should return zero if the data
     *                                  was successfully descrambled.  In the case of
     *                                  an error, it should return -1.
     */
    typedef int ( *NEXPLAYERDeceUVDescrambleCallbackFunc ) (
                                                                   unsigned char*   pInputBuffer,
                                                                   unsigned long    dwInputBufferSize,
                                                                   unsigned char*   pOutputBuffer,
                                                                   unsigned long*   pdwOutputBufferSize,
                                                                   unsigned char*   pSampleEncBox,
                                                                   unsigned long    dwSampleEncBoxLen,
                                                                   unsigned long    dwSampleIDX,
                                                                   unsigned long    dwTrackID,
                                                                   void*            pUserData);


    /**
     * \ingroup cbtypes
     * \brief   Callback function for descrambling PlayReady encrypted content in an ASF file.
     *
     * \param[in]  pInBuf               The encrypted data to be descrambled.
     *
     * \param[in]  dwInBufSize          The size of the encrypted data, in \c bytes.
     *
     * \param[out] pOutBuf              The location at which to place the descrambled
     *                                  output data.  This may point to the same
     *                                  location as the input buffer, or it may point
     *                                  to a separate location. The size available for
     *                                  the output buffer is the same as the size of
     *                                  the input buffer.  That is, the decrypted data
     *                                  may be smaller than the encrypted data, but
     *                                  not larger.
     *
     * \param[out] pdwOutSize           The size of the decrypted data.  The callback
     *                                  must set this value.  This may be equal to or
     *                                  smaller than \c uiInputBufferSize, but not larger.
     *
     * \param[in]  pIVBuf               Initialization vector.
     *
     * \param[in]  dwIVBufSize          Size (in bytes) of the initialization vector.
     *
     * \param[in]  pUserData            The user data passed when the callback was
     *                                  originally registered.
     *
     * \returns                         The callback should return zero if the data
     *                                  was successfully descrambled.  In the case of
     *                                  an error, it should return -1.
     */
    typedef int (*NEXPLAYERAsfPlayReadyDescrambleCallbackFunc)(unsigned char*   pInBuf,
                                                               unsigned long    dwInBufSize,
                                                               unsigned char*   pOutBuf,
                                                               unsigned long*   pdwOutSize,
                                                               unsigned char*   pIVBuf,
                                                               unsigned long    dwIVBufSize,
                                                               void*            pUserData);



	/**
	 * \ingroup cbtypes
	 * \brief \brief  Callback function to retrieve an encryption key from an HLS playlist over HTTPS for descrambling.
	 *
	 *  This function is called each time a new playlist is received and an encryption key is available in the playlist.
	 *
	 * \param[out] pKeyUrl		A pointer to the URL of the encryption key.
	 * \param dwKeyUrlLen		The length of the URL to the key, in bytes.
	 * \param[in] pKeyBuf		A pointer to the buffer where the encryption key information will be stored.
	 * \param dwKeyBufSize		The size of the key information buffer, in bytes (fixed to 32 bytes).
	 * \param[in] pdwKeySize	A pointer to the length of the encryption key, in bytes.
	 * \param pUserData			The user data passed when the callback was
	 *							originally registered.
	 *
	 * \returns		Zero if the operation was successful, 1 if the buffer was not large enough and a larger
	 *				buffer is needed, or 2 if an error occurred.
	 *
	 * \since version 6.2.2
	 */
	typedef int (*NEXPLAYERGetKeyExtCallbackFunc)( char*	pKeyUrl,
													unsigned long	dwKeyUrlLen,
													unsigned char* pKeyBuf,
													unsigned long dwKeyBufSize,
													unsigned long* pdwKeySize,
													unsigned long pUserData);


/**
 * \ingroup cbtypes
 * \brief Callback function for setting HTTP authorization information.
 *
 * This function replaces the callback \c NEXPLAYERGetCredentialCallbackFunc from previous versions
 * of the SDK and allows the player to control HTTP status codes in more detail.
 * The authorization information (AuthInfo) for each HTTP status code may be handled individually
 * and differently, if desired.
 *
 * The authorization information <em>MUST</em> be a NULL-terminated string.  In other words, each line included in
 * \c pAuthInfo must be a well-formed HTTP line that ends with a single "\r\n".  For example, authorization information
 * could be included as:
 * \code
 * "Authorization: AAAAA\r\n"
 * \endcode
 *
 * \param[in]	ulStatusCode		The HTTP Status Code. For example, 401 or 403 (provided for convenience).
 * \param[in]	pResponse			The string received as HTTP Response from the server.
 * \param ulResponseSize			The size of the \c pResponse, in bytes.
 * \param[out]	pAuthInfo,			A pointer to the string buffer containing authorization information.
 *									This authorization information must be written as well-formed HTTP commands,
 *									For exampl: \code "Authorization: AAAAA\r\n" \endcode
 * \param[out]	ulAuthInfoBufSize	The size of \c pAuthInfo, in bytes.
 * \param[out]	pulNewBufSize,		If the buffer is not large enough to copy the authorization information, then
 *									this can be used to request a new buffer size,
 *									and returns \c NEEDMOREBUF.
 * \param[in]	pUserData			The user data passed when the callback was registered.
 *
 * \returns Zero in the case of success, or 1 if the buffer was not large enough to copy the authorization
 *          information, in which case \c pulNewBufSize will be \c NEEDMOREBUF, a new buffer should be
 *          requested, and this callback function will be called again.
 *
 * \since version 5.9
*/

    typedef int(*NEXPLAYERGetHttpAuthInfoCallbackFunc)(
    								unsigned long 		ulStatusCode,
									char* 			pResponse,
									unsigned long		ulResponseSize,
									char*			pAuthInfo,
									unsigned long		ulAuthInfoBufSize,
									unsigned long*	pulNewBufSize,
									void*			pUserData);


 /**
  * \ingroup cbtypes
  * \brief  Callback function to handle content with an encrypted playlist or manifest.
  *
  * NexPlayer&trade;&nbsp;calls this function whenever it receives the manifest or top level of a playlist so
  * that the playlist or manifest can be decrypted (in the case it happens to be encrypted).
  *
  * \param[in]	pMpdUrl			The original URL of the top-level MPD.  In the case of
  *								redirection, this will be the URL <em>before</em>
  *								redirection.
  * \param[in]	dwMpdUrlLen		The length of the manifest or playlist URL, in bytes.
  * \param[in,out] pMpd			The top level playlist or manifest.
  * \param[in]	dwMpdLen		The size of the manifest or playlist, in bytes.
  * \param[out] pdwNewMpdLen	The size of the decrypted manifest or playlist.
  * \param[in] pUserData		The user data passed when the callback was registered.
  *
  * \returns	1 if successful, zero in the case of failure.
  *
  * \since version 5.9
  */

    typedef int(*NEXPLAYERMPDDescrambleCallbackFunc)(
												char*				pMpdUrl,
												unsigned long		dwMpdUrlLen,
												char*				pMpd,
												unsigned long		dwMpdLen,
												unsigned long*		pdwNewMpdLen,
												void*					pUserData);
	
	
	/**
	 * \ingroup cbtypes
	 * \brief Callback function for receiving AES128 encrypted HLS content.
	 *
	 * When registered, this callback function is called every time AES128 encrypted HLS content is received.
	 *
	 * \param[in] pInBuf			A pointer to the input buffer.
	 * \param dwInBufSize			The size of the input buffer, in bytes.
	 * \param[out] pOutBuf			A pointer to the output buffere where decrypted content is stored.
	 * \param[out] pdwOutBufSize	A pointer to the size of the decrypted segment, in bytes.
	 * \param[in]  pSegmentUrl		A pointer to the URL of the segment of content.
	 * \param[in]  pMpdUrl			A pointer to the original URL of the content playlist.
	 * \param[in]  pKeyAttr			A pointer to the decryption Key information.
	 * \param[in]  dwSegmentSeq		The sequence number of the TS segment file.
	 * \param[in]  pKey				A pointer to the decryption Key.  This parameter is only meaningful when \c dwKeySize is greater than 0 (ie a key has been downloaded).
	 * \param  dwKeySize			The size of the decryption Key.  This parameter will be zero if no key has been downloaded.
	 * \param[in]  pUserData		The user data passed when the callback was registered.
	 *
	 * \returns 0 if successful, non-zero in the case of failure.
	 *
	 * \since version 6.3
	 */
	typedef int (*NEXPLAYERHLSAES128DescrambleCallbackFunc)	(unsigned char*				pInBuf,			// [in] Input segment.
															unsigned long				dwInBufSize,	// [in] Input segment size.
															unsigned char*				pOutBuf,		// [out] decrypted segment.
															unsigned long*				pdwOutBufSize,	// [out] The size of decrypted segment.
															char*						pSegmentUrl,	// [in] Segment Url.
															char*						pMpdUrl,		// [in] Original Url of the currently playing Mpd.
															char*						pKeyAttr,		// [in] KeyInfo Attribute of the Segment.
															unsigned long				dwSegmentSeq,	// [in] The sequence number of the TS segment file.
															unsigned char*				pKey,			// [in] Key. (Has meaning only when dwKeySize is bigger than 0)
															unsigned long				dwKeySize,		// [in] Key size. (0 if no key is downloaded.)
															void*						pUserData);		// [in]

    /**
     *
     *  \ingroup cbtypes
     * \brief Callback function for receiving AES128 encrypted HLS content with byte range.
     *
     * When registered, this callback function is called every time AES128 encrypted HLS content is received.
     *
     * \param[in] pInBuf            A pointer to the input buffer.
     * \param dwInBufSize           The size of the input buffer, in \c bytes.
     * \param[out] pOutBuf          A pointer to the output buffer where decrypted content is stored.
     * \param[out] pdwOutBufSize    A pointer to the size of the decrypted segment, in \c bytes.
     * \param[in]  pSegmentUrl      A pointer to the URL of the content segment.
     * \param[in]  qByteRangeOffset The offset of the byte range.
     * \param[in]  qByteRangeLength The length of the byte range.
     * \param[in]  pMpdUrl          A pointer to the original URL of the content playlist.
     * \param[in]  pKeyAttr         A pointer to the decryption Key information.
     * \param[in]  dwSegmentSeq     The sequence number of the TS segment file.
     * \param[in]  pKey             A pointer to the decryption Key.  This parameter is only meaningful when \c dwKeySize is greater than 0 (ie a key has been downloaded).
     * \param  dwKeySize            The size of the decryption Key.  This parameter will be zero if no key has been downloaded.
     * \param[in]  pUserData        The passed user data when the callback was registered.
     *
     * \returns 0 if successful, non-zero in the case of failure.
     *
     * \since version 6.51
     */
	typedef int (*NEXPLAYERHLSAES128DescrambleWithByteRangeCallbackFunc)	(unsigned char*			pInBuf,			// [in] Input segment.
																			 unsigned int			dwInBufSize,	// [in] Input segment size.
																			 unsigned char*			pOutBuf,		// [out] decrypted segment.
																			 unsigned int*			pdwOutBufSize,	// [out] The size of decrypted segment.
																			 char*					pSegmentUrl,	// [in] Segment Url.
																			 long long				qByteRangeOffset,	// [in] If qByteRangeOffset is -1, then it means that byte range is not used.
																			 long long				qByteRangeLength,	// [in] If qByteRangeLength is 0, then it means that byte range is not used.
																			 char*					pMpdUrl,		// [in] Original Url of the currently playing Mpd.
																			 char*					pKeyAttr,		// [in] KeyInfo Attribute of the Segment.
																			 unsigned int			dwSegmentSeq,	// [in] The sequence number of the TS segment file.
																			 unsigned char*			pKey,			// [in] Key. (Has meaning only when dwKeySize is bigger than 0)
																			 unsigned int			dwKeySize,		// [in] Key size. (0 if no key is downloaded.)
																			 void*					pUserData);		// [in]
	
	/**
	 *
     *  \ingroup cbtypes
     *  \brief Callback function for receiving blocks of Progressive Download(PD) content.
     *
     *  This is called each time NexPlayer&trade;&nbsp;receives a block of Progressive Download(PD) content.
     *  NexPlayer&trade;&nbsp;sends the received block and the block's size with this callback.
     *
     *  \param[in,out]  pBlockBuf   The array containing the data from the PD block.
     *  \param[in]  uiBlockSize     The size of the PD block received.
     *  \param[in]  ulOffset        The offset of the block's starting position from the beginning of the content.
     *  \param[in]  pUserData       The user data passed when the callback was registered.
     *
     *  \returns    Zero, but has no meaning and should be ignored.
     *
     **/
    typedef int (*NEXPLAYERGetPDBlockCallbackFunc)(     char* pBlockBuf,
                                                        long long ulOffset,
                                                        int uiBlockSize,
                                                        void* pUserData);

    /**
     *
     *  \ingroup cbtypes
     *  \brief Callback function for parsing headers of Progressive Download(PD) content.
     *
     *  This is called when NexPlayer&trade;&nbsp; receives a header of Progressive Download(PD) content.
     *  NexPlayer&trade;&nbsp; sends the received header and its size with this callback.
     *
     *  \param[in]  pData			The array containing the data of the PD header.
     *  \param[in]  qOffset		    The offset of the header's starting position.
     *  \param[in]  iDataSize       The size of the PD header received.
     *  \param[in,out] puContentOffset The offset of content.
     *  \param[in]	pUserData       The user data passed when the callback was registered.
     *
     *  \returns    Success : 0 <br>
     * Need More Data : -1 <br>
     * DRM error code :  < -1 <br>
     * Remark : When it is not DRM content, return 0 with puContentOffset=0;
     **/
	typedef int (*NEXPLAYERPDEnvelopHeaderParsingCallbackFunc)(char* pData,
														  long long qOffset,
														  int iDataSize,
														  unsigned int* puContentOffset,
														  void* pUserData);

    /**
     * \ingroup cbtypes
     * \brief Callback replacement for \c dlopen system call.
     *
     * This is one of several callback functions that can be registered using
     * the \link nexplayer_jni.h#nexPlayerSWP_RegisterDLAPICallbackFunc nexPlayerSWP_RegisterDLAPICallbackFunc\endlink
     * in the Java Native Interface provided with the NexPlayer&trade;&nbsp;SDK.
     *
     * It can replace the system
     * calls normally used for loading and accessing dynamic libraries.
     *
     * Arguments and return values are the same as for \c dlopen.
     */
    typedef void* (*NEXPLAYERDLOpenCallbackFunc)(const char* filename, int flag);

    /**
     * \ingroup cbtypes
     * \brief Callback replacement for \c dlsym system call.
     *
     * This is one of several callback functions that can be registered using
     * the \link nexplayer_jni.h#nexPlayerSWP_RegisterDLAPICallbackFunc nexPlayerSWP_RegisterDLAPICallbackFunc\endlink
     * in the Java Native Interface provided with the NexPlayer&trade;&nbsp;SDK.
     *
     * It can replace the system
     * calls normally used for loading and accessing dynamic libraries.
     *
     *
     * Arguments and return values are the same as for \c dlsym.
     */
    typedef void* (*NEXPLAYERDLSymCallbackFunc)(void* hDL, const char* strFunc);

    /**
     * \ingroup cbtypes
     * \brief Callback replacement for \c dlclose system call.
     *
     * This is one of several callback functions that can be registered using
     * the \link nexplayer_jni.h#nexPlayerSWP_RegisterDLAPICallbackFunc nexPlayerSWP_RegisterDLAPICallbackFunc\endlink
     * in the Java Native Interface provided with the NexPlayer&trade;&nbsp;SDK.
     *
     * It can replace the system
     * calls normally used for loading and accessing dynamic libraries.
     *
     *
     * Arguments and return values are the same as for \c dlclose.
     */
    typedef int (*NEXPLAYERDLCloseCallbackFunc)(void* hDL);

    /**
     * \ingroup cbtypes
     * \brief Callback replacement for \c dlerror system call.
     *
     * This is one of several callback functions that can be registered using
     * the \link nexplayer_jni.h#nexPlayerSWP_RegisterDLAPICallbackFunc nexPlayerSWP_RegisterDLAPICallbackFunc\endlink
     * in the Java Native Interface provided with the NexPlayer&trade;&nbsp;SDK.
     *
     * It can replace the system
     * calls normally used for loading and accessing dynamic libraries.
     *
     * Arguments and return values are the same as for \c dlerror.
     */
    typedef const char* (*NEXPLAYERDLErrorCallbackFunc)(void);
	
	/**
    * \ingroup cbtypes
	* \brief Callback function to retrieve stored HTTP data.
	*
	* Since HTTP version 1.1 supports partial download, this function checks to see if there is any previously stored data available for the given URL and when there is,
	* NexPlayer&trade;&nbsp; will retrieve that data here first before requesting and receiving new data. This callback function can be used
	* in conjuction with \c NEXPLAYERHTTPStoreDataCallbackFunc to provide offline playback.
	*
	* \param[in] pUrl		A pointer to the URL where HTTP calls will be made.
	* \param[in] dwOffset	The offset of the data to be retrieved from the beginning of the file at the \mbox{URL}(used with the Range header).
	* \param[in] dwLength		The length of the data to receive, starting at the offset.
	* \param[out] ppOutputBuffer  Pointer to the buffer to hold retrieved HTTP data, if any was previously stored.
	* \param[out] pdwSize		The size of the retrieved HTTP data.
	* \param[in] pUserData	The user data passed when the callback was registered.
	*
	* \returns  Zero if successful or a non-zero error code.
	*
	* \see NEXPLAYERHTTPStoreDataCallbackFunc
	* \since version 6.5
	 */
	typedef int (*NEXPLAYERHTTPRetrieveDataCallbackFunc)(char* pUrl,
																	unsigned long long dwOffset,
																	unsigned long long dwLength,
																	char** ppOutputBuffer,
																	unsigned long long* pdwSize,
																	void* pUserData);
	
	/**
	 * \ingroup cbtypes
	 * \brief Callback function to store HTTP data.
	 *
	 * Since HTTP version 1.1 supports partial download, this function stores if there is no previously stored data to retrieve for the given URL and when there is,
	 * NexPlayer&trade;&nbsp; will store that data by requesting and receiving new data. This callback function can be used
	 * in conjuction with \c NEXPLAYERHTTPRetrieveDataCallbackFunc to provide offline playback.
	 *
	 * \param[in] pUrl		A pointer to the URL where HTTP calls will be made.
	 * \param[in] dwOffset	The offset of the data to be stored from the beginning of the file at the URL (used with the Range header).
	 * \param[in] dwLength	The length of data at offset being stored.
	 * \param[in] pBuffer   Pointer to the buffer to hold stored HTTP data.
	 * \param[in] dwSize	The size of data to store.
	 * \param[in] pUserData	The user data passed when the callback was registered.
	 *
	 * \returns  Zero if successful or a non-zero error code.
	 *
	 * \see NEXPLAYERHTTPRetrieveDataCallbackFunc
	 * \since version 6.5
	 */
	typedef int (*NEXPLAYERHTTPStoreDataCallbackFunc) (char* pUrl,
																	unsigned long long dwOffset,
																	unsigned long long dwLength,
																	char* pBuffer,
																	unsigned long long dwSize,
																	void* pUserData);


    /**
     * \ingroup cbtypes
     *
     * \brief Callback function to open DASH DRM Session. 
     *
     * \param[out] pSH          DRM session handle. The initial value is -1 which indicates that DRM Session is not opened, therefore \c NEXPLAYERDashDrmSessionCloseCallbackFunc will not be called.                 
     * \param[in] pDrmInfo      Contains all the ContentProtection tags in MPD.
     * \param[in] dwDrmInfoSize The byte length of \c pDrmInfo. 
     * \param[in] pUserData   The user data passed when the callback was registered.
     *
     * \returns  Zero if successful or a non-zero error code.
     *
     * \since version 6.42
     */
	typedef int (*NEXPLAYERDashDrmSessionOpenCallbackFunc)	(long long*		pSH,
															char*			pDrmInfo,
															unsigned int	dwDrmInfoSize,
															void*			pUserData);


    /**
     * \ingroup cbtypes
     *
	 * \brief Callback function to close DASH DRM Session. 
     * This function will only be called when the parameter \c pSH of \c NEXPLAYERDashDrmSessionOpenCallbackFunc is not -1.
     *
     * \param[in] hSH         DRM session handle.     
     * \param[in] pUserData The user data passed when the callback was registered.
     *
     * \returns  Zero if successful or a non-zero error code.
     *
     * \since version 6.42
     */
	typedef int (*NEXPLAYERDashDrmSessionCloseCallbackFunc)	(long long	hSH,
																void*		pUserData);		


    /**
     * \ingroup cbtypes
     *
	 * \brief Callback function to transfer CencBox.
     *
     * \param[in] hSH            DRM session handle.
     * \param[in] pBoxName       The box name as a NULL-terminated string : \c seig, \c tenc or \c pssh.
     * \param[in] pBoxData       The payload information of the box. 
     * \param[in] dwBoxDataSize  Byte length of \c pBoxData.
     * \param[in] pUserData      The user data passed when the callback was registered.
     *
     * \returns  Zero if successful or a non-zero error code.
     *
     * \since version 6.42
     */
	typedef int (*NEXPLAYERDashDrmSessionSetCencBoxCallbackFunc) (long long				hSH,
														char*				pBoxName,
														char*				pBoxData,
														unsigned int		dwBoxDataSize,
														void*				pUserData);


    /**
     * \ingroup cbtypes
	 *
     * \brief Callback function to decrypt an encrypted frame.
     *
     * \param[in] hSH               DRM Session handle.
     * \param[in] pIV               Initial vector.
     * \param[in] dwIVLen           Byte length of initial vector.
     * \param[in] pEncFrame         Encrypted frame.
     * \param[in] dwEncFrameLen     Byte length of encrypted frame.
     * \param[out] pDecFrame        Decrypted frame.
     * \param[out] pdwDecFrameLen   Byte length of decrypted frame.
     * \param[in] pUserData         The user data passed when the callback was registered.
     *
     * \returns  Zero if successful or a non-zero error code.
     *
     * \since version 6.42
     */
	typedef int (*NEXPLAYERDashDrmSessionDecryptIsobmffFrameCallbackFunc) (long long			hSH,
																			char*				pSAI,
																			unsigned int		dwSAILen,
																			char*				pEncFrame,
																			unsigned int		dwEncFrameLen,
																			char*				pDecFrame,
																			unsigned int*		pdwDecFrameLen,
																			void*				pUserData);
	
    /*typedef int (*NEXPLAYERInitMediaDrmCallbackFunc) (unsigned char *       pUUID,
                                                      unsigned int          nUUIDLen,
                                                      unsigned char *       pPssh,
                                                      unsigned int          nPsshLen,
                                                      void *                pUserData );

    typedef int (*NEXPLAYERDeinitMediaDrmCallbackFunc) ( void *                pUserData ); */
	
 
    /**
     * \ingroup cbtypes
     * \brief Callback function to prepare an HLS Sample encryption key.
     * \param[in] pKeyAttr The text including the key tag and its attribute list. (i.e. #EXT-X-KEY: METHOD=SAMPLE-AES,URI=..)
     * \param[in] dwKeyAttrLen String length of pKeyAttr.
     * \param[out] ppEtcInfo Extra information to pass to decryption callback.
     * \param[in] pUserData User Data which is registered with this callback.
     *
     * \returns  Zero if successful or a non-zero error code.
     */
	typedef int (*NEXPLAYERHLSEncPrepareKeyFunc)(unsigned char*		pKeyAttr,			
												 unsigned int		dwKeyAttrLen,		
												 void**		ppEtcInfo,			
												 void*			pUserData);			

    /**
     * \ingroup cbtypes
     * \brief Callback function to decrypt an HLS sample encrypted frame.
     * \param[in] pInBuf Input frame.
     * \param[in] dwInBufSize Input frame size.
     * \param[in] eCodecType Codec type.
     * \param[in] dwSeqNum Sequence number of the media segment containing this sample encrypted frame.
     * \param[out] pOutBuf Decrypted frame.
     * \param[out] pdwOutSize  The frame size of decrypted sample.
     * \param[in] pEtcInfo Extra information passed from NEXPLAYERHLSEncPrepareKeyFunc callback.
     * \param[in] pUserData User Data which is registered with this callback.
     *
     * \returns  Zero if successful or a non-zero error code.
     */
	typedef int (*NEXPLAYERHLSSampleEncDecryptSampleFunc) (unsigned char*		pInBuf,				
														   unsigned int		dwInBufSize,		
														   int	eCodecType,			
														   unsigned int		dwSeqNum,			
														   unsigned char*		pOutBuf,			
														   unsigned int*		pdwOutSize,			
														   void*			pEtcInfo,			
														   void*			pUserData);			

   
	typedef int (*NEXPLAYERSendMessageToExternalModuleFunc)(unsigned int uiMsg, // [in] message No.
															int iMsgValue1, // [in] message value1
															void* pMsgValue2, //[in] message value2
															void* pUserData); //[in] userData
	
    /**
     * \ingroup cbtypes
     * \brief   Callback function for descrambling HLS-TS encrypted content (HTTP
     *          Live Streaming content encrypted at the segment level).
     *
     *
     * This callback is called every time an HLS segment is received.  The segment
     * may be either a TS file or an audio file.  The player does not attempt to
     * detect whether a segment is encrypted, but rather passes all segments
     * directly to the callback, if one is registered.
     *
     * \param[in]  pInBuf         The segment (TS file or audio file) that
     *                                  has been received.
     *
     * \param[in]  dwInBufSize    The size of the data at \c pInBuf, in bytes.
     *
     * \param[out] pOutBuf        The location at which to place the descrambled
     *                                  output data.  This may point to the same
     *                                  location as the input buffer, or it may point
     *                                  to a separate location. The size available for
     *                                  the output buffer is the same as the size of
     *                                  the input buffer.  That is, the decrypted data
     *                                  may be smaller than the encrypted data, but
     *                                  not larger.
     *
     * \param[out] pdwOutSize    The size of the decrypted data.  The callback
     *                                  must set this value.  This may be equal to or
     *                                  smaller than \c dwInBufSize, but not larger.
     *
     * \param[in]  pMediaFileUrl        The URL of the segment media file (TS file or
     *                                  audio file).
     *
     * \param[in] qByteRangeOffset      Byte range offset. If \c qByteRangeOffset is -1, then it means that byte range is not used.
     *
     *
     * \param[in] qByteRangeLength      Byte range length. If \c qByteRangeLength is 0, then it means that byte range is not used.
     *
     *
     * \param[in]  pPlaylistUrl         The URL of the immediate parent playlist (the
     *                                  playlist directly referecing the media file).
     *
     * \param[in]  pUserData            The user data passed when the callback was
     *                                  originally registered.
     *
     * \returns                         The callback should return zero if the data
     *                                  was successfully descrambled.  In the case of
     *                                  an error, it should return -1.
     */
	typedef int (*NEXPLAYERHLSTSDescrambleWithByteRangeCallbackFunc)(unsigned char*		pInBuf,				// [in] Input segment.
																		unsigned int		dwInBufSize,		// [in] Input segment size.
																		unsigned char*		pOutBuf,			// [out] decrypted segment.
																		unsigned int*		pdwOutSize,			// [out] The size of decrypted segment.
																		char*			pMediaFileUrl,		// [in] Url of the media file.
																		long long			qByteRangeOffset,	// [in] If qByteRangeOffset is -1, then it means that byte range is not used.
																		long long			qByteRangeLength,	// [in] If qByteRangeLength is 0, then it means that byte range is not used.
																		char*			pPlaylistUrl,		// [in] Url of the parent playlist.
																		void*			pUserData);
	

    /**
     * 
     * \ingroup cbtypes
     * \brief   Callback function that verifies whether or not a key attribute is supported by the DRM module.
     *
     * This callback is called when NexPlayer meets #EXT-X-KEY tags while NexPlayer is parsing
     * playlists. If the callback returns a non-zero value, NexPlayer will not call DRM functions even if that function is registered.
     *
     * \param[in]  pMpdUrl         The URL of the playlist.
     *
     * \param[in]  pKeyAttr        Key information of the segment that has been received.
     *
     * \param[in]  pUserData       The user data passed when the callback was
     *                             originally registered.
     *
     * \returns                    If the DRM is supporting that key attribute, then it should return <b>zero(0)</b>.
     *                             Then NexPlayer will call drm callbacks depending on the encrypt method.
     *                             If the DRM is not supporing the key, then it should return a <b>non-zero value</b>.
     *                             In this case, NexPlayer will not call the DRM callbacks and it will decrypt using its internal decryption function.
     *
     * \since version 6.49
     */
	typedef int (*NEXPLAYERHLSIsSupportKeyCallbackFunc)(char*		pMpdUrl,		// [in] Original Url of the currently playing Mpd.
														char*		pKeyAttr,		// [in] KeyInfo Attribute of the Segment.
														void*		pUserData);
    /**
	  * \ingroup cbtypes
	  * \brief Callback function to create a new instance of the communication channel.
	  *
	  * This is a mandatory API.
	  *
	  * This function provides a new thread to handle messages containing credential information
	  * being sent and received between the user and the server.
	  *
	  * \note   More than one message can be sent in parallel.
	  *
	  * \param[out] a_ppHandle      NexPlayer&trade;&nbsp;HTTP stack handle.
	  * \param[in] uTaskPriority  Priority of Task.
	  * \param[in] uTaskStackSize Size of task stack.
	  * \param[in] pUserData      The user data passed when the callback was originally registered.
	  *
	  * \returns  Zero if successful otherwise a non-zero error code.
	  *
	  * \see NEXPLAYERNexHTTPDownloaderCreateCallbackFunc
	  *
	  * \since version 6.26
	  */
	typedef int (*NEXPLAYERNexHTTPDownloaderCreateCallbackFunc) (void **a_ppHandle, //[OUT]
												unsigned int *uTaskPriority, //[IN]
												unsigned int *uTaskStackSize, //[IN]
												void*a_pUserData); //[IN]

    /**
	  * \ingroup cbtypes
	  * \brief Callback function to destroy the communication channel.
	  *
	  * This is a mandatory API.
	  *
	  * \note This function will not destroy messages that are in the middle of
	  * being transferred. It is the caller's responsibility to cancel messages prior to calling this API.
	  *
	  * \param[in] a_pHandle  NexPlayer&trade;&nbsp;HTTP stack handle.
	  * \param[in] pUserData  The user data passed when the callback was originally registered.
	  *
	  * \returns  Zero if successful otherwise a non-zero error code.
	  *
	  * \see NEXPLAYERNexHTTPDownloaderDestroyCallbackFunc
	  *
	  * \since version 6.26
	  */
	typedef int (*NEXPLAYERNexHTTPDownloaderDestroyCallbackFunc) (void* a_pHandle,	 //[IN]
												void*a_pUserData);						 //[IN]

	/**
	  * \ingroup cbtypes
 	  * \brief Callback function to create a new message handle for later use with <em>cmc_send_message</em>.
	  *
	  * This is a mandatory API.
	  *
	  * \param[in] a_pHandle   NexPlayer&trade;&nbsp;HTTP stack handle.
	  * \param[out] a_uMsgID  Handle of the specific message.
	  * \param[in] pUserData	The user data passed when the callback was originally registered.
	  *
	  * \returns  Zero if successful otherwise a non-zero error code.
	  *
	  * \see NEXPLAYERNexHTTPDownloaderCreateMessageCallbackFunc
	  *
	  * \since version 6.26
	  */
	typedef int (*NEXPLAYERNexHTTPDownloaderCreateMessageCallbackFunc) (void* a_pHandle,  //[IN]
												unsigned int *a_uMsgID,					//[OUT]
												void*a_pUserData);						 //[IN]

	/**
	  * \ingroup cbtypes
	  * \brief Callback function to destroy the handle of a specific message.
	  *
	  * This is a mandatory API.
	  *
	  * \note This function will not cancel messages that are in the middle of
	  * being transferred. It is the caller's responsibility to cancel messages prior to calling this API.
	  *
	  * \param[in] a_pHandle   NexPlayer&trade;&nbsp;HTTP stack handle.
	  * \param[out] a_uMsgID  Handle for the specific message.
	  * \param[in] pUserData	The user data passed when the callback was originally registered.
	  *
	  * \returns  Zero if successful otherwise a non-zero error code.
	  *
	  * \see NEXPLAYERNexHTTPDownloaderDestroyMessageCallbackFunc
	  *
	  * \since version 6.26
      */
	typedef int (*NEXPLAYERNexHTTPDownloaderDestroyMessageCallbackFunc) (void* a_pHandle,  //[IN]
												unsigned int a_uMsgID,								//[OUT]
												void*a_pUserData);								 //[IN]

    /**
      * \ingroup cbtypes
	  * \brief Callback function to create s new message and start sending it asynchronously.
	  *
	  * This is a mandatory API.
	  *
	  * After returning this call, the message will be sent and callbacks will begin being called.
	  *
	  * \param[in] a_pHandle    NexPlayer&trade;&nbsp;HTTP stack handle.
	  * \param[in] a_uMsgID     Handle for the specific message.
	  * \param[in] a_pMsgParam  Settings for the specific message.
	  * \param[out] a_pCBList   List of callbacks.
	  * \param[in] pUserData	  The user data passed when the callback was originally registered.
	  *
	  * \returns  Zero if successful otherwise a non-zero error code.
	  *
	  * \see NEXPLAYERNexHTTPDownloaderSendMessageCallbackFunc
	  *
	  * \since version 6.26
      */
	typedef int (*NEXPLAYERNexHTTPDownloaderSendMessageCallbackFunc) (void* a_pHandle,  //[IN]
												unsigned int a_uMsgID, 							//[IN]
												 NEXHD_CALLBACK_SENDMSG_PARAM *a_pMsgParam, //[IN]
												 NEXHD_CALLBACK_SENDMSG_CBLIST *a_pCBList,	//[OUT]
												void*a_pUserData);							 //[IN]
    /**
      * \ingroup cbtypes
	  * \brief Callback function to cancel a message.
	  *
	  * This is a mandatory API.
	  *
	  * \note This call is synchronous and waits until a message is cancelled.
	  *
	  * \param[in] a_pHandle    NexPlayer&trade;&nbsp;HTTP stack handle.
	  * \param[out] a_uMsgID   Handle for the specific message.
	  * \param[in] pUserData	 The user data passed when the callback was originally registered.
	  *
	  * \returns  Zero if successful otherwise a non-zero error code.
	  *
	  * \see NEXPLAYERNexHTTPDownloaderCancelMessageCallbackFunc
	  *
	  * \since version 6.26
      */
	typedef int (*NEXPLAYERNexHTTPDownloaderCancelMessageCallbackFunc) (void* a_pHandle,  //[IN]
												 unsigned int a_uMsgID,								//[OUT]
												void*a_pUserData);						 		//[IN]

    /**
      * \ingroup cbtypes
	  * \brief Callback function to pause a message.
	  *
	  * This is an optional API.
	  *
	  * \param[in] a_pHandle    NexPlayer&trade;&nbsp;HTTP stack handle.
	  * \param[out] a_uMsgID   Handle for the specific message.
	  * \param[in] pUserData	 The user data passed when the callback was originally registered.
	  *
	  * \returns  Zero if successful otherwise a non-zero error code.
	  *
	  * \see NEXPLAYERNexHTTPDownloaderPauseMessageCallbackFunc
	  *
	  * \since version 6.26
      */
	typedef int (*NEXPLAYERNexHTTPDownloaderPauseMessageCallbackFunc) (void* a_pHandle,  //[IN]
												 unsigned int a_uMsgID,								//[OUT]
												void*a_pUserData);								 //[IN]

	/**
      * \ingroup cbtypes
	  * \brief Callback function to resume a message.
	  *
	  * This is an optional API.
	  *
	  * \param[in] a_pHandle   NexPlayer&trade;&nbsp;HTTP stack handle.
	  * \param[out] a_uMsgID  Handle for the specific message.
	  * \param[in] pUserData	The user data passed when the callback was originally registered.
	  *
	  * \returns  Zero if successful otherwise a non-zero error code.
	  *
	  * \see NEXPLAYERNexHTTPDownloaderResumeMessageCallbackFunc
	  *
	  * \since version 6.26
      */
	typedef int (*NEXPLAYERNexHTTPDownloaderResumeMessageCallbackFunc) (void* a_pHandle,  //[IN]
												 unsigned int a_uMsgID,								//[OUT]
												void*a_pUserData);								 //[IN]

    /**
      * \ingroup cbtypes
	  * \brief Callback function to set parcel information in the NexPlayer&trade;&nbsp;HTTP stack handle.
	  *
	  * This is an optional API.
	  *
	  * \param[in] a_pHandle  NexPlayer&trade;&nbsp;HTTP stack handle.
	  * \param[in] a_eType    Type of enumeration.
	  * \param[out] a_pParcel Parcel Pointer.
	  * \param[in] pUserData	The user data passed when the callback was originally registered.
	  *
	  * \returns  Zero if successful otherwise a non-zero error code.
	  *
	  * \see NEXPLAYERNexHTTPDownloaderSetInfoCallbackFunc
	  *
	  * \since version 6.23
      */
	typedef int (*NEXPLAYERNexHTTPDownloaderSetInfoCallbackFunc) (void* a_pHandle,  			//[IN]
												 NEXHD_CALLBACK_SETINFO_TYPE a_eType, 			//[IN]
												 NEXHD_CALLBACK_SETINFO_PARCEL *a_pParcel,		//[OUT]
												void*a_pUserData);								 //[IN]
    /**
      * \ingroup cbtypes
	  * \brief Callback function to get the parcel information set by the NexPlayer&trade;&nbsp;HTTP stack handle.
	  *
	  * This is an optional API.
	  *
	  * \param[in] a_pHandle   NexPlayer&trade;&nbsp;HTTP stack handle.
	  * \param[in] a_eType     Type of enumeration.
	  * \param[out] a_pParcel  Parcel pointer.
	  * \param[in] pUserData	 The user data passed when the callback was originally registered.
	  *
	  * \returns  Zero if successful otherwise a non-zero error code.
	  *
	  * \see NEXPLAYERNexHTTPDownloaderGetInfoCallbackFunc
	  *
	  * \since version 6.23
        	*/
	typedef int (*NEXPLAYERNexHTTPDownloaderGetInfoCallbackFunc) (void* a_pHandle,  			//[IN]
												 NEXHD_CALLBACK_GETINFO_TYPE a_eType, 			//[IN]
												 NEXHD_CALLBACK_GETINFO_PARCEL *a_pParcel,		//[OUT]
												void*a_pUserData);						 		//[IN]
												 
    /**
      * \ingroup     types
      * \brief       A structure holding function pointers to all of the functions that
      *              comprise the NexPlayer&trade;&nbsp; HTTP stack interface.
      *
      * This structure provides replacements for the NexPlayer&trade;&nbsp; HTTP stack functions.
      * All calls to \c create or \c destroy, for example, are directed to the function pointers in this structure instead.
      *
      * More information about each function pointer can be found in the documentation.
      *
      * \see  ::NEXPLAYERNexHTTPDownloaderCreateCallbackFunc
      * \see  ::NEXPLAYERNexHTTPDownloaderDestroyCallbackFunc
      * \see  ::NEXPLAYERNexHTTPDownloaderCreateMessageCallbackFunc
      * \see  ::NEXPLAYERNexHTTPDownloaderDestroyMessageCallbackFunc
      * \see  ::NEXPLAYERNexHTTPDownloaderSendMessageCallbackFunc
      * \see  ::NEXPLAYERNexHTTPDownloaderCancelMessageCallbackFunc
      * \see  ::NEXPLAYERNexHTTPDownloaderPauseMessageCallbackFunc
      * \see  ::NEXPLAYERNexHTTPDownloaderResumeMessageCallbackFunc
      * \see  ::NEXPLAYERNexHTTPDownloaderSetInfoCallbackFunc
      * \see  ::NEXPLAYERNexHTTPDownloaderGetInfoCallbackFunc
      * 
      * \since version 6.23
      */
	typedef struct NEXPLAYERNexHTTPDownloaderInterface_
	    {
	        NEXPLAYERNexHTTPDownloaderCreateCallbackFunc      		Create;
	        NEXPLAYERNexHTTPDownloaderDestroyCallbackFunc     		Destroy;
	        NEXPLAYERNexHTTPDownloaderCreateMessageCallbackFunc     CreateMsg;
	        NEXPLAYERNexHTTPDownloaderDestroyMessageCallbackFunc    DestroyMsg;
	        NEXPLAYERNexHTTPDownloaderSendMessageCallbackFunc    	SendMsg;
	        NEXPLAYERNexHTTPDownloaderCancelMessageCallbackFunc     CancelMsg;
	        NEXPLAYERNexHTTPDownloaderPauseMessageCallbackFunc      PauseMsg;
	        NEXPLAYERNexHTTPDownloaderResumeMessageCallbackFunc     ResumeMsg;
	        NEXPLAYERNexHTTPDownloaderSetInfoCallbackFunc      		SetInfo;
	        NEXPLAYERNexHTTPDownloaderGetInfoCallbackFunc      		GetInfo;
	} NEXPLAYERNexHTTPDownloaderInterface;
												 
    /**
     * \ingroup     types
     * \brief       File handle used in <em>Remote File I/O</em> callbacks.
     *
     * This is the file handle used in calls to the various <em>Remote File I/O</em>
     * callback functions.  This value is returned by the file-open callback, and
     * can be any value that the remote file callbacks can use to uniquely
     * identify the open file instance.
     */
   typedef      void*    NEXFileHandle;

    /**
     * \ingroup     types
     * \brief       File open mode.
     *
     * This is passed by NexPlayer&trade;&nbsp;in calls to the
     * ::NEXPLAYERRemoteFile_OpenFt callback.
     *
     * This is a bitfield, so the constants can be combined with the bitwise-or
     * operator.
     *
     * \code
     * NEX_FILE_WRITE | NEX_FILE_CREATE // Open file for writing; create if it doesn't exist
     * NEX_FILE_READ | NEX_FILE_WRITE // Same as NEX_FILE_READWRITE
     * \endcode
     */
    typedef enum _NEXFileMode
    {
        /** Open for reading                                                                           */
        NEX_FILE_READ           = 1,
        /** Open for writing                                                                           */
        NEX_FILE_WRITE          = 2,
        /** Open for reading and writing                                                               */
        NEX_FILE_READWRITE      = 3,
        /** Create the file if it doesn't exist                                                        */
        NEX_FILE_CREATE         = 4

    } NEXFileMode;

    /**
     * \ingroup     types
     * \brief       Origin for Remove File I/O callback seek operations.
     *
     * \see ::NEXPLAYERRemoteFile_SeekFt
     * \see ::NEXPLAYERRemoteFile_Seek64Ft
     */
    typedef enum _NEXFileSeekOrigin
    {
        /** Beginning of file         */
        NEX_SEEK_BEGIN          = 0,
        /** Current position          */
        NEX_SEEK_CUR            = 1,
        /** End of file               */
        NEX_SEEK_END            = 2

    } NEXFileSeekOrigin;

    /**
     * \ingroup cbtypes
     * \brief   Remote File I/O callback for opening a file.
     *
     * This is one of several callback functions that can be registered using
     * the \ref rfio Interface in order to replace the system
     * calls normally used for opening and accessing files.
     *
     * \param[in]  pFileName        Path and filename of the file to be opened.  This is the path
     *                              that the application originally passed to NexPlayer&trade;, so
     *                              the application may treat it in any way appropriate in the callback.
     *
     * \param[in]  iMode            Specifies how the file is to be opened; see ::NEXFileMode for details.
     *
     * \param[in]  pUserData        The user data passed when the callback was
     *                              originally registered.
     *
     * \returns                     The handle of the opened file, or -1 if an error occurred.
     */
    typedef NEXFileHandle ( *NEXPLAYERRemoteFile_OpenFt ) ( char* pFileName, NEXFileMode iMode, void *pUserData );

    /**
     * \ingroup cbtypes
     * \brief   Remote File I/O callback for closing a file.
     *
     * This is one of several callback functions that can be registered using
     * the \ref rfio Interface in order to replace the system
     * calls normally used for opening and accessing files.
     *
     * \param[in]  hFile            File handle (as returned by ::NEXPLAYERRemoteFile_OpenFt) of file
     *                              to be closed.
     *
     * \param[in]  pUserData        The user data passed when the callback was
     *                              originally registered.
     *
     * \returns                     0 if successful, or -1 if an error occurred.
     */
    typedef int ( *NEXPLAYERRemoteFile_CloseFt ) ( NEXFileHandle hFile, void *pUserData );

    /**
     * \ingroup cbtypes
     * \brief   Remote File I/O callback for reading a file.
     *
     * This is one of several callback functions that can be registered using
     * the \ref rfio Interface in order to replace the system
     * calls normally used for opening and accessing files.
     *
     * The actual number of bytes to read is \c (uiSize * \c uiCount).
     *
     * \note    This supports read ui size and ui count of up to 32-bits.  For large ui size and ui count,
     *          ::NEXPLAYERRemoteFile_Read64Ft will be called instead.  If the
     *          64-bit callback is not registered, file with sizes over 2GB will
     *          not be supported.
     *
     * \param[in]  hFile            File handle (as returned by ::NEXPLAYERRemoteFile_OpenFt) of file
     *                              to read from.
     *
     * \param[out] pBuf             Buffer to receive the data.
     *
     * \param[in]  uiSize           Record size, in bytes.
     *
     * \param[in]  uiCount          Number of records to read.
     *
     * \param[in]  pUserData        The user data passed when the callback was
     *                              originally registered.
     *
     * \returns
     *                              - &gt;0: The number of bytes actually read
     *                              - 0: Reached the end of the file.
     *                              - -1: An error occurred.
     */
    typedef int ( *NEXPLAYERRemoteFile_ReadFt ) ( NEXFileHandle hFile, void *pBuf, unsigned int uiSize, unsigned int uiCount, void *pUserData );

	/**
     * \ingroup cbtypes
     * \brief   Remote File I/O callback for reading a file.
     *
     * This is one of several callback functions that can be registered using
     * the \ref rfio Interface in order to replace the system
     * calls normally used for opening and accessing files.
     *
     * The actual number of bytes to read is \c (uiSize * \c uiCount).
     *
     * \note    This supports read ui size and ui count of up to 64-bits.  Implement this
     *          callback if you wish to support files over 2GB in size.
     *
     * \param[in]  hFile            File handle (as returned by ::NEXPLAYERRemoteFile_OpenFt) of file
     *                              to read from.
     *
     * \param[out] pBuf             Buffer to receive the data.
     *
     * \param[in]  uiSize           Record size, in bytes.
     *
     * \param[in]  uiCount          Number of records to read.
     *
     * \param[in]  pUserData        The user data passed when the callback was
     *                              originally registered.
     *
     * \returns
     *                              - &gt;0: The number of bytes actually read
     *                              - 0: Reached the end of the file.
     *                              - -1: An error occurred.
     */
    typedef long long ( *NEXPLAYERRemoteFile_Read64Ft ) ( NEXFileHandle hFile, void *pBuf, unsigned long long uiSize, unsigned long long uiCount, void *pUserData );

    /**
     * \ingroup cbtypes
     * \brief   Remote File I/O callback for seeking a file.
     *
     * This is one of several callback functions that can be registered using
     * the \ref rfio Interface in order to replace the system
     * calls normally used for opening and accessing files.
     *
     * This sets the location in the file at which the next <em>read</em> operation
     * will occur.
     *
     * \note    This supports seek offsets of up to 32-bits.  For large offsets,
     *          ::NEXPLAYERRemoteFile_Seek64Ft will be called instead.  If the
     *          64-bit callback is not registered, file with sizes over 2GB will
     *          not be supported.
     *
     * \param[in]  hFile            File handle (as returned by ::NEXPLAYERRemoteFile_OpenFt) of file
     *                              to be seeked.
     *
     * \param[in]  iOffset          Seek destination, as an offset in bytes from \c iOrigin
     *
     * \param[in]  iOrigin          Origin for \c iOffset.  See ::NEXFileSeekOrigin for possible values.
     *
     * \param[in]  pUserData        The user data passed when the callback was
     *                              originally registered.
     *
     * \returns                     New offset from beginning of file, or -1 if an error occurred.
     */
    typedef int ( *NEXPLAYERRemoteFile_SeekFt ) ( NEXFileHandle hFile, int iOffset, NEXFileSeekOrigin iOrigin, void *pUserData );

    /**
     * \ingroup cbtypes
     * \brief   Remote File I/O callback for seeking a file.
     *
     * This is one of several callback functions that can be registered using
     * the \ref rfio Interface in order to replace the system
     * calls normally used for opening and accessing files.
     *
     * This sets the location in the file at which the next <em>read</em> operation
     * will occur.
     *
     * \note    This supports seek offsets of up to 64-bits.  Implement this
     *          callback if you wish to support files over 2GB in size.
     *
     * \param[in]  hFile            File handle (as returned by ::NEXPLAYERRemoteFile_OpenFt) of file
     *                              to be seeked.
     *
     * \param[in]  iOffset          Seek destination, as an offset in bytes from \c iOrigin
     *
     * \param[in]  iOrigin          Origin for \c iOffset.  See ::NEXFileSeekOrigin for possible values.
     *
     * \param[in]  pUserData        The user data passed when the callback was
     *                              originally registered.
     *
     * \returns                     New offset from beginning of file, or -1 if an error occurred.
     */
    typedef long long ( *NEXPLAYERRemoteFile_Seek64Ft ) ( NEXFileHandle hFile, long long iOffset, NEXFileSeekOrigin iOrigin, void *pUserData );

    /**
     * \ingroup cbtypes
     * \brief   Remote File I/O callback for writing to a file.
     *
     * This is one of several callback functions that can be registered using
     * the \ref rfio Interface in order to replace the system
     * calls normally used for opening and accessing files.
     *
     * \param[in]  hFile            File handle (as returned by ::NEXPLAYERRemoteFile_OpenFt) of file
     *                              to be written to.
     *
     * \param[in]  pBuf             The data to write to file
     *
     * \param[in]  dwSize           The number of bytes to write to file
     *
     * \param[in]  pUserData        The user data passed when the callback was
     *                              originally registered.
     *
     * \returns                     The actual number of bytes written, or -1 if an error occurred.
     */
    typedef long long ( *NEXPLAYERRemoteFile_WriteFt ) ( NEXFileHandle hFile, char* pBuf, unsigned int dwSize, void *pUserData );

    /**
     * \ingroup cbtypes
     * \brief   Remote File I/O callback for getting the size of a file.
     *
     * This is one of several callback functions that can be registered using
     * the \ref rfio Interface in order to replace the system
     * calls normally used for opening and accessing files.
     *
     * This callback should return the size of the file <em>without</em> modifying
     * the position to which the file has been seeked (if the seek location must
     * be moved to determine the size, this function should move it back afterwards).
     *
     * \param[in]  hFile            File handle (as returned by ::NEXPLAYERRemoteFile_OpenFt) of the ile
     *                              for which the size should be retrieved.
     *
     * \param[in]  pUserData        The user data passed when the callback was
     *                              originally registered.
     *
     * \returns                     The actual number of bytes written, or -1 if an error occurred.
     */
    typedef long long ( *NEXPLAYERRemoteFile_SizeFt ) ( NEXFileHandle hFile, void *pUserData );


    /**
     * \ingroup     types
     * \brief       A structure holding function pointers to all of the functions that
     *              comprise the Remote File I/O interface.
     *
     * This structure provides replacements for the standard operating system file I/O functions.
     * Basically, to play back local content that is not available via the standard operating system file APIs,
     * all calls to open or read from the file are directed to the function pointers in this structure instead.
     *
     * This structure is passed to the \ref rfio Interface
     * when registering the callbacks.
     *
     * More information about each function pointer can be found in the documentation.
     *
     * \see  ::NEXPLAYERRemoteFile_OpenFt
     * \see  ::NEXPLAYERRemoteFile_CloseFt
     * \see  ::NEXPLAYERRemoteFile_ReadFt
     * \see  ::NEXPLAYERRemoteFile_Read64Ft
     * \see  ::NEXPLAYERRemoteFile_SeekFt
     * \see  ::NEXPLAYERRemoteFile_Seek64Ft
     * \see  ::NEXPLAYERRemoteFile_WriteFt
     * \see  ::NEXPLAYERRemoteFile_SizeFt
     */
    typedef struct NEXPLAYERRemoteFileIOInterface_
    {
        /** Open callback (see ::NEXPLAYERRemoteFile_OpenFt) */
        NEXPLAYERRemoteFile_OpenFt      Open;
        /** Close callback (see ::NEXPLAYERRemoteFile_CloseFt) */
        NEXPLAYERRemoteFile_CloseFt     Close;
        /** Read callback (see ::NEXPLAYERRemoteFile_ReadFt) */
        NEXPLAYERRemoteFile_ReadFt      Read;
        /** Read64 callback (see ::NEXPLAYERRemoteFile_Read64Ft) */
		NEXPLAYERRemoteFile_Read64Ft      Read64;
        /** Seek callback (see ::NEXPLAYERRemoteFile_SeekFt) */
        NEXPLAYERRemoteFile_SeekFt      Seek;
        /** Seek64 callback (see ::NEXPLAYERRemoteFile_Seek64Ft) */
        NEXPLAYERRemoteFile_Seek64Ft    Seek64;
        /** Write callback (see ::NEXPLAYERRemoteFile_WriteFt) */
        NEXPLAYERRemoteFile_WriteFt     Write;
        /** Size callback (see ::NEXPLAYERRemoteFile_SizeFt)  */
        NEXPLAYERRemoteFile_SizeFt      Size;
    } NEXPLAYERRemoteFileIOInterface;


    typedef int (*NEXPLAYERDrmTypeAcceptedCallbackFunc) (unsigned char * systemId[], unsigned int arraySize, unsigned int idLen, void* pUserData);
	
    typedef int (*NEXPLAYERInitMediaDrmCallbackFunc) (unsigned char *    pUUID,
                                                    unsigned int        nUUIDLen,
                                                    unsigned char *		pPssh,
                                                    unsigned int        nPsshLen,
                                                    int                 eMediaType,
                                                    void *			    pUserData );

    typedef int (*NEXPLAYERMediaDrmDecryptSampleCallbackFunc) (unsigned char* pKeyId,
                                                    unsigned char* pInputBuffer,  unsigned int uiInputBufferSize, 
                                                    unsigned char* pOutputBuffer, unsigned int* puiOutputBufferSize,
                                                    unsigned char* pIV, unsigned int uiIVSize,
                                                    void* pSampleEntry, unsigned int uiSameEntrySize,
													unsigned int uEncMode,
													unsigned int uNumBlockClear,
													unsigned int uNumBlockEncrypted,
                                                    void* pUserData);


    typedef int (*NEXPLAYERDeinitMediaDrmCallbackFunc) (void* pUserData, int eMediaType);

#ifdef __cplusplus
}
#endif

#endif  // _NEXPLAYER_JNI_H_
