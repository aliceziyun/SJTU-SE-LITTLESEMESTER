package com.example.myapplication.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.R;
import com.example.myapplication.db.entity.User;
import com.example.myapplication.util.FileUtils;
import com.example.myapplication.util.ImageUtils;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

public class ModifyActivity extends BaseActivity implements View.OnClickListener {
    private static final int SCAN_OPEN_PHONE = 0x02;// 相册
    private final static int REQUEST_CODE_CAMERA=0X01; //拍照的requestcode

    private final static int MODIFY_USERNAME = 1;
    private final static int MODIFY_SEX = 2;
    private final static int MODIFY_IMG = 3;

    private ImageButton btn_back, gender_choose, birthday_choose, height_choose, weight_choose,profile_choose;
    private TextView change_username,photoText,cameraText,cancelText;
    private TextView gender_text;
    private PopupWindow popupWindow;
    private ImageView user_profile;
    private ImageLoader imageLoader = ImageLoader.getInstance();;
    private DisplayImageOptions options;
    private String userId;
    private User user;

    private DatabaseReference mDatabase;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private String[] gender_arr = new String[]{"男","女","保密"};

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case MODIFY_SEX:
                    gender_text.setText(msg.obj.toString());
                    break;
                case MODIFY_USERNAME:
                    change_username.setText(msg.obj.toString());
                    break;
                case MODIFY_IMG:

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        SharedPreferences pref = getSharedPreferences("LogInfo",MODE_PRIVATE);     //获取当前用户id
        userId = pref.getString("user_id","");

        //获取当前用户
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(ModifyActivity.this, "网络错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                } else {
                  user = task.getResult().getValue(User.class);
                  init();
                  initPopupWindow();
                }
            }
        });
    }

    private void init(){
        btn_back = findViewById(R.id.modify_back);
        gender_choose = findViewById(R.id.gender_choose);
        gender_text = findViewById(R.id.gender_text);
        change_username = findViewById(R.id.change_username);
        birthday_choose = findViewById(R.id. birthday_choose);
        height_choose = findViewById(R.id.height_choose);
        weight_choose = findViewById(R.id.weight_choose);
        user_profile = findViewById(R.id.user_profile);
        profile_choose = findViewById(R.id.profile_choose);

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.default_avatar)
                .build();

        change_username.setText(user.getUserName());
        switch (user.getSexual()){
            case 0:
                gender_text.setText("男");
                break;
            case 1:
                gender_text.setText("女");
                break;
            case 2:
                gender_text.setText("保密");
                break;
        }
        if(user.getImg() != null){
            String headImgUrl = user.getImg();
            imageLoader.init(ImageLoaderConfiguration.createDefault(ModifyActivity.this));
            imageLoader.displayImage(headImgUrl, user_profile, options);
        }

        //注册按钮监听事件
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModifyActivity.this.finish();
            }
        });
        profile_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_modify, null), Gravity.BOTTOM, 0, 0);
            }
        });
        gender_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowGenderChooseDialog();
            }
        });
        change_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateNameDialog();
            }
        });
        birthday_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar nowdate = Calendar.getInstance();
                final int mYear = nowdate.get(Calendar.YEAR);
                final int mMonth = nowdate.get(Calendar.MONTH);
                final int mDay = nowdate.get(Calendar.DAY_OF_MONTH);
                //调用DatePickerDialog
                new DatePickerDialog(ModifyActivity.this, onDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        height_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateHeightDialog();
            }
        });
        weight_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateWeightDialog();
            }
        });
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

    private void ShowGenderChooseDialog(){
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);// 自定义对话框
        builder3.setSingleChoiceItems(gender_arr, 0, new DialogInterface.OnClickListener() {// 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                showProgressDialog(ModifyActivity.this,"修改中");
                String sex = "";
                int sexual = 0;
                gender_text.setText(gender_arr[which]);
                switch (gender_arr[which]){
                    case "男":
                        user.setSexual(0);
                        sex = "男";
                        sexual = 0;
                        break;
                    case "女":
                        user.setSexual(1);
                        sex = "女";
                        sexual = 1;
                        break;
                    case "保密":
                        user.setSexual(2);
                        sex = "保密";
                        sexual = 2;
                        break;
                }

                final String msgSex = sex;
                mDatabase.child("user").child(userId).child("sexual").setValue(sexual).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();// 点击一个item消失对话框，不用点击确认取消
                        Message msg = new Message();
                        msg.obj = msgSex;
                        msg.what = MODIFY_SEX;
                        handler.sendMessage(msg);
                    }
                });
            }
        });
        builder3.show();// 让弹出框显示
    }

    private void onCreateNameDialog() {
        // 使用LayoutInflater来加载dialog_setname.xml布局
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View nameView = layoutInflater.inflate(R.layout.dialog_setname, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // 使用setView()方法将布局显示到dialog
        alertDialogBuilder.setView(nameView);

        final EditText userInput = (EditText) nameView.findViewById(R.id.changename_edit);

        // 设置Dialog按钮
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final String userName = userInput.getText().toString();
                                // 获取edittext的内容,显示到textview
                                user.setUserName(userInput.getText().toString());
                                mDatabase.child("user").child(userId).child("userName").setValue(userName)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();// 点击一个item消失对话框，不用点击确认取消
                                        Message msg = new Message();
                                        msg.obj = userName;
                                        msg.what = MODIFY_USERNAME;
                                        handler.sendMessage(msg);
                                    }
                                });
                                //修改
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /**
     * 日期选择器对话框监听
     */
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            TextView date_textview = (TextView) findViewById(R.id.birthday_text);
            String days;
            days = new StringBuffer().append(mYear).append("-").append(mMonth).append("-").append(mDay).toString();
            date_textview.setText(days);
        }
    };

    private void onCreateHeightDialog(){
        // 使用LayoutInflater来加载dialog_setname.xml布局
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View heightView = layoutInflater.inflate(R.layout.dialog_setheight, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // 使用setView()方法将布局显示到dialog
        alertDialogBuilder.setView(heightView);

        final EditText userInput = (EditText) heightView.findViewById(R.id.changeheight_edit);
        final TextView height = (TextView) findViewById(R.id.change_userheight);

        // 设置Dialog按钮
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 获取edittext的内容,显示到textview
                                height.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void onCreateWeightDialog(){
        // 使用LayoutInflater来加载dialog_setname.xml布局
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View weightView = layoutInflater.inflate(R.layout.dialog_setweight, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // 使用setView()方法将布局显示到dialog
        alertDialogBuilder.setView(weightView);

        final EditText userInput = (EditText) weightView.findViewById(R.id.changeweight_edit);
        final TextView weight = (TextView) findViewById(R.id.change_userweight);

        // 设置Dialog按钮
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 获取edittext的内容,显示到textview
                                weight.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case SCAN_OPEN_PHONE: {
                if (resultCode == RESULT_CANCELED) {   //取消操作
                    return;
                }
                Uri uri = data.getData();
                String[] FilePathColumn={MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri,
                        FilePathColumn, null, null, null);
                cursor.moveToFirst();
                //从数据视图中获取已选择图片的路径
                int columnIndex = cursor.getColumnIndex(FilePathColumn[0]);
                String picPath = cursor.getString(columnIndex);

                Bitmap bitmap = null;
                BufferedInputStream bis = null;
                try {
                    bis = new BufferedInputStream(new FileInputStream(picPath));
                    bitmap = BitmapFactory.decodeStream(bis);
                    bis.close();
                    setProfile(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case REQUEST_CODE_CAMERA: {

                if (resultCode == RESULT_CANCELED) {     //取消操作
                    ImageUtils.deleteImageUri(this, ImageUtils.getCurrentUri());   //删除Uri
                }

                Bitmap bitmap=null;
                Bundle bundle=data.getExtras();
                if (bundle!=null) {
                    bitmap = (Bitmap) bundle.get("data");//缩略图
                } else {
                    Toast.makeText(ModifyActivity.this,"拍照失败！",Toast.LENGTH_SHORT).show();
                    return;
                }
                setProfile(bitmap);
                break;
            }
            default:
                break;
        }
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
                ActivityCompat.requestPermissions(ModifyActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                },0);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SCAN_OPEN_PHONE);
                popupWindow.dismiss();
                break;
            case R.id.popup_cancel:
                popupWindow.dismiss();
                break;
        }
    }

    public void setProfile(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if(width > height)
            width = height;
        else height = width;
        // 计算缩放比例
        float scaleWidth = ((float) 200) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);

        Bitmap bitmap2 =  Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        user_profile.setImageBitmap(bitmap2);
        String filePath = FileUtils.saveBitmapToFile(bitmap2,"headImg");
        Uri headImg = Uri.fromFile(new File(filePath));
        user.setImg(filePath);
        Log.i("UserPersonal", "in");
        StorageReference picRef = storage.getReference().child("headImg").child(userId).child(userId +".jpg");
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                UploadTask uploadTask = picRef.putFile(headImg);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ModifyActivity.this, "上传头像失败！", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getUploadImageUrl(userId);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                UploadTask uploadTask = picRef.putFile(headImg);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ModifyActivity.this, "上传头像失败！", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getUploadImageUrl(userId);
                    }
                });
            }
        });
    }

    public void getUploadImageUrl(String userId){
        String fileName = userId + ".jpg";
        StorageReference uploadRef = storage.getReference().child("headImg").child(userId).child(fileName);
        uploadRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("UserPersonal", String.valueOf(uri));
                mDatabase.child("user").child(userId).child("img").setValue(uri.toString());
                Message msg = new Message();
                msg.obj = uri.toString();
                msg.what = MODIFY_IMG;
                handler.sendMessage(msg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ModifyActivity.this, "获取头像失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
