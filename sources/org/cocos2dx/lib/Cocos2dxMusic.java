package org.cocos2dx.lib;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

/* loaded from: classes.dex */
public class Cocos2dxMusic {
    private static final String TAG = "Cocos2dxMusic";
    private MediaPlayer mBackgroundMediaPlayer;
    private Context mContext;
    private String mCurrentPath;
    private boolean mIsPaused;
    private float mLeftVolume;
    private float mRightVolume;

    public Cocos2dxMusic(Context context) {
        this.mContext = context;
        initData();
    }

    public void preloadBackgroundMusic(String path) {
        if (this.mCurrentPath == null || !this.mCurrentPath.equals(path)) {
            if (this.mBackgroundMediaPlayer != null) {
                this.mBackgroundMediaPlayer.release();
            }
            this.mBackgroundMediaPlayer = createMediaPlayerFromAssets(path);
            this.mCurrentPath = path;
        }
    }

    public void playBackgroundMusic(String path, boolean isLoop) {
        if (this.mCurrentPath == null) {
            this.mBackgroundMediaPlayer = createMediaPlayerFromAssets(path);
            this.mCurrentPath = path;
        } else if (!this.mCurrentPath.equals(path)) {
            if (this.mBackgroundMediaPlayer != null) {
                this.mBackgroundMediaPlayer.release();
            }
            this.mBackgroundMediaPlayer = createMediaPlayerFromAssets(path);
            this.mCurrentPath = path;
        }
        if (this.mBackgroundMediaPlayer == null) {
            Log.e(TAG, "playBackgroundMusic: background media player is null");
            return;
        }
        this.mBackgroundMediaPlayer.stop();
        this.mBackgroundMediaPlayer.setLooping(isLoop);
        try {
            this.mBackgroundMediaPlayer.prepare();
            this.mBackgroundMediaPlayer.seekTo(0);
            this.mBackgroundMediaPlayer.start();
            this.mIsPaused = false;
        } catch (Exception e) {
            Log.e(TAG, "playBackgroundMusic: error state");
        }
    }

    public void stopBackgroundMusic() {
        if (this.mBackgroundMediaPlayer != null) {
            this.mBackgroundMediaPlayer.stop();
            this.mIsPaused = false;
        }
    }

    public void pauseBackgroundMusic() {
        if (this.mBackgroundMediaPlayer != null && this.mBackgroundMediaPlayer.isPlaying()) {
            this.mBackgroundMediaPlayer.pause();
            this.mIsPaused = true;
        }
    }

    public void resumeBackgroundMusic() {
        if (this.mBackgroundMediaPlayer != null && this.mIsPaused) {
            this.mBackgroundMediaPlayer.start();
            this.mIsPaused = false;
        }
    }

    public void rewindBackgroundMusic() {
        if (this.mBackgroundMediaPlayer != null) {
            this.mBackgroundMediaPlayer.stop();
            try {
                this.mBackgroundMediaPlayer.prepare();
                this.mBackgroundMediaPlayer.seekTo(0);
                this.mBackgroundMediaPlayer.start();
                this.mIsPaused = false;
            } catch (Exception e) {
                Log.e(TAG, "rewindBackgroundMusic: error state");
            }
        }
    }

    public boolean isBackgroundMusicPlaying() {
        return this.mBackgroundMediaPlayer != null && this.mBackgroundMediaPlayer.isPlaying();
    }

    public void end() {
        if (this.mBackgroundMediaPlayer != null) {
            this.mBackgroundMediaPlayer.release();
        }
        initData();
    }

    public float getBackgroundVolume() {
        if (this.mBackgroundMediaPlayer != null) {
            return (this.mLeftVolume + this.mRightVolume) / 2.0f;
        }
        return 0.0f;
    }

    public void setBackgroundVolume(float volume) {
        this.mRightVolume = volume;
        this.mLeftVolume = volume;
        if (this.mBackgroundMediaPlayer != null) {
            this.mBackgroundMediaPlayer.setVolume(this.mLeftVolume, this.mRightVolume);
        }
    }

    private void initData() {
        this.mLeftVolume = 0.5f;
        this.mRightVolume = 0.5f;
        this.mBackgroundMediaPlayer = null;
        this.mIsPaused = false;
        this.mCurrentPath = null;
    }

    private MediaPlayer createMediaPlayerFromAssets(String path) {
        try {
            AssetFileDescriptor assetFileDescriptor = this.mContext.getAssets().openFd(path);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.setVolume(this.mLeftVolume, this.mRightVolume);
            return mediaPlayer;
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
            return null;
        }
    }
}
