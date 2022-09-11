package com.example.myapplication.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Activity.BaseActivity;
import com.example.myapplication.Activity.CourseListActivity;
import com.example.myapplication.Activity.DynamicListActivity;
import com.example.myapplication.Activity.DynamicStarListActivity;
import com.example.myapplication.Activity.FriendActivity;
import com.example.myapplication.Activity.LoginActivity;
import com.example.myapplication.Activity.MessageActivity;
import com.example.myapplication.Activity.ModifyActivity;
import com.example.myapplication.Activity.SportDataActivity;
import com.example.myapplication.Activity.SportPlanActivity;
import com.example.myapplication.R;
import com.example.myapplication.db.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

public class UserPersonal extends Fragment {
    private final int LOAD_SUCCESS = 0x01;

    private Button btn_logout;
    private RelativeLayout btn_course, btn_spdata, btn_spplan, btn_mystar, btn_mypost,userinfo;
    private ImageView btn_message;
    private TextView user_name, user_info,personal_title;
    private ImageView profile;
    private DisplayImageOptions circleOptions;
    private LinearLayout layout_follow, layout_dynamic, layout_fans;
    private TextView text_follow_number, text_fans_number, text_dynamic_number;
    private int followNumber, fansNumber, dynamicNumber;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private BaseActivity activity = new BaseActivity();

    private String userId;
    public User user;

    private DatabaseReference mDatabase;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    activity.closeProgressDialog();
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_user_personal,container,false);

        SharedPreferences pref = getActivity().getSharedPreferences("LogInfo", Context.MODE_PRIVATE);     //获取当前用户id
        userId = pref.getString("user_id","");

        this.circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.default_avatar)
                .displayer(new CircleBitmapDisplayer())
                .build();

        init(view,userId);

        return view;
    }

    private void init(View view,String userId){
        activity.showProgressDialog(getActivity(),"加载中");
        //先获取用户
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getContext(), "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                }
                else {
                    user = task.getResult().getValue(User.class);
                    btn_course = view.findViewById(R.id.per_my_course);
                    btn_spdata = view.findViewById(R.id.per_sport_data);
                    btn_spplan = view.findViewById(R.id.per_sport_plan);
                    btn_mystar = view.findViewById(R.id.per_star_post);
                    btn_mypost = view.findViewById(R.id.per_my_post);
                    btn_logout = view.findViewById(R.id.btn_logout);
                    user_name = view.findViewById(R.id.username);
                    user_info = view.findViewById(R.id.information);
                    profile = view.findViewById(R.id.profile);
                    btn_message = view.findViewById(R.id.personal_message);
                    userinfo = view.findViewById(R.id.userinfo);
                    personal_title = view.findViewById(R.id.personal_title);
                    layout_fans = view.findViewById(R.id.user_personal_fans);
                    layout_follow =  view.findViewById(R.id.user_personal_follow);
                    text_follow_number = view.findViewById(R.id.user_personal_follow_number);
                    text_fans_number = view.findViewById(R.id.user_personal_fans_number);
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    fansNumber = 0; followNumber = 0; dynamicNumber = 0;
                    setFansNumber();
                    setFollowNumber();
                    setDynamicNumber();

                    user_name.setText(user.getUserName());
                    user_info.setText("总运动时长："+user.getTotalTime());
                    Typeface personal_title_typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-Bold.ttf");
                    personal_title.setTypeface(personal_title_typeface);
                    btn_course.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), CourseListActivity.class);
                            startActivity(intent);
                        }
                    });
                    btn_spdata.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), SportDataActivity.class);
                            startActivity(intent);
                        }
                    });
                    btn_spplan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), SportPlanActivity.class);
                            startActivity(intent);
                        }
                    });
                    btn_mystar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), DynamicStarListActivity.class);
                            startActivity(intent);
                        }
                    });
                    btn_mypost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), DynamicListActivity.class);
                            startActivity(intent);
                        }
                    });
                    btn_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), MessageActivity.class);
                            startActivity(intent);
                        }
                    });
                    profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                    userinfo.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            Intent intent = new Intent(getActivity(), ModifyActivity.class);
                            startActivity(intent);
                        }
                    });
                    layout_fans.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), FriendActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putString("userId",user.getId());
                            bundle.putString("userName", user.getUserName());
                            bundle.putInt("sign", 1); // 查询粉丝
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    layout_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), FriendActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putString("userId",user.getId());
                            bundle.putString("userName", user.getUserName());
                            bundle.putInt("sign", 0); // 查询关注
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    btn_logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences pref = getActivity().getSharedPreferences("LogInfo",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("save_pwd",true);
                            editor.commit();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });

                    handler.sendEmptyMessage(LOAD_SUCCESS);
                    initListener();
                }
            }
        });
    }

    private void initListener(){
        ValueEventListener imgListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String url = dataSnapshot.getValue().toString();
                    Log.i("UserPersonal",url);
                    imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
                    imageLoader.displayImage(url, profile, circleOptions);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(getContext(), "获取头像失败", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.child("user").child(userId).child("img").addValueEventListener(imgListener);
    }

    private void setFansNumber(){
        Query fansNumQuery = mDatabase.child("friend").child(String.valueOf(user.getId())).child("fans");
        fansNumQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                fansNumber++;
                text_fans_number.setText(String.valueOf(fansNumber));
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                fansNumber--;
                text_fans_number.setText(String.valueOf(fansNumber));
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setFollowNumber(){
        Query followNumQuery = mDatabase.child("friend").child(String.valueOf(user.getId())).child("follow");
        followNumQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                followNumber++;
                text_follow_number.setText(String.valueOf(followNumber));
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                followNumber--;
                text_follow_number.setText(String.valueOf(followNumber));
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setDynamicNumber(){
//        text_dynamic_number.setText(dynamicNumber);
    }
}
