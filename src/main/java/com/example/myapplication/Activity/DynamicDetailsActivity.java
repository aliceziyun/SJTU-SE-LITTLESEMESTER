package com.example.myapplication.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CommentListAdapter;
import com.example.myapplication.adapter.ViewPagerAdapter;
import com.example.myapplication.db.entity.Comment;
import com.example.myapplication.db.entity.Dynamic;
import com.example.myapplication.db.entity.User;
import com.example.myapplication.util.DateHelper;
import com.example.myapplication.view.NoScrollListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

public class DynamicDetailsActivity extends BaseActivity implements View.OnClickListener,View.OnLayoutChangeListener{
    private static final int QUERY_DYNAMIC_SUCCESS = 0x11;
    private static final int Query_Dynamic_Failure = 0x12;
    private static final int GET_COMMENT_SUCCESS = 0x13;
    private static final int SEND_COMMENT_SUCCESS = 0x14;
    private static final int Send_Comment_Failure = 0x15;

    private LinearLayout allLayout;
    private ImageView backImg;
    private ImageView headImg;
    private TextView nameText;
    private TextView timeText;
    private ViewPager mViewPager;
    private TextView contentText;
    private TextView themeText;

    private NoScrollListView commentListView;

    private EditText commentEdt;
    private ImageButton sendBtn;

    private TextView commentNumber;
    private User user;
    private String currentUserId;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private DisplayImageOptions circleOptions;

    private String dynamicId = null;
    private Dynamic dynamic = null;

    private ArrayList<Comment> comments = new ArrayList<>();
    private CommentListAdapter adapter;

    private InputMethodManager inputMethodManager;

    private Comment comment  = null;
    private String commentContent = null;
    private User toUser = null;

    private DateHelper dateHelper = new DateHelper();
    private DatabaseReference mDatabase;

    private int screenHeight; //屏幕高度
    private int softKeyHeight; //软键盘弹起时占高度

    private boolean isToUser = false; //是否是对用户进行的评论，true是，false不是

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case QUERY_DYNAMIC_SUCCESS:
                    setDateToView();
                    closeProgressDialog();
                    break;

                case GET_COMMENT_SUCCESS:
                    setAdapter();
                    commentEdt.setText("");
                    commentNumber.setText(comments.size()+"");
                    break;

                case SEND_COMMENT_SUCCESS: //发送评论成功
                    commentEdt.setText("");
                    Toast.makeText(DynamicDetailsActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                    break;

                case Send_Comment_Failure: //发送评论失败
                    commentEdt.setText("");
                    Toast.makeText(DynamicDetailsActivity.this,"评论失败",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_details);

        mDatabase = FirebaseDatabase.getInstance().getReference();  //谷歌数据库

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imageLoader = ImageLoader.getInstance();
        dynamicId = getIntent().getStringExtra("dynamic_id");

        initComponent();
        initCommentListener();
        setListener();
        getCurrentUser();

        initOptions();

        if (dynamicId != null) {
            showProgressDialog(DynamicDetailsActivity.this,"加载中");
            queryDynamicById();
        }

        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        softKeyHeight = screenHeight/3;
    }


    private void initOptions(){

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_no_picture)
                .showImageOnFail(R.drawable.default_no_picture)
                .showImageForEmptyUri(R.drawable.default_no_picture)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        allLayout = (LinearLayout) findViewById(R.id.dynamic_details_layout);
        backImg = (ImageView) findViewById(R.id.dynamic_details_back_img);
        headImg = (ImageView) findViewById(R.id.dynamic_details_head_img);
        nameText = (TextView) findViewById(R.id.dynamic_details_name_text);
        timeText = (TextView) findViewById(R.id.dynamic_details_time_text);
        mViewPager = (ViewPager) findViewById(R.id.dynamic_details_mViewpager);
        contentText = (TextView) findViewById(R.id.dynamic_details_content);
        commentNumber = (TextView) findViewById(R.id.text_comment_number);
        commentListView = (NoScrollListView) findViewById(R.id.dynamic_comment_listview);
        commentEdt = (EditText) findViewById(R.id.dynamic_comment_edt);
        sendBtn = (ImageButton) findViewById(R.id.dynamic_comment_send_btn);


        backImg.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        allLayout.addOnLayoutChangeListener(this);

    }

    /**
     * 设置适配器
     */
    private void setAdapter(){
        if (comments.size() != 0) {
            adapter = new CommentListAdapter(getApplicationContext(),comments);
            commentListView.setAdapter(adapter);
        }
    }

    /**
     * 设置监听
     */
    private void setListener(){
        commentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isToUser = true;
                commentEdt.setHint("回复"+comments.get(position).getFromUser().getUserName()+"：");
                inputMethodManager.showSoftInput(commentEdt,InputMethodManager.SHOW_FORCED);
                toUser = comments.get(position).getFromUser();
            }
        });
    }

    private void getCurrentUser(){
        SharedPreferences pref = getSharedPreferences("LogInfo",MODE_PRIVATE);
        currentUserId = pref.getString("user_id","");
        mDatabase.child("user").child(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(DynamicDetailsActivity.this, "读取失败！", Toast.LENGTH_SHORT).show();
                }
                else {
                    user = task.getResult().getValue(User.class);
                }
            }
        });
    }

    /**
     * 根据id,查询动态
     */
    private void queryDynamicById(){
        mDatabase.child("dynamic").child(dynamicId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(DynamicDetailsActivity.this, "读取失败！", Toast.LENGTH_SHORT).show();
                }
                else {
                    dynamic = task.getResult().getValue(Dynamic.class);
                    Log.i("DynamicDetails",String.valueOf(dynamic));
                    handler.sendEmptyMessage(QUERY_DYNAMIC_SUCCESS);
                }
            }
        });
    }

    /**
     * 填充数据显示视图
     */
    private void setDateToView(){
        Log.i("DynamicDetails","setting");
        if (dynamic != null) {
            String fromUserId = dynamic.getFromUserId();
            Log.i("DynamicDetails",dynamicId+dynamic.getContent());
            mDatabase.child("user").child(fromUserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(DynamicDetailsActivity.this, "读取失败！", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        User fromUser = task.getResult().getValue(User.class);
                        imageLoader.displayImage(fromUser.getImg(), headImg, circleOptions);
                        nameText.setText(fromUser.getUserName());
                        timeText.setText(dynamic.getDate());
                        if (dynamic.getImage()== null || dynamic.getImage().size() <= 0) {
                            mViewPager.setVisibility(View.GONE);
                        } else {
                            mViewPager.setVisibility(View.VISIBLE);
                            mViewPager.setAdapter(new ViewPagerAdapter(getApplicationContext(), dynamic.getImage()));
                        }
                        contentText.setText(dynamic.getContent());
                    }
                }
            });
            commentNumber.setText(dynamic.getCommentCount()+"");

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dynamic_details_back_img:
                this.finish();
                break;
            case R.id.dynamic_details_head_img: //点击头像
                break;

            case R.id.dynamic_comment_send_btn: //发送评论
                commentContent = commentEdt.getText().toString();
                if (TextUtils.isEmpty(commentContent)) {
                    Toast.makeText(DynamicDetailsActivity.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                } else {
                    sendComment();
                }
                break;

        }
    }

    /**
     * 查询动态的全部评论
     */
    private void initCommentListener(){
        ChildEventListener commentListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot,String previousChildName) {
                if(dataSnapshot.exists()) {
                    Log.i("Comment", String.valueOf(dataSnapshot.getValue()));
                    comments.add(dataSnapshot.getValue(Comment.class));
                    Message msg = new Message();
                    msg.what = GET_COMMENT_SUCCESS;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.i("SocialFragment", String.valueOf(databaseError.toException()));
            }
        };
        Query commentQuery = mDatabase.child("comment").child(dynamicId).orderByChild("date");
        commentQuery.addChildEventListener(commentListener);
    }


    /**
     * 发送评论
     */
    public void sendComment(){
        comment = null;
        comment = new Comment();
        comment.setFromUser(user);
        comment.setDate(dateHelper.getNowTime());
        comment.setContent(commentContent);
        comment.setDynamic(dynamicId);
        comment.setToUser(toUser);

        mDatabase.child("comment").child(dynamicId).push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Message msg = new Message();
                        msg.what = SEND_COMMENT_SUCCESS;
                        handler.sendMessage(msg);

                        //修改动态评论数
                        increaseCommentCount();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Message msg = new Message();
                        msg.what = Send_Comment_Failure;
                        handler.sendMessage(msg);
                    }
                });
    }

    /**
     * 修改动态评论数，+1
     */
    private void increaseCommentCount() {
        if (dynamic != null) {
            int newCommentNum = dynamic.getCommentCount() + 1;
            Log.i("Comment",String.valueOf(dynamic));
            dynamic.setCommentCount(newCommentNum);
            mDatabase.child("dynamic").child(dynamicId).child("commentCount").setValue(newCommentNum);
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (bottom!=0 && oldBottom != 0 && (oldBottom-bottom>softKeyHeight)) { //弹起
            Toast.makeText(DynamicDetailsActivity.this,"弹起",Toast.LENGTH_SHORT).show();
        } else if (bottom!=0 && oldBottom != 0 && (bottom-oldBottom>softKeyHeight)){//关闭
            Toast.makeText(DynamicDetailsActivity.this,"关闭",Toast.LENGTH_SHORT).show();
            if (isToUser) { //是对用户
                toUser = null;
                commentEdt.setHint("评论......");
                isToUser = false;
            }
        }
    }
}
