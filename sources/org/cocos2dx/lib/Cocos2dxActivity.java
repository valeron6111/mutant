package org.cocos2dx.lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import java.util.Locale;

/* loaded from: classes.dex */
public class Cocos2dxActivity extends Activity {
    private static final int HANDLER_SHOW_DIALOG = 1;
    private static Cocos2dxAccelerometer accelerometer;
    private static boolean accelerometerEnabled = false;
    private static Cocos2dxMusic backgroundMusicPlayer;
    private static Handler handler;
    public static Activity instance;
    private static String packageName;
    private static Cocos2dxSound soundPlayer;

    private static native void nativeSetContentPackPath(String str);

    private static native void nativeSetPaths(String str);

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        accelerometer = new Cocos2dxAccelerometer(this);
        backgroundMusicPlayer = new Cocos2dxMusic(this);
        soundPlayer = new Cocos2dxSound(this);
        Cocos2dxBitmap.setContext(this);
        handler = new Handler() { // from class: org.cocos2dx.lib.Cocos2dxActivity.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Cocos2dxActivity.this.showDialog(((DialogMessage) msg.obj).title, ((DialogMessage) msg.obj).message);
                        break;
                }
            }
        };
    }

    public static void setContentPackPath(String path) {
        nativeSetContentPackPath(path);
    }

    public static String getCurrentLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static void showMessageBox(String title, String message) {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = new DialogMessage(title, message);
        handler.sendMessage(msg);
    }

    public static void enableAccelerometer() {
        accelerometerEnabled = true;
        accelerometer.enable();
    }

    public static void disableAccelerometer() {
        accelerometerEnabled = false;
        accelerometer.disable();
    }

    public static void preloadBackgroundMusic(String path) {
        backgroundMusicPlayer.preloadBackgroundMusic(path);
    }

    public static void playBackgroundMusic(String path, boolean isLoop) {
        backgroundMusicPlayer.playBackgroundMusic(path, isLoop);
    }

    public static void stopBackgroundMusic() {
        backgroundMusicPlayer.stopBackgroundMusic();
    }

    public static void pauseBackgroundMusic() {
        backgroundMusicPlayer.pauseBackgroundMusic();
    }

    public static void resumeBackgroundMusic() {
        backgroundMusicPlayer.resumeBackgroundMusic();
    }

    public static void rewindBackgroundMusic() {
        backgroundMusicPlayer.rewindBackgroundMusic();
    }

    public static boolean isBackgroundMusicPlaying() {
        return backgroundMusicPlayer.isBackgroundMusicPlaying();
    }

    public static float getBackgroundMusicVolume() {
        return backgroundMusicPlayer.getBackgroundVolume();
    }

    public static void setBackgroundMusicVolume(float volume) {
        backgroundMusicPlayer.setBackgroundVolume(volume);
    }

    public static int playEffect(String path, boolean isLoop) {
        return soundPlayer.playEffect(path, isLoop);
    }

    public static void stopEffect(int soundId) {
        soundPlayer.stopEffect(soundId);
    }

    public static float getEffectsVolume() {
        return soundPlayer.getEffectsVolume();
    }

    public static void setEffectsVolume(float volume) {
        soundPlayer.setEffectsVolume(volume);
    }

    public static void preloadEffect(String path) {
        soundPlayer.preloadEffect(path);
    }

    public static void unloadEffect(String path) {
        soundPlayer.unloadEffect(path);
    }

    public static void end() {
        backgroundMusicPlayer.end();
        soundPlayer.end();
    }

    public static String getCocos2dxPackageName() {
        return packageName;
    }

    public static void terminateProcess() {
        Process.killProcess(Process.myPid());
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (accelerometerEnabled) {
            accelerometer.enable();
        }
        resumeBackgroundMusic();
        soundPlayer.resumeAllEffect();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        if (accelerometerEnabled) {
            accelerometer.disable();
        }
        pauseBackgroundMusic();
        soundPlayer.pauseAllEffect();
    }

    protected void setPackageName(String packageName2) {
        packageName = packageName2;
        PackageManager packMgmr = getApplication().getPackageManager();
        try {
            ApplicationInfo appInfo = packMgmr.getApplicationInfo(packageName2, 0);
            String apkFilePath = appInfo.sourceDir;
            Log.w("apk path", apkFilePath);
            nativeSetPaths(apkFilePath);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to locate assets, aborting...");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDialog(String title, String message) {
        Dialog dialog = new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: org.cocos2dx.lib.Cocos2dxActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int whichButton) {
            }
        }).create();
        dialog.show();
    }
}
