package com.example.musicbox;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer mediaplayer;
    private SeekBar progress;
    private TextView time, timeleft;
    private Button prev, play, next;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (SeekBar) findViewById(R.id.progress);
        time = (TextView) findViewById(R.id.time);
        timeleft = (TextView) findViewById(R.id.timeLeft);
        prev = (Button) findViewById(R.id.prevButton);
        play = (Button) findViewById(R.id.playButton);
        next = (Button) findViewById(R.id.nextButton);
        mediaplayer = new MediaPlayer();
        mediaplayer = MediaPlayer.create(getApplicationContext(), R.raw.got);
        progress.setMax(mediaplayer.getDuration());
        play.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mediaplayer.seekTo(i);
                }
                SimpleDateFormat dateformat = new SimpleDateFormat("mm:ss");
                int duration = mediaplayer.getDuration();
                int currentpos = mediaplayer.getCurrentPosition();
                time.setText(dateformat.format(new Date(currentpos)));
                timeleft.setText(dateformat.format(new Date(duration-currentpos)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.playButton:
                if (mediaplayer.isPlaying()){
                    pauseMusic();
                }else{
                    playMusic();
                }
                break;
            case R.id.prevButton:
                mediaplayer.seekTo(0);
                play.setBackgroundResource(android.R.drawable.ic_media_pause);
                mediaplayer.start();
                break;
            case R.id.nextButton:
                mediaplayer.seekTo(mediaplayer.getDuration());
                play.setBackgroundResource(android.R.drawable.ic_media_play);
        }

    }

    public void pauseMusic(){
        if (mediaplayer!=null){
            mediaplayer.pause();
            play.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }
    public void playMusic(){
        if (mediaplayer!=null){
            updateThread();
            mediaplayer.start();
            play.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }

    public void updateThread(){
        thread = new Thread(){
            @Override
            public void run(){
              {
                    try {
                        while(mediaplayer!=null) {
                            Thread.sleep(50);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int newpos = mediaplayer.getCurrentPosition();
                                    int newmax = mediaplayer.getDuration();
                                    progress.setMax(newmax);
                                    progress.setProgress(newpos);
                                    time.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss").format(new Date(newpos))));
                                    timeleft.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss").format(new Date(newmax-newpos))));
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
            };
        thread.start();

    }

    @Override
    protected void onDestroy() {
        if(mediaplayer!=null&&mediaplayer.isPlaying()){
            mediaplayer.stop();
            mediaplayer.release();
            mediaplayer = null;
        }
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}
