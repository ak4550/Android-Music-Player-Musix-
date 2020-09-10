package com.codinginflow.musicplayertesting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.codinginflow.musicplayertesting.adapters.AllSongsAdapter;
import com.codinginflow.musicplayertesting.utils.MyPlayer;
import com.codinginflow.musicplayertesting.utils.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MainPlayerActivity extends AppCompatActivity {

    private static final String TAG = "MainPlayerActivity";

    private Button player_play_btn, player_previous_btn, player_next_btn;
    private ImageView imgPlayer;
    private TextView txtStart, txtEnd;
    private ArrayList<Song> songList;
    private Song currentSong;
    private int currentSongPosition;
    private MyPlayer mPlayer;
    private SeekBar seekBar;
    private Handler animationHandler = new Handler();
    private Handler mHandler;
    private Runnable runnable;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_player);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        mHandler = new Handler();
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(mHandler.hasCallbacks(runnable)){
            mHandler.removeCallbacks(runnable);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        currentSongPosition = bundle.getInt("songPosition");
        currentSong = AllSongsAdapter.getSong(bundle.getInt("songPosition"));

        changeActionBarTitle(currentSong);

        mPlayer = MyPlayer.getPlayerInstance();

        if(mPlayer != null){
            Log.d(TAG, "onCreate: Media Player is not null so cleaning");
            MyPlayer.clearInstance();
            Log.d(TAG, "onCreate: 'Media Player is null so trying to get the instance");
            mPlayer = MyPlayer.getPlayerInstance();

            if(mPlayer != null){
                Log.d(TAG, "onCreate: Successfully get the instance of player");
                // Song song = (Song)bundle.get("song");
                playSong(currentSong);
                player_play_btn.setBackgroundResource(R.drawable.ic_pause_button);
            }else{
                Log.d(TAG, "onCreate: something is wrong");
            }

        }else{
            MyPlayer.clearInstance();
            mPlayer = MyPlayer.getPlayerInstance();

            if(mPlayer != null){
                playSong(currentSong);

                Log.d(TAG, "onCreate: else block got the instance");
            }else
            Log.d(TAG, "onCreate: fuck you");
        }

//        if(mPlayer.isPlaying()){
//            seekBar.setMax(mPlayer.getDuration());
//
//        }

        player_previous_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPlayer.clearInstance();
                mPlayer = MyPlayer.getPlayerInstance();
                if(--currentSongPosition <= AllSongsAdapter.getListSize()){
                    currentSong = AllSongsAdapter.getSong(currentSongPosition);
                    changeActionBarTitle(currentSong);
                    playSong(currentSong);
                }else{
                    currentSong = AllSongsAdapter.getSong(AllSongsAdapter.getListSize());
                    changeActionBarTitle(AllSongsAdapter.getSong(AllSongsAdapter.getListSize()));
                    playSong(AllSongsAdapter.getSong(AllSongsAdapter.getListSize()));
                }

            }
        });

        player_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPlayer.clearInstance();
                mPlayer = MyPlayer.getPlayerInstance();
                if(++currentSongPosition <= AllSongsAdapter.getListSize()){
                    currentSong = AllSongsAdapter.getSong(currentSongPosition+1);
                    changeActionBarTitle(currentSong);
                    playSong(currentSong);
                }else{
                    currentSong = AllSongsAdapter.getSong(0);
                    changeActionBarTitle(AllSongsAdapter.getSong(0));
                    playSong(AllSongsAdapter.getSong(0));
                }
            }
        });

        player_play_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(mPlayer.isPlaying()){
                    mPlayer.pause();
                    player_play_btn.setBackgroundResource(R.drawable.ic_play_button);
                }else if(mPlayer != null){
                        mPlayer.start();
                        player_play_btn.setBackgroundResource(R.drawable.ic_pause_button);

                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && mPlayer != null){
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void changeActionBarTitle(Song song){
        getSupportActionBar().setTitle(song.getTrackName() + " | " + song.getArtistName());
    }

    private void playSong(Song song){

        if(mHandler.hasCallbacks(runnable)){
            mHandler.removeCallbacks(runnable);
        }

        try {

            mPlayer.setDataSource(song.getTrackPath());
            mPlayer.prepare();
            mPlayer.start();
            txtEnd.setText(getTimeFormat(mPlayer.getDuration()/1000));
            player_play_btn.setBackgroundResource(R.drawable.ic_pause_button);
            animatePlayer();


            changeSeekBar();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void animatePlayer(){
        animationHandler.post(new Runnable() {
            @Override
            public void run() {
                imgPlayer.animate().rotationY(360f).setDuration(mPlayer.getDuration()).start();
            }
        });
    }

    private void  initViews(){
        player_next_btn = findViewById(R.id.player_next_btn);
        player_play_btn = findViewById(R.id.player_play_btn);
        player_previous_btn = findViewById(R.id.player_previous_btn);
        seekBar = findViewById(R.id.seekBar);
        imgPlayer = findViewById(R.id.imgPlayer);
        txtStart = findViewById(R.id.txtStart);
        txtEnd = findViewById(R.id.txtEnd);
    }


    private void changeSeekBar(){
        seekBar.setMax(mPlayer.getDuration());
        seekBar.setProgress(mPlayer.getCurrentPosition());
        if(mPlayer.isPlaying()){
            runnable = new Runnable() {
                @Override
                public void run() {
                    try{
                        if(mPlayer.isPlaying()){
                            seekBar.setProgress(mPlayer.getCurrentPosition());
                            txtStart.setText(getTimeFormat(mPlayer.getCurrentPosition()/1000));
                            changeSeekBar();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            mHandler.postDelayed(runnable, 1000);
        }
    }

    @Override
    protected void onPause() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(currentSong.getTrackName())
                .setContentText(currentSong.getArtistName())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .addAction(R.drawable.ic_previous, "Last Song", null)
                .addAction(R.drawable.ic_pause_button, "Now Playing", null)
                .addAction(R.drawable.ic_next, "Next Song", null)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .build();

        notificationManager.notify(10, notification);

        super.onPause();
    }

    private  String getTimeFormat(int seconds){
        int hour = 0;
        int min = 0;
        int sec = 0;
        if(seconds < 60){
            return String.format("%02d:%02d", min, seconds);
        }
        if(seconds >= 60){
            min = seconds / 60;
            sec = seconds - (min * 60);

            if(min >= 60){
                hour = min / 60;
                min = min - (hour * 60);
            }
        }
        if(hour == 0){
            return String.format("%02d:%02d", min, sec);
        }else{
            return String.format("%02d:%02d:%02d", hour, min, sec);
        }
    }
}