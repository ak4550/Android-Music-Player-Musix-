package com.codinginflow.musicplayertesting;

import androidx.appcompat.app.AppCompatActivity;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_player);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        mHandler = new Handler();

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
                    Song previousSong = AllSongsAdapter.getSong(currentSongPosition);
                    changeActionBarTitle(previousSong);
                    playSong(previousSong);
                }else{
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
                    Song nextSong = AllSongsAdapter.getSong(currentSongPosition+1);
                    changeActionBarTitle(nextSong);
                    playSong(nextSong);
                }else{
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
                            txtStart.setText(String.valueOf(mPlayer.getCurrentPosition()/1000));
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
}