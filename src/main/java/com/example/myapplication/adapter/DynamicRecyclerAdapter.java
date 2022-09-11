package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activity.DynamicDetailsActivity;
import com.example.myapplication.R;
import com.example.myapplication.db.entity.Dynamic;
import com.example.myapplication.db.entity.User;
import com.example.myapplication.view.NineGridLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int Item_Type_Footer = 0;
    private static final int Item_Type_Normal = 1;

    private boolean isLoadMore = false;//是否显示footerview,默认不显示

    private Context context;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoader imageLoaderHead = ImageLoader.getInstance();

    private String userId;
    private String noticeStr = null;
    private List<String> likes = new ArrayList<>();
    private List<Dynamic> data = null;
    public int lastPosition = -1;
    private Activity activity;

    private DisplayImageOptions options;
    private DisplayImageOptions circleOptions;
    private DatabaseReference mDatabase;

    public DynamicRecyclerAdapter(Context context,Activity activity){
        this.activity = activity;
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();  //谷歌数据库
        SharedPreferences pref = context.getSharedPreferences("LogInfo",Context.MODE_PRIVATE);     //获取当前用户id
        userId = pref.getString("user_id","");
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
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.default_avatar)
                .displayer(new CircleBitmapDisplayer())
                .build();
    }

    public void setData(List<Dynamic> data) {
        this.data = data;
    }

    public void setNoticeStr(String noticeStr) {
        this.noticeStr = noticeStr;
    }

    public boolean isLoadMore() {

        return isLoadMore;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }


    /**
     * 设置是否显示上拉加载更多，默认不显示
     * @param isLoadMore
     */
    public void setIsLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;

        //刷新数据
        notifyDataSetChanged();
    }

    private void setAnimation(View view,int position) {
        if(position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(),R.animator.item_bottom_in);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case Item_Type_Footer:
                View footView =LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_foot_view_item,parent,false);
                return new RecyclerFootViewHolder(footView);
            case Item_Type_Normal:
                View normalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_dynamic_item,parent,false);
                return new DynamicViewHolder(normalView);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof DynamicViewHolder) {
            final DynamicViewHolder viewHolder = (DynamicViewHolder) holder;
            final Dynamic dynamic = data.get(position);

            mDatabase.child("user").child(dynamic.getFromUserId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        User fromUser = task.getResult().getValue(User.class);
                        imageLoaderHead.init(ImageLoaderConfiguration.createDefault(context));
                        imageLoaderHead.displayImage(fromUser.getImg(), viewHolder.avatar, circleOptions);
                        viewHolder.nickName.setText(fromUser.getUserName());
                        viewHolder.time.setText(dynamic.getDate());

                        viewHolder.nineGridLayout.setImageLoader(new NineGridLayout.ImageLoader() {
                            @Override
                            public void onDisplayImage(Context context, ImageView imageView, String url) {
                                imageLoader.init(ImageLoaderConfiguration.createDefault(context));
                                imageLoader.displayImage(url, imageView, options);
                            }
                        });

                        viewHolder.nineGridLayout.setImageUrls(dynamic.getImage());

                        String content = dynamic.getContent();
                        String shortContent = "";

                        if(content.length() > 20){
                            shortContent = content.substring(0,20) + "...";
                        }else{
                            shortContent = content;
                        }

                        viewHolder.content.setText(shortContent);

                        Log.i("Likes",dynamic.getId()+" "+ likes.size());

                        viewHolder.commentCount.setText(dynamic.getCommentCount().toString());
                        viewHolder.likeCount.setText(dynamic.getLikesCount().toString());
                        if (likes.contains(dynamic.getId())) {
                            viewHolder.likeImage.setImageResource(R.drawable.aleadylike);
                        } else {
                            viewHolder.likeImage.setImageResource(R.drawable.like);
                        }

                        Log.i("DynamicDetail",dynamic.getContent());

                        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("DynamicDetail",dynamic.getId());
                                Intent intent = new Intent(context, DynamicDetailsActivity.class);
                                intent.putExtra("dynamic_id", dynamic.getId());
                                activity.startActivity(intent);
                            }
                        });

                        viewHolder.likeRelative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (viewHolder.isClickFinish) {
                                    doLike(dynamic, viewHolder);
                                    viewHolder.isClickFinish = false;
                                }
                            }
                        });
                    }
                }
            });

//            viewHolder.shareRelative.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    OnekeyShare oks = new OnekeyShare();
//
//                    //关闭sso授权
//                    oks.disableSSOWhenAuthorize();
//                    // title标题：微信、QQ（新浪微博不需要标题）
//                    oks.setTitle("动态");  //最多30个字符
//                    // text是分享文本：所有平台都需要这个字段
//                    if (dynamic.getContent() != null) {
//                        int lengthStr = dynamic.getContent().length();
//                        oks.setText(dynamic.getContent().substring(0, lengthStr > 40 ? 35 : lengthStr));  //最多40个字符
//                    }
//                    // imagePath是图片的本地路径：除Linked-In以外的平台都支持此参数
//                    //oks.setImagePath(Environment.getExternalStorageDirectory() + "/meinv.jpg");//确保SDcard下面存在此张图片
//
//                    if (dynamic.getImage() != null && dynamic.getImage().size() > 0) {
//                        //网络图片的url：所有平台
//                        oks.setImageUrl(dynamic.getImage().get(0));//网络图片rul
//                    }
////                // url：仅在微信（包括好友和朋友圈）中使用
//                    oks.setUrl("http://runbang.bmob.cn");   //网友点进链接后，可以看到分享的详情
////                // Url：仅在QQ空间使用
//                    oks.setTitleUrl("http://runbang.bmob.cn");  //网友点进链接后，可以看到分享的详情
//                    // 启动分享GUI
//                    oks.show(context);
//
//                }
//            });
//
//            viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent avatarIntent = new Intent(context, PersonProfileActivity.class);
//                    avatarIntent.putExtra("userid", dynamic.getFromUser().getObjectId());
//                    homeFragment.startActivity(avatarIntent);
//                }
//            });

            setAnimation(viewHolder.cardView, position);
        } else if (holder instanceof RecyclerFootViewHolder) {
            RecyclerFootViewHolder footViewHolder = (RecyclerFootViewHolder) holder;
            footViewHolder.loadMore.setText("加载更多");
            setAnimation(footViewHolder.linearLayout,position);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isLoadMore&&position+1 == getItemCount()){
            return Item_Type_Footer;
        } else {
            return Item_Type_Normal;
        }
    }

    @Override
    public int getItemCount() {
        if (isLoadMore) {
            return data==null||data.size()==0?0:data.size()+1;
        }
        return data==null||data.size()==0?0:data.size();
    }

    /**
     * 点赞处理
     * @param dynamic
     * @param holder
     */
    private void doLike(final Dynamic dynamic, final DynamicViewHolder holder) {
        Log.i("Likes", String.valueOf(likes.size()));
        if (!likes.contains(dynamic.getId())) { //没有点过赞
            Log.i("Likes", "没有点过赞");
            Map<String, Object> childUpdates = new HashMap<>();
            String path1 = "/dynamic/" + dynamic.getId() + "/likeUser/" + userId;
            Map<String, Boolean> likeUser = new HashMap<>();
            likeUser.put(userId, true);
            childUpdates.put(path1, likeUser);

            String path2 = "/user/" + userId + "/likeDynamic/" + dynamic.getId();
            Map<String, Boolean> likesDynamic = new HashMap<>();
            likesDynamic.put(dynamic.getId(), true);
            childUpdates.put(path2, likesDynamic);

            mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dynamic.setLikesCount(dynamic.getLikesCount() + 1);
                            mDatabase.child("dynamic").child(dynamic.getId()).
                                    child("likesCount").setValue(dynamic.getLikesCount()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            holder.isClickFinish = true;
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            holder.isClickFinish = true;
                                            Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            holder.isClickFinish = true;
                            Toast.makeText(context, "点赞失败，请稍后重试", Toast.LENGTH_SHORT);
                        }
                    });
        } else {
            Log.i("Likes", "已点赞");
            Map<String, Object> childUpdates = new HashMap<>();
            String path1 = "/dynamic/" + dynamic.getId() + "/likeUser/" + userId;
            Map<String, Boolean> likeUser = new HashMap<>();
            likeUser.put(userId, null);
            childUpdates.put(path1, likeUser);

            String path2 = "/user/" + userId + "/likeDynamic/" + dynamic.getId();
            Map<String, Boolean> likesDynamic = new HashMap<>();
            likesDynamic.put(dynamic.getId(), null);
            childUpdates.put(path2, likesDynamic);

            mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dynamic.setLikesCount(dynamic.getLikesCount() - 1);
                            mDatabase.child("dynamic").child(dynamic.getId()).
                                    child("likesCount").setValue(dynamic.getLikesCount()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            holder.isClickFinish = true;
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "取消点赞成功", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            holder.isClickFinish = true;
                                            Toast.makeText(context, "取消点赞失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            holder.isClickFinish = true;
                            Toast.makeText(context, "取消点赞失败，请稍后重试", Toast.LENGTH_SHORT);
                        }
                    });
        }
    }

}
