package com.walmartreact.ReactOrientationListener;

import javax.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.LifecycleEventListener;

import android.content.BroadcastReceiver;
import android.content.res.Configuration;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;
import android.os.Build;
import android.util.Log;
import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

public class ReactOrientationListenerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

  ReactApplicationContext reactContext;

  final BroadcastReceiver receiver;

  public ReactOrientationListenerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    final ReactApplicationContext thisContext = reactContext;

    receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Configuration newConfig = intent.getParcelableExtra("newConfig");
                    Log.d("receiver", String.valueOf(newConfig.orientation));

                    String orientationValue = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ? "PORTRAIT" : "LANDSCAPE";

                    WritableNativeMap params = new WritableNativeMap();

                    params.putString("orientation", orientationValue);
                    params.putString("device", getDeviceName());
                    if (thisContext.hasActiveCatalystInstance()) {
                        thisContext
                          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                          .emit("orientationDidChange", params);
                    }
                }
            };
    thisContext.addLifecycleEventListener(this);
  }

  public String getDeviceName() {
    String manufacturer = Build.MANUFACTURER;
    String model = Build.MODEL;
    if (model.startsWith(manufacturer)) {
      return capitalize(model);
    } else {
      return capitalize(manufacturer) + " " + model;
    }
  }

  private String capitalize(String s) {
    if (s == null || s.length() == 0) {
      return "";
    }
    char first = s.charAt(0);
    if (Character.isUpperCase(first)) {
      return s;
    } else {
      return Character.toUpperCase(first) + s.substring(1);
    }
  }

  @Override
  public String getName() {
    return "OrientationListener";
  }

  @Override
  public @Nullable Map<String, Object> getConstants() {
    HashMap<String, Object> constants = new HashMap<String, Object>();
    PackageManager packageManager = this.reactContext.getPackageManager();
    String packageName = this.reactContext.getPackageName();
    return constants;
  }

  @ReactMethod
  public void getOrientation(Callback success) {
    WritableNativeMap data = new WritableNativeMap();
    DisplayMetrics metrics = this.reactContext.getResources().getDisplayMetrics();
    String orientation = "";
    if(metrics.widthPixels < metrics.heightPixels){
      orientation = "PORTRAIT";
    }else {
      orientation = "LANDSCAPE";
    }
    data.putString("orientation", orientation);
    data.putString("device", getDeviceName());
    success.invoke(data);
  }

  @Override
  public void onHostResume() {
      final Activity activity = getCurrentActivity();

      assert activity != null;
      activity.registerReceiver(receiver, new IntentFilter("onConfigurationChanged"));
  }
  @Override
  public void onHostPause() {
      final Activity activity = getCurrentActivity();
      if (activity == null) return;
      try
      {
          activity.unregisterReceiver(receiver);
      }
      catch (java.lang.IllegalArgumentException e) {
          Log.e(ReactConstants.TAG, "receiver already unregistered", e);
      }
  }

  @Override
  public void onHostDestroy() {
      final Activity activity = getCurrentActivity();
      if (activity == null) return;
      try
      {
          activity.unregisterReceiver(receiver);
      }
      catch (java.lang.IllegalArgumentException e) {
          Log.e(ReactConstants.TAG, "receiver already unregistered", e);
      }
  }

}
