package com.elabel.api.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtils {
    private LocationManager locationManager;
    static LocationUtils locationUtils;
    public static LocationUtils getInstance(){
        if (locationUtils == null){
            locationUtils = new LocationUtils();
        }
        return locationUtils;
    }


    public Location getLocations(Context context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location location=null;
        try {
            //获取系统的服务，
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//            //创建一个criteria对象
//            Criteria criteria = new Criteria();
//            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//            //设置不需要获取海拔方向数据
//            criteria.setAltitudeRequired(false);
//            criteria.setBearingRequired(false);
//            //设置允许产生资费
//            criteria.setCostAllowed(true);
//            //要求低耗电
//            criteria.setPowerRequirement(Criteria.POWER_LOW);


            String locationProvider= LocationManager.NETWORK_PROVIDER;
            //String provider = locationManager.getBestProvider(criteria, true);
            Log.i("Tobin", "Location Provider is "+ locationProvider);
            location = locationManager.getLastKnownLocation(locationProvider);
//            if(location==null){
//                location=getLngAndLatWithNetwork(context);
//            }
            /**
             * 重要函数，监听数据测试
             * 位置提供器、监听位置变化的时间间隔（毫秒），监听位置变化的距离间隔（米），LocationListener监听器
             */
//           locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    lm.removeUpdates(locationListener);
//                }
//            },2000);


        }catch (SecurityException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return location;

    }

    private Location getLngAndLatWithNetwork(Context mContext) {
        //添加用户权限申请判断
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

        }
        public void onProviderDisabled(String provider){
            Log.i("Tobin", "Provider now is disabled..");
        }
        public void onProviderEnabled(String provider){
            Log.i("Tobin", "Provider now is enabled..");
        }
        public void onStatusChanged(String provider, int status, Bundle extras){ }
    };


    /**
     * @param latitude 经度
     * @param longitude 纬度
     * @return 详细位置信息 GeoCoder是基于后台backend的服务，因此这个方法不是对每台设备都适用。
     */
    public String convertAddress(Context context, double latitude, double longitude) {
        Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
        StringBuilder mStringBuilder = new StringBuilder();

        try {
            List<Address> mAddresses = mGeocoder.getFromLocation(latitude, longitude, 1);
            if (!mAddresses.isEmpty()) {
                Address address = mAddresses.get(0);
                mStringBuilder.append(address.getCountryName()).append(",").append(address.getAdminArea()).append(",").append(address.getLocality())
                        .append(",").append(address.getSubLocality()).append(",").append(address.getThoroughfare());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mStringBuilder.toString();
    }

    /**
     * 手机是否开启位置服务
     * @param mContext
     * @return
     */
    public boolean isLocServiceEnable(Context mContext) {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }
}
