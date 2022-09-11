package com.example.myapplication.adapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activity.AddFriendActivity;
import com.example.myapplication.Activity.FriendActivity;
import com.example.myapplication.R;
import com.example.myapplication.db.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
//import com.yang.runbang.listener.OnRecyclerViewClickListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 洋 on 2016/6/6.
 */
public class AddFriendRecyclerAdapter extends RecyclerView.Adapter<AddFriendRecyclerAdapter.ViewHolder> {

//    private OnRecyclerViewClickListener listener=null;

    private static final int Friend_Activity = 0;
    private static final int Add_Friend_Activity = 1;
    private int lastActivity;

    private List<User> data= new ArrayList<>();
    private List<User> followData = new ArrayList<>();
    private User user; // 当前用户
    private Context context;

    private DisplayImageOptions options;
    private DisplayImageOptions circleOptions;

    private DatabaseReference mDatabase;

    private Activity getActivity() {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    public void setData(List<User> data) {
        this.data = data;
        Log.i(TAG, "setData: " + String.valueOf(data.size()));
    }

    public void clearData(){
        this.data.clear();
    }

    public void getCurrentFollowData(User currentUser){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query followQuery = mDatabase.child("friend").child(String.valueOf(currentUser.getId())).child("follow");
        followQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    for (DataSnapshot item: task.getResult().getChildren()) {
                        // TODO: handle the post
                        String value = String.valueOf(item.getValue());
                        String key = String.valueOf(item.getKey());
                        if (value == "true"){
                            mDatabase.child("user").child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
//                                        Toast.makeText("", "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        if (!data.contains(task.getResult().getValue(User.class))) {
                                            followData.add(task.getResult().getValue(User.class));
                                        }
                                        if (lastActivity == Friend_Activity) {
                                            FriendActivity.FriendActivity_instance.getAdapter().notifyDataSetChanged();
                                        }
                                        else{
                                            AddFriendActivity.AddFriendActivity_instance.getAdapter().notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                        }
                    }
//                    handler.sendEmptyMessage(Get_Query_Follow_Success);
                }
            }
        });
    }

    public void getQueryFollowData(User queryUser){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query followQuery = mDatabase.child("friend").child(String.valueOf(queryUser.getId())).child("follow");
        followQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    for (DataSnapshot item: task.getResult().getChildren()) {
                        // TODO: handle the post
                        String value = String.valueOf(item.getValue());
                        String key = String.valueOf(item.getKey());
                        if (value == "true"){
                            mDatabase.child("user").child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
//                                        Toast.makeText("", "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        if (!data.contains(task.getResult().getValue(User.class))) {
                                            data.add(task.getResult().getValue(User.class));
                                        }
//                                        this.notify();
                                        FriendActivity.FriendActivity_instance.getAdapter().notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
//                    handler.sendEmptyMessage(Get_Query_Follow_Success);
                }
            }
        });
    }

    public void getUserById(String userId){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
//                  Toast.makeText("", "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!data.contains(task.getResult().getValue(User.class))) {
                            data.add(task.getResult().getValue(User.class));
                    }
                    AddFriendActivity.AddFriendActivity_instance.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }


    public List<User> getData() {
        return data;
    }

    public void setFollowData(List<User> followData) {
        this.followData = followData;
    }

    public AddFriendRecyclerAdapter(User user, Context context, int lastActivity) {
        this.user = user;
//        this.listener = listener;
        this.context = context;
        this.lastActivity = lastActivity;

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_no_picture)
                .showImageOnFail(R.drawable.default_no_picture)
                .showImageForEmptyUri(R.drawable.default_no_picture)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.default_no_picture)
                .build();

        this.circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_head)
                .showImageOnFail(R.drawable.default_head)
                .showImageForEmptyUri(R.drawable.default_head)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_firend_recycle_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this.context));
            ImageLoader.getInstance().displayImage(data.get(position).getImg(), holder.avatar, circleOptions);

            holder.username.setText(data.get(position).getUserName());
            holder.totalTime.setText("总运动时长:"+data.get(position).getTotalTime());

            if (followData!=null&&followData.size()>0&&followData.contains(data.get(position))) {
                holder.alreadyFollowLayout.setVisibility(View.VISIBLE);
            } else if (user!=null&&user.equals(data.get(position))){
                holder.followLayout.setVisibility(View.INVISIBLE);
                holder.alreadyFollowLayout.setVisibility(View.INVISIBLE);
            }else {
                holder.followLayout.setVisibility(View.VISIBLE);
            }

    }

    @Override
    public int getItemCount() {
        return data==null||data.size()<=0?0:data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView avatar;
        private TextView username;
        private TextView totalTime;
        private LinearLayout followLayout;
        private LinearLayout alreadyFollowLayout;
        private Button btn_follow;
        private Button btn_cancel_follow;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.image_item_add_friend_avatar);
            username = (TextView) itemView.findViewById(R.id.text_item_add_friend_name);
            totalTime = (TextView) itemView.findViewById(R.id.text_item_add_friend_total_time);
            followLayout = (LinearLayout) itemView.findViewById(R.id.linear_item_add_friend_follow);
            alreadyFollowLayout = (LinearLayout) itemView.findViewById(R.id.linear_item_add_friend_already_follow);
            btn_follow = (Button) itemView.findViewById(R.id.btn_follow);
            btn_cancel_follow = (Button) itemView.findViewById(R.id.btn_cancel_follow);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                }
            });

            btn_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("关注" + getLayoutPosition());
                    User aimFollow = data.get(getLayoutPosition());
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("friend").child(String.valueOf(user.getId())).child("follow")
                            .child(String.valueOf(aimFollow.getId())).setValue(true);
                    mDatabase.child("friend").child(String.valueOf(aimFollow.getId())).child("fans")
                            .child(String.valueOf(user.getId())).setValue(true);
                    followLayout.setVisibility(view.INVISIBLE);
                    alreadyFollowLayout.setVisibility(view.VISIBLE);
                }
            });

            btn_cancel_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("取消关注" + getLayoutPosition());
                    User aimCancel = data.get(getLayoutPosition());
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("friend").child(String.valueOf(user.getId())).child("follow")
                            .child(String.valueOf(aimCancel.getId())).removeValue();
                    mDatabase.child("friend").child(String.valueOf(aimCancel.getId())).child("fans")
                            .child(String.valueOf(user.getId())).removeValue();
                    followLayout.setVisibility(view.VISIBLE);
                    alreadyFollowLayout.setVisibility(view.INVISIBLE);
                }
            });

        }

        @Override
        public void onClick(View v) {
//            if (listener!=null) {
//                listener.onItemClick(getAdapterPosition());
//            }
        }


    }
}
