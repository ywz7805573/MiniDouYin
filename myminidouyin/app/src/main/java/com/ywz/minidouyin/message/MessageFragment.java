package com.ywz.minidouyin.message;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ywz.minidouyin.MainActivity;
import com.ywz.minidouyin.R;
import com.ywz.minidouyin.bean.Feed;
import com.ywz.minidouyin.db.FeedContract;
import com.ywz.minidouyin.db.FeedDBHelper;
import com.ywz.minidouyin.newtork.FetchFeedService;
import com.ywz.minidouyin.newtork.FetchFeedThreads;
import com.ywz.minidouyin.showVideo.VideoFragment;
import com.ywz.minidouyin.showVideo.VideoPlayActivity;
import com.ywz.minidouyin.takeVideo.MyHomeActivity;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment {
    private static final String TAG = "Fragment";
    public ListView messageList;
    private ListViewAdapter adapter;
    private List<Feed>feedList = new ArrayList<>();
    private FeedDBHelper dbHelper;
    private SQLiteDatabase db;
    private FloatingActionButton button;



    public MessageFragment() {
    }

    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        button = getView().findViewById(R.id.floatingActionRefresh);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //FetchFeedThreads.getInstance().fetch_request(handler,MainActivity.this);
//                FetchFeedService.startFetchFeed(MainActivity.this,resultReceiver,null);
//            }
//        });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_message, container, false);
        adapter=new ListViewAdapter(getActivity(),feedList);
        messageList = v.findViewById(R.id.messageList);
        messageList.setAdapter(adapter);
        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), MyHomeActivity.class);
                intent.putExtra(MyHomeActivity.LOGINNAME,feedList.get(i).getName());
                intent.putExtra(MyHomeActivity.LOGINID,feedList.get(i).getStudentID());
                startActivity(intent);
            }
        });
        Log.d(TAG, "onCreateView: ");
        refreshList();
        return v;
    }

    public void refreshList(){
        Log.d(TAG, "refreshList: ");
        Cursor cursor = db.rawQuery("select * from "+ FeedContract.tableName
                +" order by "+FeedContract.updatedAt+" DESC",null);
        feedList= new ArrayList<>();
        while(cursor.moveToNext()){
            Feed f = dbHelper.toFeed(cursor);
            feedList.add(f);
        }
        adapter.setArgs(feedList);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dbHelper=new FeedDBHelper(getActivity());
        db = dbHelper.getReadableDatabase();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        db.close();
        dbHelper.close();
        db=null;
        dbHelper=null;
    }

    public class ListViewAdapter extends BaseAdapter {
        private List<Feed> args;
        private Context context;

        public void setArgs(List<Feed> list){
            args=list;
        }

        @Override
        public void notifyDataSetChanged() {
            Log.d(TAG, "notifyDataSetChanged: "+args.size());
            super.notifyDataSetChanged();
        }

        public ListViewAdapter(Context c, List<Feed> args){
            context=c;
            this.args = args;
        }

        @Override
        public int getCount() {
            return args.size();
        }

        @Override
        public Object getItem(int i) {
            return args.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void renewView(ViewGroup view,Feed msg){
            //ViewGroup iconLayout = (ViewGroup)view.getChildAt(0);
            //CircleImageView img = (CircleImageView)iconLayout.getChildAt(0);
            TextView title = (TextView)view.getChildAt(1);
            title.setText(msg.getName());
            TextView des = (TextView)view.getChildAt(2);
            des.setText(msg.getStudentID());
            TextView time = (TextView)view.getChildAt(3);
            time.setText(msg.getUpdatedAt());
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = null;
            Feed msg = args.get(i);
            if(view==null) {
                ViewGroup inf = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.im_list_item,null);
                renewView(inf,msg);
                v=inf;
            }else{
                renewView((ViewGroup)view,msg);
                v = view;
            }
            return v;
        }
    }

}
