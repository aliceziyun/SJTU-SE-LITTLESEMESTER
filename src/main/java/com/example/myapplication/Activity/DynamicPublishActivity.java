package com.example.myapplication.Activity;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.autonavi.base.amap.mapcore.FileUtil;
import com.example.myapplication.R;
import com.example.myapplication.adapter.PublishPicAdapter;
import com.example.myapplication.db.entity.Dynamic;
import com.example.myapplication.util.DateHelper;
import com.example.myapplication.util.FileUtils;
import com.example.myapplication.util.InitDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicPublishActivity extends BaseActivity implements View.OnClickListener{

    private final static int REQUEST_CODE_CAMERA=0X01; //拍照的requestcode

    private static final int GET_PHOTO_REQUEST = 0X02;

    private static final int SCAN_OPEN_PHONE = 2;// 相册

    //handler请求码
    private static final int PUSH_DYNAMIC_SUCCESS = 0x10;
    private static final int REQUEST_CODE_IMAGE = 0x20;
    private static final int Save_Dynamic_To_MYTimeline = 0x11;
    private static final int Push_Dynamic_Failure = 0x12;
    private static final int UPLOAD_PICTURE_SUCCESS = 0x13;

    private EditText contentEdt; //动态内容
    private PopupWindow popupWindow;
    private TextView photoText,cameraText,cancelText;
    private GridView gridView;
    private List<String> selectedPicture = new ArrayList<>(); //选中图片
    private PublishPicAdapter adapter;
    private ImageButton publish_btn;

    private String userId; //当前用户Id
    private String userName;
    private String key = "";     //动态key，每次这个activity会重启，因此可以用全局变量
    private Dynamic dynamic = null; //动态
    private String content = null; //内容
    private String theme = null;//主题
    private List<String> pictureList = new ArrayList<>(); //图片url

    private boolean isPushFinish = true;//发布动态完成标志
    private DateHelper dateHelper = new DateHelper();

    private DatabaseReference mDatabase;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPLOAD_PICTURE_SUCCESS: //上传图片文件成功
                    Bundle bundle = msg.getData();
                    //获取图片url集合
                    pictureList = bundle.getStringArrayList("urls");
                    //发布动态
                    publishDynamic();
                    break;
                case PUSH_DYNAMIC_SUCCESS: //发布动态成功
                    Toast.makeText(DynamicPublishActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
                    isPushFinish = true;
                    closeProgressDialog();
                    finish();
                    break;
                case Push_Dynamic_Failure://发布动态失败
                    isPushFinish = true;
                    Toast.makeText(DynamicPublishActivity.this, "发布动态失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_publish);

        getCurrentUser();

        initComponent();
        initPopupWindow();
        initGridView();
    }


    private void getCurrentUser(){
        SharedPreferences pref = getSharedPreferences("LogInfo",MODE_PRIVATE);     //获取当前用户id
        userId =  pref.getString("user_id","");
        userName = pref.getString("user_name","");
    }

    private void initComponent(){
        InitDatabase initDatabase = new InitDatabase();
        mDatabase = initDatabase.getDatabase().getReference();

        publish_btn = (ImageButton) findViewById(R.id.publish_dynamic);
        publish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInput() && isPushFinish) { //有内容
                    isPushFinish = false;
                    showProgressDialog(DynamicPublishActivity.this,"等待动态上传");
                    if (selectedPicture.size() > 0) { //有图片，先上传图片
                        uploadPicture();
                    } else {//无图片
                        //发布动态
                        publishDynamic();
                    }
                } else { //无内容
                    Toast.makeText(getApplicationContext(), "动态不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        contentEdt = (EditText) findViewById(R.id.publish_content);
        gridView = (GridView) findViewById(R.id.publish_gridview);
    }

    private void initGridView(){
        adapter = new PublishPicAdapter(getApplicationContext(),selectedPicture);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_dynamic_publish, null), Gravity.BOTTOM, 0, 0);
                }
                }
        });
    }

    /**
     * 检查输入
     * @return
     */
    private boolean checkInput(){

        if (contentEdt.getText().length()>0 || selectedPicture.size()>0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {     //发图
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCAN_OPEN_PHONE:
                if (resultCode == RESULT_OK){
                    ClipData selectImages = data.getClipData();
                    if (selectImages != null) {
                        for (int i = 0; i < selectImages.getItemCount(); i++) {
                            Uri imageUri = selectImages.getItemAt(i).getUri();
                            String[] FilePathColumn={MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(imageUri,
                                    FilePathColumn, null, null, null);
                            cursor.moveToFirst();
                            //从数据视图中获取已选择图片的路径
                            int columnIndex = cursor.getColumnIndex(FilePathColumn[0]);
                            String picpath = cursor.getString(columnIndex);
                            selectedPicture.add(picpath);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK){
                    Bitmap bitmap=null;
                    Uri uri=data.getData();
                    if (uri!=null) {
                        bitmap= BitmapFactory.decodeFile(uri.getPath());
                        Log.i("Camera","urinotnull");
                    }
                    if (bitmap==null){
                        Bundle bundle=data.getExtras();
                        if (bundle!=null) {
                            bitmap = (Bitmap) bundle.get("data");//缩略图
                        } else {
                            Toast.makeText(DynamicPublishActivity.this,"拍照失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    //获取拍照图片路径
                    String picPath = FileUtils.saveBitmapToFile(bitmap,"camera");
                    selectedPicture.add(picPath);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 发布动态
     */
    private void publishDynamic(){
        if(key.length() == 0){
            key = mDatabase.child("dynamic").push().getKey();
        }

        content = contentEdt.getText().toString();
        String date = dateHelper.getNowTime();

        dynamic = new Dynamic();
        dynamic.setId(key);
        dynamic.setFromUserId(userId);
        dynamic.setDate(date);
        dynamic.setContent(content);
        dynamic.setTheme(theme);
        dynamic.setImage(pictureList);
        dynamic.setCommentCount(0);
        dynamic.setLikesCount(0);

        mDatabase.child("dynamic").child(key).setValue(dynamic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //保存动态到个人时间线中
                saveDynamicToMyTimeline(userId,key,date);
                //推送动态给粉丝
                pushDynamic(key,date);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                handler.sendEmptyMessage(Push_Dynamic_Failure);
                Toast.makeText(DynamicPublishActivity.this, "发布动态失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadPicture() {
        key = mDatabase.child("dynamic").push().getKey();
        final ArrayList<String> filePaths = new ArrayList<String>();
        UploadTask uploadTask = null;

        if(selectedPicture.size() > 0){
            for (int i = 0; i < selectedPicture.size(); i++) {
                Uri file = Uri.fromFile(new File(selectedPicture.get(i)));
                String lastPathSegment = file.getLastPathSegment();
                filePaths.add(lastPathSegment);
                StorageReference picRef = storage.getReference().child("dynamic").child(key).child(lastPathSegment);
                uploadTask = picRef.putFile(file);
            }
        }

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                int errorCode = ((StorageException) exception).getErrorCode();
                String errorMessage = exception.getMessage();
                Log.i("upLoadPic",errorMessage+String.valueOf(errorCode));
                Toast.makeText(DynamicPublishActivity.this, "上传图片失败！", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getUploadImageUrl(filePaths);
            }
        });
    }

    private void getUploadImageUrl(ArrayList<String> filePaths){
        int len = filePaths.size();
        final int[] count = {0};
        ArrayList<String> picUrl = new ArrayList<>();
        for(String each : filePaths){
            StorageReference uploadRef = storage.getReference().child("dynamic").child(key).child(each);
            uploadRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    picUrl.add(uri.toString());
                    count[0]++;
                    Log.i("upLoadPic", String.valueOf(count[0]));
                    if(count[0] == len){
                        Message msg = new Message();
                        msg.what = UPLOAD_PICTURE_SUCCESS;
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("urls",picUrl);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                    Log.i("upLoadPic", String.valueOf(uri));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(DynamicPublishActivity.this, "获取网址失败！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveDynamicToMyTimeline(String userId,String key,String date){
        mDatabase.child("user").child(userId).
                child("all_dynamic").child(key).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        handler.sendEmptyMessage(PUSH_DYNAMIC_SUCCESS);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DynamicPublishActivity.this, "发布动态失败,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });;
        mDatabase.child("user").child(userId).
                child("dynamic_published").child(key).setValue(date);
    }

    /**
     * 初始化popup
     */
    private void initPopupWindow() {
        View view1 = getLayoutInflater().inflate(R.layout.popup_headimg_layout,null);
        popupWindow = new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        photoText = (TextView) view1.findViewById(R.id.popup_photo);
        cameraText = (TextView) view1.findViewById(R.id.popup_camera);
        cancelText = (TextView) view1.findViewById(R.id.popup_cancel);

        photoText.setOnClickListener(this);
        cameraText.setOnClickListener(this);
        cancelText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popup_camera:
                Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(cameraIntent.resolveActivity(getPackageManager())!=null){
                    //判断系统是否有能处理cameraIntent的activity
                    startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
                    popupWindow.dismiss();
                }
                popupWindow.dismiss();
                break;
            case R.id.popup_photo:
                //获取权限
                ActivityCompat.requestPermissions(DynamicPublishActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                },0);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent, SCAN_OPEN_PHONE);
                popupWindow.dismiss();
                break;
            case R.id.popup_cancel:
                popupWindow.dismiss();
                break;
        }
    }

    //推送动态给粉丝
    private void pushDynamic(String key,String date){
        //获取粉丝对象集合
        mDatabase.child("friend").child(userId).child("fans").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(DynamicPublishActivity.this, "推送失败", Toast.LENGTH_SHORT).show();
                }
                else {
                    Map<String,Boolean> friendMap = (Map<String, Boolean>) task.getResult().getValue();
                    if(friendMap != null){
                        for(Map.Entry<String,Boolean> entry : friendMap.entrySet()) {
                            if(entry.getValue()){
                                String fansId = entry.getKey();
                                pushToFans(fansId,key,date);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 推送给粉丝
     */
    public void pushToFans(String fansId,String key,String date) {
        Map<String,Object> dynamic = new HashMap<>();
        dynamic.put(key,date);
        //查询粉丝的时间线
        mDatabase.child("user").child(fansId).child("all_dynamic").updateChildren(dynamic);
    }
}
