package org.cocos2dx.lib;

import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* loaded from: classes.dex */
public class Cocos2dxRenderer implements GLSurfaceView.Renderer {
    private static final long NANOSECONDSPERMINISECOND = 1000000;
    private static final long NANOSECONDSPERSECOND = 1000000000;
    private static long animationInterval = 16666666;
    private long last;
    private int screenHeight;
    private int screenWidth;

    private static native void nativeDeleteBackward();

    private static native String nativeGetContentText();

    private static native void nativeInit(int i, int i2);

    private static native void nativeInsertText(String str);

    private static native boolean nativeKeyDown(int i);

    private static native void nativeOnPause();

    private static native void nativeOnResume();

    private static native void nativeRender();

    private static native void nativeTouchesBegin(int i, float f, float f2);

    private static native void nativeTouchesCancel(int[] iArr, float[] fArr, float[] fArr2);

    private static native void nativeTouchesEnd(int i, float f, float f2);

    private static native void nativeTouchesMove(int[] iArr, float[] fArr, float[] fArr2);

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        nativeInit(this.screenWidth, this.screenHeight);
        this.last = System.nanoTime();
    }

    public void setScreenWidthAndHeight(int w, int h) {
        this.screenWidth = w;
        this.screenHeight = h;
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 gl, int w, int h) {
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 gl) {
        long now = System.nanoTime();
        long interval = now - this.last;
        nativeRender();
        if (interval < animationInterval) {
            try {
                Thread.sleep(((animationInterval - interval) * 2) / NANOSECONDSPERMINISECOND);
            } catch (Exception e) {
            }
        }
        this.last = now;
    }

    public void handleActionDown(int id, float x, float y) {
        nativeTouchesBegin(id, x, y);
    }

    public void handleActionUp(int id, float x, float y) {
        nativeTouchesEnd(id, x, y);
    }

    public void handleActionCancel(int[] id, float[] x, float[] y) {
        nativeTouchesCancel(id, x, y);
    }

    public void handleActionMove(int[] id, float[] x, float[] y) {
        nativeTouchesMove(id, x, y);
    }

    public void handleKeyDown(int keyCode) {
        nativeKeyDown(keyCode);
    }

    public void handleOnPause() {
        nativeOnPause();
    }

    public void handleOnResume() {
        nativeOnResume();
    }

    public static void setAnimationInterval(double interval) {
        animationInterval = (long) (1.0E9d * interval);
    }

    public void handleInsertText(String text) {
        nativeInsertText(text);
    }

    public void handleDeleteBackward() {
        nativeDeleteBackward();
    }

    public String getContentText() {
        return nativeGetContentText();
    }
}
