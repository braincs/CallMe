package com.attrsc.braincs.audio.utils;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.attrsc.braincs.audio.AudioRecorder;
import com.attrsc.braincs.audio.AudioTrackManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import dev.mars.audio.AudioFrame;
import dev.mars.audio.Common;
import dev.mars.audio.LogUtils;

/**
 * Created by Shuai
 * 08/04/2019.
 */

public class AudioDelegate {
    private final static String TAG = AudioDelegate.class.getSimpleName();
    private AtomicBoolean isRecording = new AtomicBoolean(false);
    private AtomicBoolean isPlaying = new AtomicBoolean(false);
    private AtomicBoolean isRecordAndPlay = new AtomicBoolean(false);

    private AudioFrame echoAudioFrame = new AudioFrame();
    private static ReentrantLock reentrantLock = new ReentrantLock(true);
    private AudioTrackManager audioPlayer;
    private AudioManager mAudioManager;
    private AudioRecorder recorder;

    public AudioDelegate(){

        initPlayer();
        initRecorder();
    }
    public void setEchoAudioFrame(byte[] bytes){
        reentrantLock.lock();
        echoAudioFrame.data = bytes;
        reentrantLock.unlock();
    }

    public byte[] getEchoAudioFrame(){
        reentrantLock.lock();
        byte[] bytes = echoAudioFrame.data;
        reentrantLock.unlock();
        return bytes;
    }


    public void setIsRecording(boolean v) {
        isRecording.set(v);
        LogUtils.DEBUG("setIsRecording " + v);
    }

    public void setIsRecordingAndPlaying(boolean v) {
        isRecordAndPlay.set(v);
        LogUtils.DEBUG("setIsRecordingAndPlaying " + v);
    }

    public boolean isRecording() {
        return isRecording.get();
    }

    public void setIsPlaying(boolean b) {
        isPlaying.set(b);
        LogUtils.DEBUG("setIsPlaying " + b);
    }

    public boolean isPlaying() {
        return isPlaying.get();
    }

    public boolean isRecordingAndPlaying() {
        return isRecordAndPlay.get();
    }


    private void initPlayer() {
        audioPlayer = new AudioTrackManager();
    }

    private void initRecorder() {

        recorder = new AudioRecorder();

        recorder.createDefaultAudio(Constants.CACHE_RECORD_AUDIO_NAME);
    }

    private void initRecorder(Context context) {
        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(!mAudioManager.isBluetoothScoAvailableOffCall()){
            Log.d(TAG, "系统不支持蓝牙录音");
            return;
        }

        recorder = new AudioRecorder();

        recorder.createDefaultAudio(Constants.CACHE_RECORD_AUDIO_NAME);
    }

    public void stopRecording(){
        recorder.stopRecord();
        setIsRecording(false);
    }
    public void stopPlaying(){
        audioPlayer.stopPlay();
        setIsPlaying(false);
    }

    public int encode(String pcm, String speex){
        return -1;
    }

    public  int decode(String speex, String pcm){
        return -1;
    }

    public int recordAndPlayPCM(boolean enableProcess, boolean enableEchoCancel){

        return -1;
    }

    public int stopRecordingAndPlaying(){

        return -1;
    }

    BlockingQueue<AudioFrame> audioFrames;

    public void startPlay2(BlockingQueue<AudioFrame> audioFrames) {
        setIsPlaying(true);
        this.audioFrames = audioFrames;
        playRecording2(Common.SAMPLERATE, Common.PERIOD_TIME, Common.CHANNELS);
    }

    public void startRecording2(int sampleRate, int period, int channels){
        setIsRecording(true);
        recorder.startRecord();
    }

    public void playRecording2(int sampleRate, int period, int channels){
        audioPlayer.startPlay(audioFrames);
    }

    public byte[] getOneFrame() {

        //AudioFrame audioFrame = audioFrames.take();
        AudioFrame audioFrame = audioFrames.poll(); //非阻塞
        if (audioFrame == null) {
            return null;
        }
        LogUtils.DEBUG("JNI 从 JAVA层取走byte[]数据");
        return audioFrame.data;
    }

//    public void onRecord(byte[] bytes) {
//        LogUtils.DEBUG("收到native传来的bytes，长度 = " + (bytes == null ? 0 : bytes.length));
//        if (onRecordListener != null) {
//            onRecordListener.onRecord(bytes);
//        }
//    }
//
//    public void onRecordStart() {
//        if (onRecordListener != null)
//            onRecordListener.onStart();
//    }

    AudioRecorder.OnRecordListener onRecordListener;

    public void setOnRecordListener(AudioRecorder.OnRecordListener l) {
        onRecordListener = l;
        if (recorder!=null) {
            recorder.setListener(onRecordListener);
        }

    }



    public void setNoiseClear(boolean enable){

    }

    public void setEchoClear(boolean enable){

    }

//    public void setBlueOn(boolean enable){
//        if (enable){
//            enableBluetooth();
//        }else {
//            disableBluetooth();
//        }
//    }
//    public void enableBluetooth(){
//        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
//        mAudioManager.setBluetoothScoOn(true);  //打开SCO
//        mAudioManager.startBluetoothSco();
//    }
//
//    public void disableBluetooth(){
//        if(mAudioManager.isBluetoothScoOn()){
//            mAudioManager.setBluetoothScoOn(false);
//            mAudioManager.stopBluetoothSco();
//        }
//    }
}
