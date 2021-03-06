package com.attrsc.braincs.audio;


import android.media.AudioRecord;
import android.util.Log;

import com.attrsc.braincs.audio.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.mars.audio.LogUtils;

/**
 * Created by HXL on 16/8/11.
 * 用于实现录音   暂停录音
 */
public class AudioRecorder {

    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;

    //录音对象
    private AudioRecord audioRecord;

    //录音状态
    private Status status = Status.STATUS_NO_READY;

    //文件名
    private String fileName;

    //录音文件
    private List<String> filesName = new ArrayList<>();

    //线程池
    private ExecutorService mExecutorService;

    //录音监听
    private OnRecordListener listener;
//    private AacEncode aacMediaEncode;


    public AudioRecorder() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    /**
     * 创建录音对象
     */
    public void createAudio(String fileName, int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        this.fileName = fileName;
        status = Status.STATUS_READY;
//        aacMediaEncode = new AacEncode(sampleRateInHz, 2);
    }

    /**
     * 创建默认的录音对象
     *
     * @param fileName 文件名
     */
    public void createDefaultAudio(String fileName) {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(Constants.AUDIO_SAMPLE_RATE,
                Constants.AUDIO_CHANNEL, Constants.AUDIO_ENCODING);
        audioRecord = new AudioRecord(
                Constants.AUDIO_INPUT,
                Constants.AUDIO_SAMPLE_RATE,
                Constants.AUDIO_CHANNEL,
                Constants.AUDIO_ENCODING,
                bufferSizeInBytes);

        this.fileName = fileName;
        status = Status.STATUS_READY;
//        aacMediaEncode = new AacEncode();
    }

    /**
     * 创建默认的录音对象
     *
     */
    public void initDefaultAudio() {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(Constants.AUDIO_SAMPLE_RATE,
                Constants.AUDIO_CHANNEL, Constants.AUDIO_ENCODING);
        audioRecord = new AudioRecord(
                Constants.AUDIO_INPUT,
                Constants.AUDIO_SAMPLE_RATE,
                Constants.AUDIO_CHANNEL,
                Constants.AUDIO_ENCODING,
                bufferSizeInBytes);

        status = Status.STATUS_READY;
//        aacMediaEncode = new AacEncode();
    }

    /**
     * 开始录音
     */
    public void startRecord() {

        if (status == Status.STATUS_NO_READY || audioRecord == null) {
            initDefaultAudio();
//            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限~");
        }
        if (status == Status.STATUS_START) {
//            throw new IllegalStateException("正在录音");
            LogUtils.DEBUG("正在录音");
        }
        Log.d("AudioRecorder", "===startRecord===" + audioRecord.getState());
        audioRecord.startRecording();

        String currentFileName = "tmp" + fileName;
        if (status == Status.STATUS_PAUSE) {
            //假如是暂停录音 将文件名后面加个数字,防止重名文件内容被覆盖
            currentFileName += filesName.size();

        }
        filesName.add(currentFileName);

        final String finalFileName = currentFileName;
        //将录音状态设置成正在录音状态
        status = Status.STATUS_START;

        //使用线程池管理线程
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
//                writeDataTOFile(finalFileName);
//                writeDataToAacFile(finalFileName);
                if (listener != null){
                    listener.onStart();
                }
                sendData2Listener();
            }
        });
    }

    /**
     * 暂停录音
     */
    public void pauseRecord() {
        Log.d("AudioRecorder", "===pauseRecord===");
        if (status != Status.STATUS_START) {
            throw new IllegalStateException("没有在录音");
        } else {
            audioRecord.stop();
            status = Status.STATUS_PAUSE;
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        Log.d("AudioRecorder", "===stopRecord===");
        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY) {
            throw new IllegalStateException("录音尚未开始");
        } else {
            audioRecord.stop();
            status = Status.STATUS_STOP;
            release();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        Log.d("AudioRecorder", "===release===");
        //假如有暂停录音
//        try {
//            if (filesName.size() > 0) {
//                List<String> filePaths = new ArrayList<>();
//                for (String fileName : filesName) {
//                    filePaths.add(FileUtils.getFileUnderAppFolder(fileName));
////                    filePaths.add(FileUtils.getFileUnderAppFolder(fileName));
//                }
        //清除
//                filesName.clear();
        //将多个pcm文件转化为wav文件
//                mergeAacFiles(filePaths);
//                mergePCMFilesToWAVFile(filePaths);
//                makePCMFileToWAVFile();

//            } else {
        //这里由于只要录音过filesName.size都会大于0,没录音时fileName为null
        //会报空指针 NullPointerException
        // 将单个pcm文件转化为wav文件
        //Log.d("AudioRecorder", "=====makePCMFileToWAVFile======");
        //makePCMFileToWAVFile();
//            }
//        } catch (IllegalStateException e) {
//            throw new IllegalStateException(e.getMessage());
//        }

        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }

//        if (aacMediaEncode != null){
//            aacMediaEncode.close();
//            aacMediaEncode = null;
//        }

        status = Status.STATUS_NO_READY;
    }

    /**
     * 取消录音
     */
    public void canel() {
        filesName.clear();
        fileName = null;
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }

        status = Status.STATUS_NO_READY;
    }


    private void sendData2Listener() {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        byte[] audiodata = new byte[bufferSizeInBytes];

        int readsize = 0;
        while (status == Status.STATUS_START) {

            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && listener != null) {
                listener.onRecord(audiodata);
            }
        }
    }

//    /**
//     * 将音频信息写入文件
//     */
//    private void writeDataTOFile(String currentFileName) {
//        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
//        byte[] audiodata = new byte[bufferSizeInBytes];
//
//        FileOutputStream fos = null;
//        int readsize = 0;
//        try {
//            File file = new File(FileUtils.getFileUnderAppFolder(currentFileName));
//            if (file.exists()) {
//                file.delete();
//            }
//            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
//        } catch (IllegalStateException e) {
//            Log.e("AudioRecorder", e.getMessage());
//            throw new IllegalStateException(e.getMessage());
//        } catch (FileNotFoundException e) {
//            Log.e("AudioRecorder", e.getMessage());
//
//        }
//        while (status == Status.STATUS_START) {
//            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
//            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
//                try {
//                    fos.write(audiodata);
//                    if (listener != null) {
//                        //用于拓展业务
//                        listener.onRecord(audiodata);
//                    }
//                } catch (IOException e) {
//                    Log.e("AudioRecorder", e.getMessage());
//                }
//            }
//        }
////        if (listener != null) {
////            listener.finishRecord();
////        }
//        try {
//            if (fos != null) {
//                fos.close();// 关闭写入流
//            }
//        } catch (IOException e) {
//            Log.e("AudioRecorder", e.getMessage());
//        }
//    }

//    /**
//     * 将音频信息写入文件
//     */
//    private void writeDataToAacFile(String currentFileName) {
//        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
//        byte[] audiodata = new byte[bufferSizeInBytes];
//
//        FileOutputStream fos = null;
//        int readsize = 0;
//        try {
//            File file = new File(FileUtils.getFileUnderAppFolder(currentFileName));
//            if (file.exists()) {
//                file.delete();
//            }
//            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
//        } catch (IllegalStateException e) {
//            Log.e("AudioRecorder", e.getMessage());
//            throw new IllegalStateException(e.getMessage());
//        } catch (FileNotFoundException e) {
//            Log.e("AudioRecorder", e.getMessage());
//
//        }
//
//        while (status == Status.STATUS_START) {
//            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
//            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
//                try {
//                    //转成AAC编码
//                    byte[] ret = aacMediaEncode.offerEncoder(audiodata);
//                    fos.write(ret);
//                    if (listener != null) {
//                        //用于拓展业务
//                        listener.onRecording(audiodata, 0, audiodata.length);
//                    }
//                } catch (IOException e) {
//                    Log.e("AudioRecorder", e.getMessage());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        if (listener != null) {
//            listener.finishRecord();
//        }
//        try {
//            if (fos != null) {
//                fos.close();// 关闭写入流
//            }
//        } catch (IOException e) {
//            Log.e("AudioRecorder", e.getMessage());
//        }
//    }

//    /**
//     * 将pcm合并成wav
//     *
//     * @param filePaths
//     */
//    private void mergePCMFilesToWAVFile(final List<String> filePaths) {
//        mExecutorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                if (PcmToWav.mergePCMFilesToWAVFile(filePaths, FileUtils.getFileUnderAppFolder(fileName))) {
//                    //操作成功
//                } else {
//                    //操作失败
//                    Log.e("AudioRecorder", "mergePCMFilesToWAVFile fail");
//                    throw new IllegalStateException("mergePCMFilesToWAVFile fail");
//                }
//            }
//        });
//    }

//    /**
//     * 合并多个aac文件
//     *
//     * @param filePaths
//     */
//    private void mergeAacFiles(final List<String> filePaths) {
//        mExecutorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                if (FileUtils.mergeFiles(filePaths, FileUtils.getFileUnderAppFolder(fileName))) {
//                    //操作成功
//                } else {
//                    //操作失败
//                    Log.e("AudioRecorder", "mergeAacFiles fail");
//                    throw new IllegalStateException("mergeAacFiles fail");
//                }
//            }
//        });
//    }

//    /**
//     * 将单个pcm文件转化为wav文件
//     */
//    private void makePCMFileToWAVFile() {
//        mExecutorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                if (PcmToWav.makePCMFileToWAVFile(FileUtils.getFileUnderAppFolder(fileName), FileUtils.getFileUnderAppFolder(fileName), false)) {
//                    //操作成功
//                } else {
//                    //操作失败
//                    Log.e("AudioRecorder", "makePCMFileToWAVFile fail");
//                    throw new IllegalStateException("makePCMFileToWAVFile fail");
//                }
//            }
//        });
//    }


    /**
     * 录音对象的状态
     */
    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP
    }

    /**
     * 获取录音对象的状态
     *
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * 获取本次录音文件的个数
     *
     * @return filesName.size()
     */
    public int getPcmFilesCount() {
        return filesName.size();
    }

    /**
     * 获取本次录音文件名集合
     *
     * @return filesName
     */
    public List<String> getPcmFilesName() {
        return filesName;
    }

    public OnRecordListener getListener() {
        return listener;
    }

    public void setListener(OnRecordListener listener) {
        this.listener = listener;
    }

    public interface OnRecordListener {
        void onRecord(byte[] datas);

        void onStart();
    }
}
