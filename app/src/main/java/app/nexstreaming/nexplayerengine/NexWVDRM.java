/*
 * DRM Manager class.
 *
 * @copyright:
 * Copyright (c) 2009-2013 NexStreaming Corporation, all rights reserved.
 */

package app.nexstreaming.nexplayerengine;

import android.annotation.SuppressLint;
import android.media.DeniedByServerException;
import android.media.MediaDrm;
import android.media.NotProvisionedException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.String;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * \brief  This class allows NexPlayer&trade;&nbsp;to handle and descramble WideVine HLS content.
 */

public class NexWVDRM implements NexWVDRMSession.IWVDRMSessionListener {
    private static final String TAG = "NexWVDRM";
    private static final int NEXWVDRM_EVENT_MODIFYKEYATTR = 0x1000;
    private static final int NEXWVDRM_EVENT_CDM_REQUEST   = 0x1001;
    public static final Handler mHandler = new Handler();
	protected IWVDrmListener m_listener;
	private INexDRMLicenseListener mLicenseRequestListener;
    private HashMap<String, String> mOptionalHeaderFields = null;
    private byte[] mServiceCertificate = null;

    private static final int   REQ_CACH_SERVICE_CERTIFICATE = 0xFFFFFFFF;

    final private ReentrantLock mLock = new ReentrantLock();
    final private Condition mCertificateRecv = mLock.newCondition();


    /**
     * NexWVDRM constructor.
     */
    public NexWVDRM() {
    }

    /**
     * \brief Initializes and registers the NexWVDRM.
     *
     * When this API was called service certification will be retrieved automatically internally.
     *
     * \param strEngineLibName	The relevant engine library name as a \c string.
     * \param strFilePath       The file path that will save certification files.
     * \param strKeyServerURL   The URL of the Key Server.
     * \param intOfflineMode    Offline Mode. 0: Online, 1: Store only, 2: Retrieve only, 3: Continuously storing mode.
     */
    public int initDRMManager(String strEnginePath, String strFilePath, String strKeyServerURL, int offlineMode)
    {
        return initDRMManagerMulti(null, strEnginePath, strFilePath, strKeyServerURL, offlineMode);
    }

    /**
     * \brief Initializes and registers the NexWVDRM.
     *
     * When this API was called service certification will be retrieved automatically internally.
     * This method is for using multi instances
     *
     * \param nexplayerInstance	The instance of NexPlayer.
     * \param strEngineLibName	The relevant engine library name as a \c string.
     * \param strFilePath       The file path that will save certification files.
     * \param strKeyServerURL   The URL of the Key Server.
     * \param intOfflineMode    Offline Mode. 0: Online, 1: Store only, 2: Retrieve only, 3: Continuously storing mode.
     */
    public int initDRMManagerMulti(Object nexplayerInstance, String strEnginePath, String strFilePath, String strKeyServerURL, int offlineMode)
    {
        NexWVDRMSession serviceCertSession = new NexWVDRMSession(this);
        serviceCertSession.setOptionalHeaderFields(mOptionalHeaderFields);

        mLock.lock();
        mServiceCertificate = null;
        final byte[] reqMsg = {0x08, 0x04};

        if (null != mLicenseRequestListener) {
            new Thread(new Runnable() {
                public void run() {
                    final byte[] resp = mLicenseRequestListener.onLicenseRequest(reqMsg);
                    processResonsde(resp, REQ_CACH_SERVICE_CERTIFICATE);
                }
            }).start();

        } else {
            serviceCertSession.processRequest(0, reqMsg, REQ_CACH_SERVICE_CERTIFICATE, strKeyServerURL);
        }

        try {
            mCertificateRecv.await(10, TimeUnit.SECONDS);
            NexLog.d(TAG, "certificate wait end");
        } catch(InterruptedException e) {
            NexLog.e(TAG, "Conditional exception : " + e.toString());
            NexLog.e(TAG, "Will use service certification for test environment.");
        } finally {
            mLock.unlock();
            if(mServiceCertificate == null) {
                return initDRMManagerMulti(nexplayerInstance, strEnginePath, strFilePath, strKeyServerURL, offlineMode, 0, null);       // use service certification for test environment.
            } else {
                return initDRMManagerMulti(nexplayerInstance, strEnginePath, strFilePath, strKeyServerURL, offlineMode, 2, mServiceCertificate);
            }
        }
    }

    /**
     * \brief Initializes and registers the NexWVDRM.
     *
     * \param strEngineLibName	The relevant engine library name as a \c string.
     * \param strFilePath       The file path that will save certification files.
     * \param strKeyServerURL   The URL of the Key Server.
     * \param intOfflineMode    Offline Mode. 0: Online, 1: Store only, 2: Retrieve only, 3: Continuously storing mode.
	 * \param serviceCertificateType  Pre-loaded Widevine Service Certification Type.
	 *                                0 : Widevine Cloud License Server Test environment
	 *                                1 : Widevine Cloud License Server Production environment
	 *                                2 : Widevine Custom Server. serviceCertificate should be exist.
	 * \param serviceCertificate Widevine custom service certificate data. It's only available when serviceCertificateType is 2.
     */
    public native int initDRMManager(String strEnginePath, String strFilePath, String strKeyServerURL, int offlineMode,
                                     int serviceCertificateType, byte[] serviceCertificate);

    /**
     * For internal use only. Please do not use.
     */
    public native int initDRMManagerMulti(Object nexPlayerHandle, String strEnginePath, String strFilePath, String strKeyServerURL, int offlineMode,
                                     int serviceCertificateType, byte[] serviceCertificate);


	/**
	 *
	 * \brief   Deinitializes the NexWVDRM.
     * Call this function before releasing NexPlayer and NexALFactory.
     */
    public native void releaseDRMManager();

	/**
     * \brief	This method sets optionalParameters when sending requests to the Key Server of NexWVDRM.
     * @param optionalHeaderFields HashMap is included in the key request message to allow a client application to provide additional message parameters to the server.
     */
    public void setNexWVDrmOptionalHeaderFields(HashMap<String, String> optionalHeaderFields)
    {
        mOptionalHeaderFields = optionalHeaderFields;
    }
    private native void setOptionalHeaderFields(Object objHeaderField);
    private native void enableCallback(boolean enable);

	public native void enableWVDRMLogs(boolean enable);
	public native void setProperties(int properties, int value);
    public native void processCdmResponse(byte[] response, int cach);

    //Internal use only.
    private int mNativeContext = 0;

    static {
        System.loadLibrary("nexwvdrm");
    }

    /**
     * \brief Registers a callback that will be invoked when new events occur.
     *
     * @param listener INexDRMLicenseListener: the object on which methods will be called when new events occur.
     *            This must implement the \c INexDRMLicenseListener interface.
     */
    public void setLicenseRequestListener(INexDRMLicenseListener listener)
    {
        if(listener != null) {
            mLicenseRequestListener = listener;
        }
    }

	/**
	 * \brief Registers a callback that will be invoked when new events occur.
	 *
	 * @param listener IWVDrmListener: the object on which methods will be called when new events occur.
	 *            This must implement the \c IWVDrmListener interface.
	 */
    public void setListener(IWVDrmListener listener)
    {
        if(listener != null) {
            enableCallback(true);
            m_listener = listener;
        }
        else
            enableCallback(false);
    }

	/**
	 * \brief The application must implement this interface in order to receive
	 *         events from NexWVDrm.
	 */
	public interface IWVDrmListener {
		/**
		 * \brief This method provides the key attribute that will be used by NexWVDRM when the key attribute is modified.
		 * @param strKeyAttr strKeyAttr is the key attribute that is written in the playlist file.
 		 * @return A \c string with modified key attribute. NexWVDRM will send this \c string without any modification.
		 * 			If modification is not needed, then the UI should return the input parameter: strKeyAttr.
		 */
		public String onModifyKeyAttribute(String strKeyAttr);
	}

	@Override
    public void processResonsde(byte[] arrResponse, int cach)
    {
        if(arrResponse != null) {
            NexLog.d(TAG, "[processResponse] response len:" + arrResponse.length);
        }

        if(cach != REQ_CACH_SERVICE_CERTIFICATE) {
            processCdmResponse(arrResponse, cach);
        } else {    // Service Certification.
            mLock.lock();
            if(arrResponse != null) {
                // Remove 5 byte header
                mServiceCertificate = new byte[arrResponse.length - 5];
                for (int i = 0; i < arrResponse.length - 5; i++) {
                    mServiceCertificate[i] = arrResponse[i + 5];
                }

                String strSC = "";
                for (byte b : mServiceCertificate) {
                    strSC += String.format("%02x", b);
                }

                NexLog.d(TAG, "[processResponse] Service Certification : " + strSC);
            } else {
                NexLog.d(TAG, "[processResponse] Fail to retrieve Service Certification from license server.");
            }

            mCertificateRecv.signal();
            mLock.unlock();
        }
    }



    //will be called by native
    private String callbackFromNativeStringRet(int msg, int type, byte[] reqMsg, int arg3, int cach, Object what)
    {
        NexLog.d(TAG, "[callbackFromNativeStringRet] msg:"+msg+" reqMsg.length:"+reqMsg.length+" cach:"+Integer.toHexString(cach));
        NexLog.d(TAG, "[callbackFromNativeStringRet] URL:" + (String)what);
        switch(msg)
        {
            case NEXWVDRM_EVENT_MODIFYKEYATTR:
            {
                if(m_listener != null)
                {
                    return m_listener.onModifyKeyAttribute((String)what);
                }
                else
                {
                    return (String)what;
                }
            }
            case NEXWVDRM_EVENT_CDM_REQUEST:
            {
                boolean needSession = true;
                if(reqMsg.length > 0 && mLicenseRequestListener != null) {
                    NexLog.d(TAG, "[license delegator] before length" + reqMsg.length);
                    final byte[] resp = mLicenseRequestListener.onLicenseRequest(reqMsg);
                    final int cachVal = cach;
                    if (resp != null && ((byte[])resp).length > 0) {
                        NexLog.d(TAG, "[license delegator] after length" + resp.length);
                        needSession = false;
                        new Thread(new Runnable() {
                            public void run() {
                                processResonsde((byte[])resp, cachVal);
                            }
                        }).start();
                    }  else {
                        NexLog.d(TAG, "[license delegator] Response is null");
                    }
                }
                if(needSession) {
                    NexWVDRMSession WVSession = new NexWVDRMSession(this);
                    if (WVSession != null) {
                        WVSession.setOptionalHeaderFields(mOptionalHeaderFields);
                        WVSession.processRequest(type, reqMsg, cach, what);
                    }
                }
            }
            default:
                return null;
        }
    }
}
