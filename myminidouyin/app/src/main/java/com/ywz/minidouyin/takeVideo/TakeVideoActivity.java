package com.ywz.minidouyin.takeVideo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ywz.minidouyin.R;
import com.ywz.minidouyin.bean.Feed;
import com.ywz.minidouyin.bean.PostVideoResponse;
import com.ywz.minidouyin.db.FeedDBHelper;
import com.ywz.minidouyin.newtork.IMiniDouyinService;
import com.ywz.minidouyin.showVideo.VideoPlayActivity2;
import com.ywz.minidouyin.utils.ResourceUtils;
import com.ywz.minidouyin.utils.UriUtils;

import java.io.File;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.ywz.minidouyin.GateWay.REQUEST_EXTERNAL_CAMERA;

public class TakeVideoActivity extends AppCompatActivity {

    public static final String LOGINNAME = "LOGINNAME";
    public static final String LOGINID = "LOGINID";

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final String TAG = "TakeVideoActivity";
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    public Button mBtn2;
    private String myName;
    private String myId;



    private static final String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_video);

        findViewById(R.id.btn_system).setOnClickListener(v -> {
            startActivity(new Intent(TakeVideoActivity.this, TakePictureActivity.class));
        });

        findViewById(R.id.btn_camera).setOnClickListener(v -> {
            startActivity(new Intent(TakeVideoActivity.this, VideoTakeActivity.class));
        });

        findViewById(R.id.btn_custom).setOnClickListener(v -> {
            //todo 在这里申请相机、麦克风、存储的权限
            boolean flg =true;
            for(int i=0;i<permissions.length;i++){
                if(checkSelfPermission(permissions[i])!= PackageManager.PERMISSION_GRANTED){
                    flg=false;
                    break;
                }
            }
            if (!flg) {
                requestPermissions(permissions,REQUEST_EXTERNAL_CAMERA);
            }else {
                startActivity(new Intent(TakeVideoActivity.this, CustomcameraActivity.class));
            }
        });

        final Intent intent = getIntent();
        myName = intent.getStringExtra(LOGINNAME);
        myId = intent.getStringExtra(LOGINID);
        initBtns();
    }

    private void initBtns() {
        mBtn = findViewById(R.id.btn_post);
        mBtn2 = findViewById(R.id.btn_preview);
        mBtn2.setVisibility(View.INVISIBLE);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String s = mBtn.getText().toString();
                if (getString(R.string.select_an_image).equals(s)) {
                    chooseImage();
                } else if (getString(R.string.select_a_video).equals(s)) {
                    chooseVideo();
                    mBtn2.setVisibility(View.VISIBLE);
                } else if (getString(R.string.post_it).equals(s)) {
                    if (mSelectedVideo != null && mSelectedImage != null) {
                        postVideo();
                        mBtn2.setVisibility(View.INVISIBLE);
                    } else {
                        throw new IllegalArgumentException("error data uri, mSelectedVideo = " + mSelectedVideo + ", mSelectedImage = " + mSelectedImage);
                    }
                } else if ((getString(R.string.success_try_refresh).equals(s))) {
                    mBtn.setText(R.string.select_an_image);
                }
            }
        });
        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TakeVideoActivity.this, VideoPlayActivity2.class);
                intent.putExtra(VideoPlayActivity2.PIC,mSelectedImage);
                intent.putExtra(VideoPlayActivity2.VIDEO,mSelectedVideo);
                startActivity(intent);
            }
        });
    }
    public void chooseImage() {
        // TODO-C2 (4) Start Activity to select an image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    public void chooseVideo() {
        // TODO-C2 (5) Start Activity to select a video
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                mBtn.setText(R.string.select_a_video);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                mBtn.setText(R.string.post_it);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(TakeVideoActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    public void post2db(){
        FeedDBHelper helper = new FeedDBHelper(TakeVideoActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Feed f = new Feed();
        f.setVideoUrl(UriUtils.formatUri(this,mSelectedVideo));
        f.setStudentID(myId);
        f.setName(myName);
        f.setImgUrl(UriUtils.formatUri(this,mSelectedImage));
        Log.d(TAG, UriUtils.formatUri(this,mSelectedImage));
        String date = (new Date()).toGMTString();
        f.setUpdatedAt(date);
        f.setCreateAt(date);
        helper.insertOneFeed(f,db);
    }

    private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);

        // TODO-C2 (6) Send Request to post a video with its cover image
        // if success, make a text Toast and show

        MultipartBody.Part file1 = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part file2 = getMultipartFromUri("video", mSelectedVideo);

        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 1);
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://test.androidcamp.bytedance.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Call<PostVideoResponse> call = retrofit.create(IMiniDouyinService.class).postVideoResponse(myId, myName, file1, file2);
            call.enqueue(new Callback<PostVideoResponse>() {
                //请求成功时回调
                @Override
                public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                    //请求处理,输出结果
                    if(response.body().isSuccess()){
                        post2db();
                        Toast toast = Toast.makeText(getApplicationContext(), "上传成功！", Toast.LENGTH_SHORT);
                        toast.show();
                        mBtn.setText(R.string.success_try_refresh);
                        mBtn.setEnabled(true);
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(), "上传失败！", Toast.LENGTH_SHORT);
                        toast.show();
                        mBtn.setText(R.string.select_an_image);
                        mBtn.setEnabled(true);
                    }
                }
                //请求失败时回调
                @Override
                public void onFailure(Call<PostVideoResponse> call, Throwable throwable) {
                    System.out.println("连接失败");
                    Toast toast = Toast.makeText(getApplicationContext(), "上传失败！", Toast.LENGTH_SHORT);
                    toast.show();
                    mBtn.setText(R.string.select_an_image);
                    mBtn.setEnabled(true);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_EXTERNAL_CAMERA){
            for(int i=0;i<permissions.length;i++){
                if(checkSelfPermission(permissions[i])!=PackageManager.PERMISSION_GRANTED){
                    return;
                }
            }
            startActivity(new Intent(TakeVideoActivity.this, CustomcameraActivity.class));
        }
    }
}
