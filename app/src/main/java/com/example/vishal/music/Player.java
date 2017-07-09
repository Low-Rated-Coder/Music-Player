package com.example.vishal.music;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class Player extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    static MediaPlayer mp;
    ArrayList<File> mySongs;
    int position;
    Bitmap bitmap;
    MediaMetadataRetriever mmr;
    Thread updateSeekBar;
    ImageView album_cover;
    SeekBar sb;
    TextView songTitle;
    ImageButton btPlay,btFF,btFB,btNxt,btPv,btnRepeat,btnShuffle;
    boolean isShuffle = false;
    boolean isRepeat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btPlay = (ImageButton) findViewById(R.id.btPlay);
        btFF = (ImageButton) findViewById(R.id.btFF);
        btFB = (ImageButton) findViewById(R.id.btFB);
        btNxt = (ImageButton) findViewById(R.id.btNxt);
        btPv = (ImageButton) findViewById(R.id.btPv);
        songTitle = (TextView) findViewById(R.id.songTitle);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        album_cover = (ImageView) findViewById(R.id.album_cover);

        sb = (SeekBar) findViewById(R.id.seekBar);
        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getInt("pos",0);

        if(mp!=null &&mp.isPlaying()){
            mp.stop();
            mp.release();
            mp=null;
        }
        mp = new MediaPlayer();
        mmr = new MediaMetadataRetriever();
        playSong(position);

        mp.setOnCompletionListener(this);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });

        btPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        btPlay.setImageResource(android.R.drawable.ic_media_play);
                    }
                }else{
                    if(mp!=null){
                        mp.start();
                        btPlay.setImageResource(android.R.drawable.ic_media_pause);
                    }
                }

            }
        });
        btFF.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int currentPosition = mp.getCurrentPosition();
                if(currentPosition + 5000 <= mp.getDuration()){
                    mp.seekTo(currentPosition + 5000);
                }else{
                    mp.seekTo(mp.getDuration());
                }
            }
        });
        btFB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int currentPosition = mp.getCurrentPosition();
                if(currentPosition - 5000 >= 0){
                    mp.seekTo(currentPosition - 5000);
                }else{
                    mp.seekTo(0);
                }

            }
        });
        btNxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    Random rand = new Random();
                    position = rand.nextInt((mySongs.size() - 1) + 1);
                    playSong(position);
                }
                if(position < (mySongs.size() - 1)){
                    playSong(position + 1);
                    position = position + 1;
                }else{
                    playSong(0);
                    position = 0;
                }

            }
        });
        btPv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(position > 0){
                    playSong(position - 1);
                    position = position - 1;
                }else {
                    playSong(mySongs.size() - 1);
                    position = mySongs.size() - 1;
                }

            }
        });
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }else{
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }else{
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });

    }

    public void  playSong(int songIndex) {
        try {
            mp.reset();
            mp.setDataSource(mySongs.get(songIndex).getPath());
            mp.prepare();
            mp.start();
            songTitle.setText(mySongs.get(songIndex).getName().replace(".mp3","").replace(".wav",""));
            //album_cover.setImageResource(R.drawable.pic2);
            mmr.setDataSource(mySongs.get(songIndex).getPath());
            try {
                byte[] data = mmr.getEmbeddedPicture();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                album_cover.setImageBitmap(bitmap);
            }
            catch (Exception e){
                album_cover.setImageResource(R.drawable.pic2);
            }
            //album_cover.setImageBitmap(bitmap);
            sb.setMax(mp.getDuration());
            updateSeekBar.start();
            btPlay.setImageResource(android.R.drawable.ic_media_pause);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onCompletion(MediaPlayer arg0) {
        if(isRepeat){
            playSong(position);
        } else if(isShuffle){
            Random rand = new Random();
            position = rand.nextInt((mySongs.size() - 1) + 1);
            playSong(position);
        } else{
            if(position < (mySongs.size() - 1)&&mySongs.size()!=0){
                playSong(position + 1);
                position = position + 1;
            }else{
                playSong(0);
                position = 0;
            }
        }
    }
}
