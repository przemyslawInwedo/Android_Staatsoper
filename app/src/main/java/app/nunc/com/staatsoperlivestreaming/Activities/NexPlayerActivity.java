package app.nunc.com.staatsoperlivestreaming.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import app.nexstreaming.nexplayerengine.NexPlayer;
import app.nexstreaming.nexplayerengine.NexStatisticsMonitor;
import app.nexstreaming.nexplayerengine.NexVideoView;
import app.nunc.com.staatsoperlivestreaming.R;

public class NexPlayerActivity extends AppCompatActivity {

    //NexPlayer
    private NexVideoView nexVideoView;
    private NexStatisticsMonitor nexStatisticsMonitor;

    //UI
    private Button playButton;
    private Button pauseButton;
    private Button changeURIButton;
    private SeekBar volume;
    private boolean paused = false;
    private String dialogURIText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nex_player);

        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        changeURIButton = findViewById(R.id.change_uri_button);
        volume = findViewById(R.id.seekBar);
        nexVideoView = findViewById(R.id.video_view);

        pauseButton.setEnabled(false);
        playButton.setEnabled(false);

        Intent i = getIntent();
        dialogURIText = i.getStringExtra("STREAM_URL");

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!paused)
                    pausePlayer();
                else
                    restartPlayer();
                paused = !paused;
            }
        });

        volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
            float progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                System.out.println(progress);
                nexVideoView.getNexPlayer().setVolume(progress/10.0f);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executePlayer();
                paused = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pauseButton.setText("PAUSE");
                    }
                });
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setTitle("Set video URI");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogURIText = input.getText().toString();
                changeURI();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        changeURIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
            }
        });


        nexVideoView.setVideoURI(Uri.parse("http://live-aec1.performa.nunc.at/media/test-2019-01-22-0/master-allsubs.m3u8"));
        nexVideoView.setOnPreparedListener(new NexVideoView.OnPreparedListener() {
            @Override
            public void onPrepared(NexPlayer mp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playButton.setEnabled(true);
                        pauseButton.setEnabled(true);
                    }
                });
            }
        });

/*
        //Statistics
        nexStatisticsMonitor = new NexStatisticsMonitor(nexVideoView.getNexPlayer());
        nexStatisticsMonitor.setListener(new NexStatisticsMonitor.IStatisticsListener() {
            @Override
            public void onUpdated(int statisticsType, HashMap<NexStatisticsMonitor.IStatistics, Object> map) {
                switch (statisticsType){
                    case NexStatisticsMonitor.STATISTICS_GENERAL:
                        updatePlayTime((Long) map.get(NexStatisticsMonitor.GeneralStatisticsMetric.PLAY_TIME_SEC));
                        break;
                    case NexStatisticsMonitor.STATISTICS_HTTP:
                        break;
                    case NexStatisticsMonitor.STATISTICS_INITIAL:
                        break;
                    case NexStatisticsMonitor.STATISTICS_SYSTEM:
                        break;
                    default:
                }
            }
        });*/

    }

    private void changeURI(){
        pauseButton.setEnabled(false);
        playButton.setEnabled(false);
        nexVideoView.setVideoURI(Uri.parse(dialogURIText));
    }

    private void executePlayer() {
        nexVideoView.start();
    }

    private void pausePlayer(){
        nexVideoView.pause();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pauseButton.setText("RESTART");
            }
        });
    }

    private void restartPlayer(){
        nexVideoView.seekTo(0);
        nexVideoView.pause();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pauseButton.setText("PAUSE");
            }
        });
    }

    private void updatePlayTime(long time){
        System.out.println("Play Time ----> " + time);
    }
}
