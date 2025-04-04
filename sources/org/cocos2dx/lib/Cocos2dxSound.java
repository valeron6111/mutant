package org.cocos2dx.lib;

import android.content.Context;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import com.alawar.mutant.jni.MutantMessages;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class Cocos2dxSound {
    private static final int MAX_SIMULTANEOUS_STREAMS_DEFAULT = 5;
    private static final int SOUND_PRIORITY = 1;
    private static final int SOUND_QUALITY = 5;
    private static final float SOUND_RATE = 1.0f;
    private static final String TAG = "Cocos2dxSound";
    private final int INVALID_SOUND_ID = -1;
    private final int INVALID_STREAM_ID = -1;
    private Context mContext;
    private float mLeftVolume;
    private HashMap<String, Integer> mPathSoundIDMap;
    private HashMap<Integer, Integer> mRepeatSoundIdStreamIdMap;
    private float mRightVolume;
    private HashMap<Integer, Integer> mSoundIdStreamIdMap;
    private SoundPool mSoundPool;

    public Cocos2dxSound(Context context) {
        this.mContext = context;
        int streamCount = isSGDeviceModel(Build.DEVICE) ? 1 : 5;
        initData(streamCount);
    }

    static boolean isSGDeviceModel(String device) {
        Pattern pattern = Pattern.compile("^(\\w+)\\-(\\w)(\\d+)(\\w)?$", 2);
        Matcher matcher = pattern.matcher(device);
        if (!matcher.find()) {
            Log.i(TAG, String.format("! %s: false", device));
            return false;
        }
        int pos = 0 + 1;
        String model = matcher.group(pos);
        int pos2 = pos + 1;
        String prefix = matcher.group(pos2);
        int pos3 = pos2 + 1;
        int index = Integer.valueOf(matcher.group(pos3)).intValue();
        String suffix = matcher.group(pos3 + 1);
        if (suffix == null) {
            suffix = MutantMessages.sEmpty;
        }
        boolean result = false;
        if (model.equals("SGH") && "IT".contains(prefix) && index >= 727) {
            result = true;
        } else if (model.equals("GT") && prefix.equals("I") && index >= 9000) {
            result = true;
        }
        Log.i(TAG, String.format("! %s-%s%d%s: %s", model, prefix, Integer.valueOf(index), suffix, Boolean.toString(result)));
        return result;
    }

    public int preloadEffect(String path) {
        if (this.mPathSoundIDMap.get(path) != null) {
            return this.mPathSoundIDMap.get(path).intValue();
        }
        int soundId = createSoundIdFromAsset(path);
        if (soundId != -1) {
            this.mSoundIdStreamIdMap.put(Integer.valueOf(soundId), -1);
            this.mPathSoundIDMap.put(path, Integer.valueOf(soundId));
            return soundId;
        }
        return soundId;
    }

    public void unloadEffect(String path) {
        Integer soundId = this.mPathSoundIDMap.remove(path);
        if (soundId != null) {
            this.mSoundPool.unload(soundId.intValue());
            this.mSoundIdStreamIdMap.remove(soundId);
        }
    }

    public int playEffect(String path, boolean isLoop) {
        Integer soundId = this.mPathSoundIDMap.get(path);
        if (soundId != null) {
            this.mSoundPool.stop(soundId.intValue());
            int streamId = this.mSoundPool.play(soundId.intValue(), this.mLeftVolume, this.mRightVolume, 1, isLoop ? -1 : 0, SOUND_RATE);
            this.mSoundIdStreamIdMap.put(soundId, Integer.valueOf(streamId));
            if (isLoop) {
                this.mRepeatSoundIdStreamIdMap.put(soundId, Integer.valueOf(streamId));
            }
        } else {
            soundId = Integer.valueOf(preloadEffect(path));
            if (soundId.intValue() == -1) {
                return -1;
            }
            playEffect(path, isLoop);
        }
        return soundId.intValue();
    }

    public void stopEffect(int soundId) {
        Integer streamId = this.mSoundIdStreamIdMap.get(Integer.valueOf(soundId));
        if (streamId != null && streamId.intValue() != -1) {
            this.mSoundPool.stop(streamId.intValue());
            this.mPathSoundIDMap.remove(Integer.valueOf(soundId));
            this.mRepeatSoundIdStreamIdMap.remove(Integer.valueOf(soundId));
        }
    }

    public void pauseAllEffect() {
        pauseOrResumeAllEffect(true);
    }

    public void resumeAllEffect() {
        pauseOrResumeAllEffect(false);
    }

    public float getEffectsVolume() {
        return (this.mLeftVolume + this.mRightVolume) / 2.0f;
    }

    public void setEffectsVolume(float volume) {
        this.mRightVolume = volume;
        this.mLeftVolume = volume;
    }

    public void end() {
        this.mSoundPool.release();
        this.mPathSoundIDMap.clear();
        this.mSoundIdStreamIdMap.clear();
        this.mRepeatSoundIdStreamIdMap.clear();
        initData(5);
    }

    public int createSoundIdFromAsset(String path) {
        try {
            int soundId = this.mSoundPool.load(this.mContext.getAssets().openFd(path), 0);
            return soundId;
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
            return -1;
        }
    }

    private void initData(int streamCount) {
        this.mSoundIdStreamIdMap = new HashMap<>();
        this.mRepeatSoundIdStreamIdMap = new HashMap<>();
        this.mSoundPool = new SoundPool(streamCount, 3, 5);
        this.mPathSoundIDMap = new HashMap<>();
        this.mLeftVolume = 0.5f;
        this.mRightVolume = 0.5f;
    }

    private void pauseOrResumeAllEffect(boolean isPause) {
        for (Map.Entry<Integer, Integer> entry : this.mRepeatSoundIdStreamIdMap.entrySet()) {
            int streamId = entry.getValue().intValue();
            if (isPause) {
                this.mSoundPool.pause(streamId);
            } else {
                this.mSoundPool.resume(streamId);
            }
        }
    }
}
