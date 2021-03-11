package com.zyh.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyh.activities.About;
import com.zyh.activities.LoginActivity;
import com.zyh.activities.MainActivity;
import com.zyh.beans.HeadPicBean;
import com.zyh.beans.LoginBean;
import com.zyh.utills.FileUtil;
import com.zyh.utills.PhotoPopupWindow;
import com.zyh.utills.Utills;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;


public class IndividualFragment extends Fragment {

    String token;
    String cookie;
    String name;
    String stuId;
    String college;
    String major;
    String className;
    TextView name_text;
    TextView stuId_text;
    TextView college_text;
    TextView marjor_text;
    TextView className_text;
    com.makeramen.roundedimageview.RoundedImageView head_pic;
    Button logout;
    Button about;
    private PhotoPopupWindow mPhotoPopupWindow;
    public static File tempFile;
    private Uri imageUri;
    public static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    public static final int CROP_PHOTO = 2; //剪裁
    public static final int SELECT_PHOTO = 3;//选取图片


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.individual, container, false);
        final MainActivity mainActivity = (MainActivity)getActivity();

        LoginBean.Datas.StuInfo stuInfo = ((LoginBean.Datas)mainActivity.loginBean.getData()).getStuInfo();
        name = stuInfo.getName();
        stuId = stuInfo.getStuId();
        college = stuInfo.getCollege();
        major = stuInfo.getMajor();
        className = stuInfo.getClassName();
        token = mainActivity.loginBean.getData().getToken();
        cookie = mainActivity.loginBean.getData().getCookie();
        initView(view);
        getHeadPic();
        Log.d("IndividualFragment","ActionBegin");
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                startActivity(intent);
                mainActivity.finish();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, About.class);
                startActivity(intent);
            }
        });

        head_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoPopupWindow = new PhotoPopupWindow(getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 进入相册选择
                        openGallery();
                        mPhotoPopupWindow.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 拍照
                        openCamera();
                        mPhotoPopupWindow.dismiss();
                    }
                });
                View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_main, null);
                mPhotoPopupWindow.showAtLocation(rootView,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
        return view;
    }



    private void initView(View view){
        logout = (Button)view.findViewById(R.id.logout);
        head_pic = (com.makeramen.roundedimageview.RoundedImageView)view.findViewById(R.id.head_pic);
        name_text = (TextView)view.findViewById(R.id.name);
        stuId_text = (TextView)view.findViewById(R.id.stu_id);
        college_text = (TextView)view.findViewById(R.id.college);
        marjor_text = (TextView)view.findViewById(R.id.major);
        className_text = (TextView)view.findViewById(R.id.class_name);
        about = view.findViewById(R.id.about);
        name_text.setText(name);
        stuId_text.setText(stuId);
        college_text.setText(college);
        marjor_text.setText(major);
        className_text.setText(className);
    }
    private void getHeadPic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("cookie",cookie)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://42.193.177.76:8081/getHeadImg")
                            .post(requestBody)
                            .addHeader("token",token)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    HeadPicBean headPicBean = Utills.parseJSON(responseData,HeadPicBean.class);
                    ShowHeadPic(headPicBean.getData());
                }catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void ShowHeadPic(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                head_pic.setImageBitmap(decodedByte);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_CAREMA:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra("aspectX", 1); // 宽高比例
                    intent.putExtra("aspectY", 1);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        File picFile = uirToFile(imageUri);
                        Log.d("oringalSize",FileUtil.getFileOrFilesSize(picFile)+"");
                        picFile = FileUtil.compress(picFile);
                        Log.d("laterSize",FileUtil.getFileOrFilesSize(picFile)+"");
                        if (!FileUtil.isPicLegal(picFile,7)){
                            Toast.makeText(getActivity(),"图片能不超过8MB,且以jpg,jpeg,png,gif后缀",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        picFile = FileUtil.modifyFileName(picFile);
                        postSetHeadImg(picFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        if(data != null) {
                            Uri uri = data.getData();
                            if (uri.toString().contains("com.miui.gallery.open")) {
                                Log.d("miui","miui");
                                uri = FileUtil.getImageContentUri(getActivity(), new File(FileUtil.getRealFilePath(getActivity(), uri)));
                            }
                            imageUri = uri;
                        }
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver()
                                .openInputStream(imageUri));
                        //picture.setImageBitmap(bitmap);
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(imageUri, "image/*");
                        intent.putExtra("scale", true);
                        intent.putExtra("aspectX", 1); // 宽高比例
                        intent.putExtra("aspectY", 1);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }
    public void openCamera() {
        //獲取系統版本
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                //检查是否有存储权限，以免崩溃
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    Toast.makeText(getActivity(),"请开启存储权限",Toast.LENGTH_SHORT).show();
                    return;
                }
                imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
     * 判断sdcard是否被挂载
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
    public String bitmapToString(Bitmap bitmap){
        //将Bitmap转换成字符串
        String string=null;
        ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[]bytes=bStream.toByteArray();
        string=Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }
    private void postPicToServer(final String token, final String cookie, final File pic){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    MediaType MEDIA_TYPE_PNG = MediaType.parse("image");
                    RequestBody requestBody = MultipartBody.create(MEDIA_TYPE_PNG, pic);
                    // 文件上传的请求体封装
                    MultipartBody multipartBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("cookie",cookie)
                            .addFormDataPart("img", pic.getName(), requestBody)
                            .build();
//                    RequestBody requestBody = new FormBody.Builder()
//                            .add("cookie",cookie)
//                            .add("img",pic)
//                            .build();
                    Request request = new Request.Builder()
                            .url("http://42.193.177.76:8081/setHeadImg")
                            .post(multipartBody)
                            .addHeader("token",token)
                            .build();
                    Log.d("setHeadPic","ready");
                    Response response = client.newCall(request).execute();
                    Log.d("setHeadPic","done");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getHeadPic();
                        }
                    });

                    String responseData = response.body().string();
                }catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void postSetHeadImg(File pic){
        postPicToServer(token,cookie,pic);
    }
    private File uirToFile(Uri uri){
        String[] arr = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, arr, null, null, null);
        int imgIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(imgIndex);
        File file = new File(imgPath);
        return file;
    }
    public static String getImgMimeType(File imgFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getPath(), options);
        return options.outMimeType;
    }



}