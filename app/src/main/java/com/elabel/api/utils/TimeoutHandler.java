package com.elabel.api.utils;

public class TimeoutHandler implements Runnable{
    private int timeout = 1000*20;
    private boolean cancel;
    private OnTimeoutListener onTimeoutListener;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void cancel() {
        this.cancel = true;
    }

    public void start(){
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(cancel)
            return;
        if(onTimeoutListener!=null)
            onTimeoutListener.onTimeout();
    }

    public void setOnTimeoutListener(OnTimeoutListener onTimeoutListener) {
        this.onTimeoutListener = onTimeoutListener;
    }

    public interface OnTimeoutListener{
        void onTimeout();
    }
}
