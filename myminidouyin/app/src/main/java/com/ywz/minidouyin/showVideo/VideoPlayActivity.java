package com.ywz.minidouyin.showVideo;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.ywz.minidouyin.R;

public class VideoPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        Uri uri = getIntent().getData();
        VideoPlayFragment videoPlayFragment = VideoPlayFragment.newInstance();
        videoPlayFragment.setResource(uri);
        getSupportFragmentManager().beginTransaction().replace(R.id.VideoPlayPlaceholder,
                videoPlayFragment).commit();
    }
}
