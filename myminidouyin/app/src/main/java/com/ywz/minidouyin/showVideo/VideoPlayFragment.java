package com.ywz.minidouyin.showVideo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ywz.minidouyin.R;
import com.ywz.minidouyin.newtork.FetchVideoThreads;

import java.io.IOException;


public class VideoPlayFragment extends Fragment {
    public static final String TAG = "VideoPlayFragment";
    private Uri resource;
    private Uri imageResource;

    private SurfaceView surfaceView;
    private ImageView imageView;

    private MediaPlayer mediaPlayer;

    private SeekBar seekBar;
    private CheckBox checkBox;
    private static final int REFRESH_PROCGRESS=1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==REFRESH_PROCGRESS){
//                Log.d(TAG, "handleMessage: ");
                try {
                    double cur = mediaPlayer.getCurrentPosition();
                    double len = mediaPlayer.getDuration();
                    double process = cur / len * 100.0;
                    seekBar.setProgress((int) process);
                    handler.removeMessages(REFRESH_PROCGRESS);
                    handler.sendMessageDelayed(Message.obtain(handler, REFRESH_PROCGRESS),
                            1000);
                }catch (Exception e){
                    Log.d(TAG, "handleMessage: "+e.getMessage());
                }
            }
        }
    };
    private DisplayMetrics displayMetrics;
    private int mScreenWidth;
    private int mScreenHeight;

    public VideoPlayFragment() {
    }


    public static VideoPlayFragment newInstance() {
        VideoPlayFragment fragment = new VideoPlayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayMetrics = new DisplayMetrics();
        this.getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_video_play, container, false);
        surfaceView = view.findViewById(R.id.surface);
        surfaceView.setVisibility(View.INVISIBLE);
        imageView = view.findViewById(R.id.place_image);
        imageView.setVisibility(View.VISIBLE);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchVideoThreads.getInstance().fetch_request(mediaPlayer,resource,getActivity());
            }
        });

        seekBar = view.findViewById(R.id.seekbar);
        seekBar.setEnabled(false);
        checkBox = view.findViewById(R.id.loop_checkbox);
        checkBox.setChecked(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: ");
                if(isChecked){
                    seekBar.setEnabled(false);
                    if (mediaPlayer!=null) mediaPlayer.start();
                }else{
                    seekBar.setEnabled(true);
                    if (mediaPlayer!=null) mediaPlayer.pause();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!fromUser){
                    return;
                }
                try {
                    double len = mediaPlayer.getDuration();
                    double cur = (progress * len / 100.0);
                    Log.d(TAG, "onProgressChanged: " + len + " " + (int) cur);
                    mediaPlayer.seekTo((int) cur);
                }catch (Exception e){
                    Log.d(TAG, "onProgressChanged: "+e.getMessage());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        surfaceView.getHolder().addCallback(new PlayerCallBack());
        makeMediaPlayer();
        return view;
    }

    public void makeMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                checkBox.setChecked(true);
                if(imageResource!=null) {
                    changVisi();
                }
                handler.sendMessage(Message.obtain(handler,REFRESH_PROCGRESS));
                Log.d(TAG, "onPrepared: mediaPlayer started");
            }
        });
    }

    public void changVisi(){
        imageView.setAlpha(1f);
        surfaceView.setAlpha(0f);
        surfaceView.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofFloat(0,1);
        animator.setDuration(500);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imageView.setAlpha(1f-(float)animation.getAnimatedValue());
                surfaceView.setAlpha((float)animation.getAnimatedValue());
                if(imageView.getAlpha()<0.1f){
                    imageView.setVisibility(View.GONE);
                }
            }
        });
        animator.start();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        if(!getUserVisibleHint()){
            return;
        }
        show();
    }

    public void show(){
        Log.d(TAG, "show: ");
        if(imageResource!=null){
            Glide.with(getView()).load(imageResource).into(imageView);
        }else{
            imageView.setVisibility(View.GONE);
            surfaceView.setVisibility(View.VISIBLE);
            FetchVideoThreads.getInstance().fetch_request(mediaPlayer,resource,getActivity());
        }
    }

    public void releaseMedia(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        handler.removeMessages(REFRESH_PROCGRESS);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "setUserVisibleHint: "+isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isResumed()){
            show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: "+resource);
        releaseMedia();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        super.onDetach();
        releaseMedia();
    }

    public void setResource(Uri resource) {
        if(resource!=null) {
            this.resource = resource;
        }
    }

    public void setImageResource(Uri imageResource) {
        this.imageResource = imageResource;
    }

    private class PlayerCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mediaPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }
}
