package com.ywz.minidouyin.takeVideo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ywz.minidouyin.R;
import com.ywz.minidouyin.bean.Feed;
import com.ywz.minidouyin.db.FeedContract;
import com.ywz.minidouyin.db.FeedDBHelper;
import com.ywz.minidouyin.showVideo.VideoPlayActivity;

import java.util.ArrayList;
import java.util.List;

public class MyHomeActivity extends AppCompatActivity {
    public static final String LOGINNAME = "LOGINNAME";
    public static final String LOGINID = "LOGINID";

    TextView myname;
    TextView myid;
    String name;
    String id;
    RecyclerView mRv;
    private List<Feed> feedList = new ArrayList<>();
    View mFloatingActionBar;

    FeedDBHelper helper = new FeedDBHelper(this);
    SQLiteDatabase db;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_home);
        final Intent intent = getIntent();
        name = intent.getStringExtra(LOGINNAME);
        id = intent.getStringExtra(LOGINID);
        myname=findViewById(R.id.myname);
        myid=findViewById(R.id.myid);
        myid.setText(id);
        myname.setText(name);

        db = helper.getReadableDatabase();

        mRv = findViewById(R.id.myrecycleview);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerView.Adapter() {
            @NonNull @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
                ImageView imageView = new ImageView(viewGroup.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                return new MyViewHolder(imageView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageView iv = (ImageView) viewHolder.itemView;
                iv.setPadding(10,30,10,10);
                String url = feedList.get(i).getImgUrl();
                Glide.with(iv.getContext()).load(url).into(iv);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent showVideoIntent = new Intent(MyHomeActivity.this, VideoPlayActivity.class);
                        showVideoIntent.setData(Uri.parse(feedList.get(i).getVideoUrl()));
                        startActivity(showVideoIntent);
                    }
                });
            }

            @Override public int getItemCount() {
                return feedList.size();
            }
        };
        mRv.setAdapter(adapter);
        getFeeds();
        adapter.notifyDataSetChanged();

        //悬浮按钮
        mFloatingActionBar = findViewById(R.id.floatingActionButton);
        mFloatingActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyHomeActivity.this, TakeVideoActivity.class);
                intent.putExtra(TakeVideoActivity.LOGINID,myid.getText().toString());
                intent.putExtra(TakeVideoActivity.LOGINNAME,myname.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFeeds();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    public void getFeeds(){
        Cursor cursor = db.query(FeedContract.tableName,
                null, FeedContract.student_id+"=? and "
            + FeedContract.user_name+"=?",new String[]{id,name},null,null,null);
        while(cursor.moveToNext()){
            Feed f = FeedDBHelper.toFeed(cursor);
//            if(f.getName().compareTo(name)==0&&f.getStudentID().compareTo(id)==0)
            feedList.add(f);
        }
        Log.d("Myhome", "getFeeds: "+feedList.size());
        cursor.close();
    }
}
