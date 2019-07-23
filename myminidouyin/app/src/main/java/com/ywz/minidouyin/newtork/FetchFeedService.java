package com.ywz.minidouyin.newtork;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;

import com.ywz.minidouyin.bean.Feed;
import com.ywz.minidouyin.bean.FeedResponse;
import com.ywz.minidouyin.db.FeedDBHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FetchFeedService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.ywz.minidouyin.newtork.action.FOO";
    private static final String ACTION_BAZ = "com.ywz.minidouyin.newtork.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.ywz.minidouyin.newtork.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.ywz.minidouyin.newtork.extra.PARAM2";

    public static final String baseUrl="http://test.androidcamp.bytedance.com/";
    public static final String TAG = "FETCHFEED";
    private List<Feed> list = new ArrayList<>();

    public FetchFeedService() {
        super("FetchFeedService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startFetchFeed(Context context, MyResultReceiver param1, String param2) {
        Intent intent = new Intent(context, FetchFeedService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startFetchVideo(Context context, MyResultReceiver param1, String param2) {
        Intent intent = new Intent(context, FetchFeedService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final ResultReceiver param1 = intent.getParcelableExtra(EXTRA_PARAM1);
            final String param2 = intent.getStringExtra(EXTRA_PARAM2);
            if (ACTION_FOO.equals(action)) {
                handleActionFoo(param1, param2);
                param1.send(MyResultReceiver.RECEIVE_FEED,new Bundle());
            } else if (ACTION_BAZ.equals(action)) {
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(ResultReceiver param1, String param2) {
        Retrofit retrofit = RetrofitManager.get(baseUrl);
        try {
            final Response<FeedResponse> response = retrofit.create(IMiniDouyinService.class)
                    .getFeedResponse().execute();
            Log.d(TAG, "run: end Fetch Feed");
            if (response.body().isSuccess()) {
                list = response.body().getFeeds();
                Log.d(TAG, "run: Fetch Feed Succeed");
            } else {
                Log.d(TAG, "run: Fetch Feed Failed");
            }
            (new FeedDBHelper(this)).updateDB(
                    list,this);
        }catch (Exception e){
            Log.d(TAG, "run: "+e.getMessage());
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(ResultReceiver param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
