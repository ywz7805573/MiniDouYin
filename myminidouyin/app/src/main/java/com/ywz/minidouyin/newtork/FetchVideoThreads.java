package com.ywz.minidouyin.newtork;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ywz.minidouyin.bean.Feed;
import com.ywz.minidouyin.bean.FeedResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;
import retrofit2.Retrofit;

public class FetchVideoThreads {
    private static FetchVideoThreads instance = new FetchVideoThreads();
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    public static int FETCH_BACK=101;
    private List<Feed> list = new ArrayList<>();
    public static final String TAG = "Video FETCHFEED";

    private FetchVideoThreads(){
        super();
    }

    public static FetchVideoThreads getInstance(){
        return instance;
    }

    public void fetch_request(final MediaPlayer mediaPlayer, final Uri resource, final Activity activity){
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if(resource!=null) {
                        Log.d(TAG, "fetch_request: "+resource);
                        mediaPlayer.setDataSource(activity, resource);
                        mediaPlayer.prepare();
                        Log.d(TAG, "fetch_request: end prepare");
                    }
                }catch (Exception e){
                    Log.d(TAG, "fetch_request: "+e.getMessage());
                }
            }
        });
    }

    public List<Feed> getList() {
        return list;
    }
}
