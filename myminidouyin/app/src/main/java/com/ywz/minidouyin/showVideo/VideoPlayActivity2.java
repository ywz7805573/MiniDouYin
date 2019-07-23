package com.ywz.minidouyin.showVideo;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ywz.minidouyin.R;

public class VideoPlayActivity2 extends AppCompatActivity {
    public static final String VIDEO = "VIDEO";
    public static final String PIC = "PIC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        VideoPlayFragment videoPlayFragment = VideoPlayFragment.newInstance();
        Uri videouri = getIntent().getParcelableExtra(VIDEO);
        Uri imageuri = getIntent().getParcelableExtra(PIC);
        videoPlayFragment.setResource(videouri);
        videoPlayFragment.setImageResource(imageuri);
        getSupportFragmentManager().beginTransaction().replace(R.id.VideoPlayPlaceholder,
                videoPlayFragment).commit();
    }
}
