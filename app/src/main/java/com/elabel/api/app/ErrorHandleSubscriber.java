package com.elabel.api.app;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.elabel.api.MyApplication;
import com.elabel.api.R;
import com.elabel.api.mvp.model.entity.ResponseBase;
import com.elabel.api.mvp.model.service.TagServerErrorException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public abstract class ErrorHandleSubscriber <T> implements Observer<T> {
    private Context context;
    public ErrorHandleSubscriber(){
        this.context= MyApplication.getAppComponent().getMyApplication();
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }


    @Override
    public void onComplete() {

    }

    @Override
    public void onError(@NonNull Throwable e) {
        //e.printStackTrace();
        try {
            e.printStackTrace();
            if (e instanceof SocketTimeoutException) {
                showErrorMessage(getString(R.string.http_net_timeout_error));
            } else if (e instanceof ConnectException) {
                showErrorMessage(getString(R.string.http_net_error));
            } else if (e instanceof HttpException) {
                HttpException exception= (HttpException) e;
                if(exception.code()==500) {
                    showErrorMessage(getString(R.string.http_error_500));
                }else if(exception.code()==401){
                    //showErrorMessage(getString(R.string.login_status_expired));
                    onUnauthorized();
                }else if(exception.code()==404){
                    showErrorMessage(getString(R.string.server_not_supported));
                }
                else
                    showErrorMessage(e.getMessage());
            }else if(e instanceof TagServerErrorException){
                onServerError(((TagServerErrorException) e).getResponseBase());
            }
            else {
                showErrorMessage(e.getMessage());
            }
        } catch (Throwable el) {
            el.printStackTrace();
        }
    }

    protected void onUnauthorized(){

    }

    protected void onServerError(ResponseBase responseBase){

    }

    private void showErrorMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    private String getString(@StringRes int id){
        return context.getString(id);
    }
}
