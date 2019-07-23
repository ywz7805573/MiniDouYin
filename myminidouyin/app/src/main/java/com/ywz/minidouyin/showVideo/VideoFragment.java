package com.ywz.minidouyin.showVideo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ywz.minidouyin.R;
import com.ywz.minidouyin.bean.Feed;
import com.ywz.minidouyin.db.FeedDBHelper;
import com.ywz.minidouyin.newtork.FetchFeedThreads;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {
    private ViewPager viewPager;
    protected List<Feed> feedList=new ArrayList<>();
    private FragmentStatePagerAdapter adapter;
    public static final String TAG = "VideoFragment";


    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what== FetchFeedThreads.FETCH_BACK){
                feedList = FetchFeedThreads.getInstance().getList();
                adapter.notifyDataSetChanged();
                Log.d(TAG, "handleMessage: "+feedList.size());
            }
        }
    }
    private Handler handler = new MyHandler();

    public VideoFragment() {}

    public static VideoFragment newInstance() {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        //FetchFeedThreads.getInstance().fetch_request(handler);
        viewPager = view.findViewById(R.id.videoViewPager);
        adapter = new FragmentStatePagerAdapter(getFragmentManager()){
            @Override
            public Fragment getItem(int position) {
                VideoPlayFragment fragment = VideoPlayFragment.newInstance();
                fragment.setResource(Uri.parse(feedList.get(position).getVideoUrl()));
                fragment.setImageResource(Uri.parse(feedList.get(position).getImgUrl()));
                return fragment;
            }

            @Override
            public int getCount() {
                return feedList.size();
            }
        };
        viewPager.setAdapter(adapter);

        feedList = FeedDBHelper.refreshList(getActivity());

        adapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
