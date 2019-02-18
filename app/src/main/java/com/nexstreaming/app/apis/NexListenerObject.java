package com.nexstreaming.app.apis;

import com.nexstreaming.nexplayerengine.NexClosedCaption;
import com.nexstreaming.nexplayerengine.NexDateRangeData;
import com.nexstreaming.nexplayerengine.NexID3TagInformation;
import com.nexstreaming.nexplayerengine.NexPictureTimingInfo;
import com.nexstreaming.nexplayerengine.NexPlayer;
import com.nexstreaming.nexplayerengine.NexPlayer.NexErrorCode;
import com.nexstreaming.nexplayerengine.NexSessionData;

public class NexListenerObject implements NexPlayer.IListener {

	@Override
	public void onEndOfContent(NexPlayer mp) {

	}

	@Override
	public void onStartVideoTask(NexPlayer mp) {
		
	}

	@Override
	public void onStartAudioTask(NexPlayer mp) {
		
	}

	@Override
	public void onTime(NexPlayer mp, int sec) {
		
	}

	@Override
	public void onProgramTime(NexPlayer mp, String strTag, long offset) {
		
	}
	
	@Override
	public void onError(final NexPlayer mp, final NexErrorCode errorcode) {
	}

	@Override
	public void onSignalStatusChanged(NexPlayer mp, int pre, int now) {
		
	}

	@Override
	public void onStateChanged(NexPlayer mp, int pre, int now) {
		
	}

	@Override
	public void onRecordingErr(NexPlayer mp, int err) {
		
	}

	@Override
	public void onRecordingEnd(NexPlayer mp, int success) {
		
	}

	@Override
	public void onRecording(NexPlayer mp, int recDuration, int recSize) {
		
	}

	@Override
	public void onTimeshiftErr(NexPlayer mp, int err) {
		
	}

	@Override
	public void onTimeshift(NexPlayer mp, int currTime, int TotalTime) {
		
	}

	@Override
	public void onAsyncCmdComplete(final NexPlayer mp, int command, final int result,
                                   final int param1, int param2) {
	}

	@Override
	public void onRTSPCommandTimeOut(NexPlayer mp) {
		
	}

	@Override
	public void onPauseSupervisionTimeOut(NexPlayer mp) {
		
	}

	@Override
	public void onDataInactivityTimeOut(NexPlayer mp) {
		
	}

	@Override
	public void onBufferingBegin(NexPlayer mp) {
		
	}

	@Override
	public void onBufferingEnd(NexPlayer mp) {
		
	}

	@Override
	public void onBuffering(NexPlayer mp, int progress_in_percent) {
		
	}

	@Override
	public void onAudioRenderPrepared(NexPlayer mp) {
		
	}

	@Override
	public void onAudioRenderCreate(NexPlayer mp, int samplingRate, int channelNum) {
		
	}

	@Override
	public void onAudioRenderDelete(NexPlayer mp) {
		
	}

	@Override
	public void onVideoRenderPrepared(NexPlayer mp) {
		
	}

	@Override
	public void onVideoRenderCreate(NexPlayer mp, int width, int height,
                                    Object rgbBuffer) {
	}

	@Override
	public void onVideoRenderDelete(NexPlayer mp) {
		
	}

	@Override
	public void onVideoRenderRender(NexPlayer mp) {
		
	}

	@Override
	public void onVideoRenderCapture(NexPlayer mp, int width, int height,
                                     int pixelbyte, Object bitmap) {
		
	}

	@Override
	public void onTextRenderInit(NexPlayer mp, int numTracks) {
		
	}

	@Override
	public void onTextRenderRender(NexPlayer mp, int trackIndex,
                                   NexClosedCaption textInfo) {
		
	}

	@Override
	public void onTimedMetaRenderRender(NexPlayer mp,
                                        NexID3TagInformation TimedMeta) {
		
	}

	@Override
	public void onStatusReport(NexPlayer mp, int msg, int param1) {
		
	}

	@Override
	public void onDownloaderError(NexPlayer mp, int msg, int param1) {
		
	}

	@Override
	public void onDownloaderAsyncCmdComplete(NexPlayer mp, int msg, int param1,
                                             int param2) {
		
	}

	@Override
	public void onDownloaderEventBegin(NexPlayer mp, int param1, int param2) {
		
	}

	@Override
	public void onDownloaderEventProgress(NexPlayer mp, int param1, int param2,
                                          long param3, long param4) {
		
	}

	@Override
	public void onDownloaderEventComplete(NexPlayer mp, int param1) {
		
	}

	@Override
	public void onDownloaderEventState(NexPlayer mp, int param1, int param2) {
		
	}

	@Override
	public void onSessionData(NexPlayer mp, NexSessionData[] data) {

	}

	@Override
	public void onDateRangeData(NexPlayer mp , NexDateRangeData[] data) {

	}

	@Override
	public void onPictureTimingInfo(NexPlayer mp,
                                    NexPictureTimingInfo[] arrPictureTimingInfo) {
		
	}

	@Override
	public void onHTTPResponse(NexPlayer mp, String strResponse) {
		
	}

	@Override
	public void onHTTPRequest(NexPlayer mp, String strRequest) {
		
	}

	@Override
	public String onModifyHttpRequest(NexPlayer mp, int param1, Object input_obj) {
		return (String) input_obj;
	}
	/*
	 */
}
