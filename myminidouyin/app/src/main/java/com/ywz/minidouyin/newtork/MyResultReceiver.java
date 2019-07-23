package com.ywz.minidouyin.newtork;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;

public class MyResultReceiver extends ResultReceiver {
    public static final int RECEIVE_FEED=1;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    private Handler handler;
    public MyResultReceiver(Handler handler) {
        super(handler);
        this.handler=handler;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if(resultCode==RECEIVE_FEED){
            handler.sendMessage(Message.obtain(handler,RECEIVE_FEED));
        }
    }
}
