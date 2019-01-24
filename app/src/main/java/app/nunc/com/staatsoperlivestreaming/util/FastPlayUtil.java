package app.nunc.com.staatsoperlivestreaming.util;


import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexContentInformation;

/**
 * Created by bonnie.kyeon on 2015-07-08.
 */
public class FastPlayUtil {
	private static final int BANDWIDTH_KBPS = 1000;

	public static float getFastPlaySpeed(float currentSpeed, boolean bForward) {
		float maxFastPlaySpeed = 64.0f;
		float minFastPlaySpeed = -64.0f;
		float speed = currentSpeed;

		if( bForward && (currentSpeed < 0 || currentSpeed >= maxFastPlaySpeed) ) {
			speed = 2.0f;
		} else if( !bForward && (currentSpeed > 0 || currentSpeed <= minFastPlaySpeed) ) {
			speed = -2.0f;
		} else {
			speed *= 2.0f;
		}

		return speed;
	}

	private static boolean checkValidBandWidthForFastPlay(int bandwidth, int minBW, int maxBW) {
		return (minBW == 0 && maxBW == 0) || (bandwidth > (minBW * BANDWIDTH_KBPS) && bandwidth < (maxBW * BANDWIDTH_KBPS));
	}

	public static boolean isFastPlayPossible(NexContentInformation info, int minBW, int maxBW) {
		boolean result = false;
		if (info.mSubSrcType == 5)	// fast play is supported for HLS.
		{
			result = true;
		}
		// Fast play is allowed when i-frame only track is not exist.
/*		
 		if( info != null &&	info.mCurrVideoStreamID != NexPlayer.MEDIA_STREAM_DISABLE_ID ) {
			for( int i = 0; i < info.mStreamNum; i++ ) {
				for (int j = 0; j < info.mArrStreamInformation[i].mTrackCount; j++) {
					if( checkValidBandWidthForFastPlay(info.mArrStreamInformation[i].mArrTrackInformation[j].mBandWidth, minBW, maxBW) ) {
						if( info.mArrStreamInformation[i].mArrTrackInformation[j].mIFrameTrack ) {
							result = true;
							break;
						}
					}
				}
			}
		}
*/

		return result;
	}
}
