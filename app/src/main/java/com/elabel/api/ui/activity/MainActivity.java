package com.elabel.api.ui.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.elabel.api.R;
import com.elabel.api.app.Constants;
import com.elabel.api.app.ErrorHandleSubscriber;
import com.elabel.api.blesdk.BleDeviceFeedback;
import com.elabel.api.blesdk.BleManager;
import com.elabel.api.blesdk.BleTask;
import com.elabel.api.blesdk.SendWork;
import com.elabel.api.databinding.ActivityMainBinding;
import com.elabel.api.injection.component.AppComponent;
import com.elabel.api.injection.component.DaggerMainComponent;
import com.elabel.api.injection.module.MainModule;
import com.elabel.api.mvp.model.entity.BleData;
import com.elabel.api.mvp.model.entity.LEDDataEntity;
import com.elabel.api.mvp.model.entity.ResponseBase;
import com.elabel.api.mvp.model.service.RepositoryManager;
import com.elabel.api.ui.base.BaseActivity;
import com.elabel.api.utils.HandlerHelper;
import com.elabel.api.utils.LocationUtils;
import com.elabel.api.utils.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    // eLabel APP Account and Password
    private static final String loginId = "[Account]";
    private static final String password = "Password";

    private ActivityMainBinding binding;

    @Inject
    RepositoryManager repositoryManager;

    private final ActivityResultLauncher<Intent> openGPSLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()==RESULT_OK){
                checkGPS();
            }
        }
    });

    @Override
    protected void setupComponent(AppComponent appComponent) {
        DaggerMainComponent
                .builder()
                .appComponent(appComponent)
                .mainModule(new MainModule())
                .build()
                .inject(this);
    }

    @Override
    protected View getView() {
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onPermissionComplete() {
        binding.btnSend.setOnClickListener(this::viewClick);
        binding.btnLogin.setOnClickListener(this::viewClick);
        initBle();
    }

    private void viewClick(View view) {
        if(view.getId()==R.id.btn_login){
            login();
        }else if(view.getId()==R.id.btn_send){
            //label barcode
            String tagBarcode="400000037CB3";
            getData(tagBarcode);
            //getLEDData(tagBarcode);
        }
    }

    private void login(){
        repositoryManager.token(loginId,password)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<ResponseBase<String>>() {
                    @Override
                    public void onNext(@NonNull ResponseBase<String> responseBase) {
                        if(responseBase.getCode()==0) {
                            Log.i("YUAN","Login succeeded");
                            Constants.token=responseBase.getBody();
                            showToast("登录成功");
                            binding.txtMessage.setText("登录成功");
                        }else if(responseBase.getCode()==1001){
                            /**
                             * code = 1001
                             * User name or password error
                             */
                            binding.txtMessage.setText("用户名或密码错误");
                        }
                    }
                });
    }

    private void getData(String mac){
        /**
         * The image size must be consistent with the label size
         * For the label size, please refer to the ESLTagType attribute in queryTagType()
         * String tagType= mac.substring(0,2);
         * ESLTagType.tagType==tagType
         *      ESLTagType.width
         *      ESLTagType.height
         */
        byte[] buffer =getAssetsFile(this,"test.png");
        repositoryManager.getBleImagePack(buffer,mac)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<ResponseBase<BleData>>() {
                    @Override
                    public void onNext(@NonNull ResponseBase<BleData> responseBase) {
                        if(responseBase.getCode()==0) {
                            binding.txtMessage.setText("打包成功，正在发送....");
                            BleTask task=new BleTask();
                            task.setMac(mac);
                            task.setPack(responseBase.getBody().getData());
                            send(task);
                        }else if(responseBase.getCode()==3001){
                            /**
                             * code = 3001
                             * The tag is not under the current account, so you need to add the tag to the current account;
                             * call addTag(mac)
                             *In the actual production environment, you should call the secondary method to add the label to the account when using the label for the first time.
                             */
                            Log.e("YUAN","打包失败：标签不存在");
                            //In the actual production environment, you should call the secondary method to add the label to the account when using the label for the first time.
                            addTag(mac);
                        }
                        else if(responseBase.getCode()==3002){
                            /**
                             * code = 3002
                             * The current account does not have the operation permission of this tag
                             */
                            Log.e("YUAN","打包失败：权限错误");
                            binding.txtMessage.setText("标签权限错误，发送失败");
                        }
                    }
                });

    }

    private void getLEDData(String mac){
        short time=20;
        LEDDataEntity dataEntity=new LEDDataEntity(mac,time,true,true,false);
        repositoryManager.ledData(dataEntity)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<ResponseBase<String[]>>() {
                    @Override
                    public void onNext(@NonNull ResponseBase<String[]> responseBase) {
                        if(responseBase.getCode()==0) {
                            binding.txtMessage.setText("打包成功，正在发送....");
                            BleTask task=new BleTask();
                            task.setMac(mac);
                            task.setPack(Arrays.asList(responseBase.getBody()));
                            send(task);
                        }else if(responseBase.getCode()==3001){
                            /**
                             * code = 3001
                             * The tag is not under the current account, so you need to add the tag to the current account;
                             * call addTag(mac)
                             *In the actual production environment, you should call the secondary method to add the label to the account when using the label for the first time.
                             */
                            Log.e("YUAN","打包失败：标签不存在");
                            //In the actual production environment, you should call the secondary method to add the label to the account when using the label for the first time.
                            addTag(mac);
                        }
                        else if(responseBase.getCode()==3002){
                            /**
                             * code = 3002
                             * The current account does not have the operation permission of this tag
                             */
                            Log.e("YUAN","打包失败：权限错误");
                            binding.txtMessage.setText("标签权限错误，发送失败");
                        }
                    }
                });

    }

    private void send(BleTask task){
        new SendWork( this,BleManager.getInstance())
                .setOnSendingListener(new SendWork.OnSendingListener() {
                    @Override
                    public void onSendSuccessfully(BleTask task, BleDeviceFeedback deviceFeedback) {
                        Log.i("YUAN","send successfully");
                        runOnUiThread(()->binding.txtMessage.setText("发送成功"));
                    }

                    @Override
                    public void onSendFail(BleTask task,String errorMessage) {
                        //Retry 3 times
                        if(task.getSendCount()<2){
                            retryTask(task);
                            return;
                        }
                        Log.e("YUAN","send fail");
                        runOnUiThread(()->binding.txtMessage.setText("发送失败"));
                    }
                })
                .send(task);
    }

    private void retryTask(BleTask task){
        HandlerHelper.postDelayed(()->send(task),2000);
    }

    private void addTag(String mac){
        repositoryManager.addTag(mac)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<ResponseBase<String>>() {
                    @Override
                    public void onNext(@NonNull ResponseBase<String> responseBase) {
                        if(responseBase.getCode()==0) {
                            Log.i("YUAN","Successfully added");

                        }else if(responseBase.getCode()==9994){
                            /**
                             * code = 9994
                             * Unable to add, the label is already under another account
                             */
                            Log.e("YUAN","无法添加，标签已在其他账户下");
                        }else if(responseBase.getCode()==9990){
                            Log.e("YUAN","add fail");
                        }
                    }
                });
    }

    @Override
    protected String[] getPermission() {
        return new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH};
    }


    public void initBle() {
        BleManager.getInstance().init(this);
        if (!BleManager.getInstance().isSupportBle()) {
            new AlertDialog.Builder(this)
                    .setMessage("当前设备不支持低功耗蓝牙")
                    .setTitle("消息")
                    .setPositiveButton("确定",(dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    }).show();
            return;
        }
        checkGPS();
    }
    private void checkGPS(){
        //判断位置服务是否开启
        if(!LocationUtils.getInstance().isLocServiceEnable(this)){
            new AlertDialog.Builder(this)
                    .setMessage("是否开启定位服务?")
                    .setNegativeButton("取消", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        new AlertDialog.Builder(this)
                                .setMessage("位置服务未开启,可能无法搜索到附近的蓝牙设备")
                                .setTitle("消息")
                                .setPositiveButton("确定",(dialog, i1) -> {
                                    dialog.dismiss();
                                    checkBle();
                                }).show();
                    })
                    .setPositiveButton("确认", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        openGPSLauncher.launch(intent);
                    }).show();
            return;
        }
        checkBle();

    }

    private void checkBle(){
        if (!BleManager.getInstance().isBlueEnable()) {
            new AlertDialog.Builder(this)
                    .setMessage("蓝牙没有开启,是否开启？")
                    .setNegativeButton("取消", (dialogInterface, i) ->{
                        dialogInterface.dismiss();
                    })
                    .setPositiveButton("开启", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        BleManager.getInstance().enableBluetooth();
                    }).show();
        }
    }

    public static byte[] getAssetsFile(Context context, String fileName){
        try {
            InputStream is = context.getAssets().open(fileName);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = is.read(buffer))) {
                output.write(buffer, 0, n);
            }
            is.close();
            return output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}