package com.attrsc.braincs.audio.utils;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * Created by Shuai
 * 07/04/2019.
 */

public class Constants {
    //音频输入-麦克风
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    public final static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    public final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    public final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public static final String APP_NAME = "SoundRecorder";
    public static final String CACHE_RECORD_AUDIO_NAME = "audio.aac";
}
