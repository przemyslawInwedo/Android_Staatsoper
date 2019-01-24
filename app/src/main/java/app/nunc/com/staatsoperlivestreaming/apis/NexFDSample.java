package app.nunc.com.staatsoperlivestreaming.apis;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;

import java.io.IOException;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer;

/**
 * Created by bonnie.kyeon on 2016-04-08.
 */
public class NexFDSample extends NexVideoViewSample {
    
    @Override
    protected void open(Uri uri, String path) {
        AssetFileDescriptor afd;

//      This way is also possible.
//      try {
//          afd = this.getAssets().openFd("overthehorizon.mp3");
//      } catch (IOException e) {
//          e.printStackTrace();
//      }

        afd = this.getResources().openRawResourceFd(R.raw.two_seater);
        if( afd != null ) {
            int result = mVideoView.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            if( result != 0 ) {
                onPlayerError(mVideoView.getNexPlayer(), NexPlayer.NexErrorCode.fromIntegerValue(result));
            }
            try {
                afd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPlayerStart(NexPlayer mp) {
        super.onPlayerStart(mp);

        AssetFileDescriptor afd;

//      This way is also possible.
//      try {
//          afd = this.getAssets().openFd("subtitle.mp3");
//      } catch (IOException e) {
//          e.printStackTrace();
//      }

        afd = this.getResources().openRawResourceFd(R.raw.subtitle);
        if(afd != null) {
            mVideoView.addSubtitleSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

            try {
                afd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
