package com.example.myapplication.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.Activity.AddFriendActivity;
import com.example.myapplication.Activity.DynamicPublishActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.DynamicRecyclerAdapter;
import com.example.myapplication.db.entity.Dynamic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 评论等（需要friend功能）
 */

public class SocialFragment extends Fragment {
    public static final int DYNAMIC_SIZE = 3;

    private Context context;
    private String userId;
    private String userName;
    private ArrayList<Dynamic> data = new ArrayList<Dynamic>(); //动态数据
    private DynamicRecyclerAdapter adapter;
    private ArrayList<String> userTimeline = new ArrayList<String>();
    private ArrayList<String> likes = new ArrayList<String>();

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private Handler handler = new Handler();
    private DatabaseReference mDatabase;

    private int lastVisibleItemPosition;
    private int position = 0;
    private boolean isLoading = false; //加载更多

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("SocialFragment", "Create");
        SharedPreferences pref = getActivity().getSharedPreferences("LogInfo",Context.MODE_PRIVATE);     //获取当前用户id
        userId = pref.getString("user_id","");
        userName = pref.getString("user_name","");
        mDatabase = FirebaseDatabase.getInstance().getReference();  //谷歌数据库
        initListener(); //初始化对timeline和likes的监听
        context = getActivity().getApplicationContext();
        data = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_social,container,false);
        initComponent(view);

        return view;
    }

    @Override
    public void onResume(){
        Log.i("SocialFragment", "Resume");
        super.onResume();

        initRefreshDynamic();
    }

    private void initComponent(View view) {
        ImageButton pushDynamicBtn = (ImageButton) view.findViewById(R.id.push_dynamic_btn);
        pushDynamicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DynamicPublishActivity.class));
            }
        });

        ImageButton addFriendBtn = (ImageButton) view.findViewById(R.id.add_friend_btn);
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddFriendActivity.class));
            }
        });

        TextView socialUserName = (TextView) view.findViewById(R.id.social_user_name);
        socialUserName.setText(userName);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_home_swiperefresh);
        refreshLayout.setColorSchemeResources(android.R.color.holo_orange_light);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_dynamic);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context,LinearLayoutManager.VERTICAL));
        adapter = new DynamicRecyclerAdapter(getActivity(),getActivity());
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (userTimeline != null) {
                    if(true){
//                    if(GeneralUtil.isNetworkAvailable(context)) {//网络提供
                        //获取动态
                        refreshDynamic();
                    } else{
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(context,"没有网络连接，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                Log.i("SocialFragment", "Scroll");
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    //手指上抛，最后可见的item位置+1等于适配器中数据个数，即最后一个Item可见
                    boolean isRefreshing = refreshLayout.isRefreshing();
                    if (isRefreshing) {//正在刷新
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        //显示加载更多布局
                        adapter.setIsLoadMore(true);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isLoading = false;
                                //延迟2秒加载数据
                                loadDynamic();
                            }
                        }, 2000);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void initListener(){    //设置监听用户发动态的listener
        ChildEventListener dynamicListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot,String previousChildName) {
                Log.i("SocialFragment", "inListen");
                if(dataSnapshot.exists()) {
                    userTimeline.add(dataSnapshot.getKey());
                    Collections.sort(userTimeline);
                    Collections.reverse(userTimeline);
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
        Query timelineQuery = mDatabase.child("user").child(userId).child("all_dynamic").orderByChild("date");
        timelineQuery.addChildEventListener(dynamicListener);

        ChildEventListener likesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot,String previousChildName) {
                if(dataSnapshot.exists()) {
                    likes.add(dataSnapshot.getKey());
                    adapter.setLikes(likes);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.i("Likes", "childRemoved");
                if(snapshot.exists()) {
                    likes.remove(snapshot.getKey());
                    adapter.setLikes(likes);
                    adapter.notifyDataSetChanged();
                }
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
        Query likesQuery = mDatabase.child("user").child(userId).child("likeDynamic");
        likesQuery.addChildEventListener(likesListener);

        ChildEventListener commentListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot,String previousChildName) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    Dynamic dynamic = snapshot.getValue(Dynamic.class);
                    int index = userTimeline.indexOf(dynamic.getId());
                    Log.i("tuisong", String.valueOf(index));
                    if(index >= 0) {
                        int dataHas = -1;
                        for (Dynamic each: data){
                            if(each.getId().equals(dynamic.getId())){   //如果可见的动态里有
                                dataHas = data.indexOf(each);
                            }
                        }
                        if(dataHas != -1){
                            data.remove(dataHas);
                        }
                        data.add(0,dynamic);
                    }else{
                        //尝试推送
                        if(dynamic.getLikesCount() >= 10)
                            userTimeline.add(dynamic.getId());
                    }
                }
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

        Query dynamicQuery = mDatabase.child("dynamic");
        dynamicQuery.addChildEventListener(commentListener);
    }

    /**
     * 上拉加载动态
     */
    private void loadDynamic() {
        Log.i("SocialFragment", "load");
        Query dynamicQuery =  mDatabase.child("dynamic");
        dynamicQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task){
                if(task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    if(position == userTimeline.size()){
                        Toast.makeText(getContext(),"已到最底部",Toast.LENGTH_SHORT).show();
                    }else{
                        for(int i = 0;i < DYNAMIC_SIZE;i++){
                            if(position < userTimeline.size()){
                                String timeline = userTimeline.get(position);
                                data.add(dataSnapshot.child(timeline).getValue(Dynamic.class));
                                Log.i("SocialFragment",position +String.valueOf(dataSnapshot.child(timeline).getValue(Dynamic.class)));
                                position++;
                            }
                        }
                    }
                    Log.i("SocialFragment", String.valueOf(data.size()));
                    refreshLayout.setRefreshing(false);
                    adapter.setData(data);
                    adapter.setIsLoadMore(false);
                }else{
                    Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 初次刷新加载动态
     */
    private void initRefreshDynamic(){
        refreshLayout.setRefreshing(true);//开始刷新
        //刷新动态
        refreshDynamic();
    }

    /**
     * 下拉刷新查询动态
     */
    private void refreshDynamic(){
        Query dynamicQuery =  mDatabase.child("dynamic");
        dynamicQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task){
                if(task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    if(userTimeline.size() !=0 ){
                        if(data != null && data.size() > 0 && data.get(0).getId().equals(dataSnapshot.
                                child(userTimeline.get(0)).getKey())){
                            refreshLayout.setRefreshing(false);
                            adapter.setData(data);
                            adapter.setLikes(likes);
                            adapter.setIsLoadMore(false);
                            Toast.makeText(context, "没有新动态", Toast.LENGTH_SHORT).show();
                        }else{
                            position = 0;
                            data.clear();
                            for(int i = 0;i < DYNAMIC_SIZE;i++){        //下拉刷新这样写没啥问题
                                if(position < userTimeline.size()){
                                    String timeline = userTimeline.get(position);
                                    Dynamic newDynamic = dataSnapshot.child(timeline).getValue(Dynamic.class);
                                    newDynamic.setId(dataSnapshot.child(timeline).getKey());
                                    data.add(newDynamic);
//                                    Log.i("SocialFragment",position +String.valueOf(dataSnapshot.child(timeline).getValue(Dynamic.class)));
                                    position++;
                                }
                                else break;
                            }
//                            Log.i("SocialFragment", String.valueOf(data.size()));
                            refreshLayout.setRefreshing(false);
                            adapter.setData(data);
                            adapter.setIsLoadMore(false);
                            Toast.makeText(context, "刷新完成", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(context, "没有动态，去写一个吧！", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(),"刷新失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
