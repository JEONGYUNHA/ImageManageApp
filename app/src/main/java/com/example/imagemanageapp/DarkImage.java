package com.example.imagemanageapp;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DarkImage {

    private AppCompatActivity act;

    public DarkImage(AppCompatActivity activity) {
        this.act = activity;
    }

    public String checkDarkImg(String path) {
        try {

            Bitmap img = BitmapFactory.decodeFile(path);

            int cx = 28, cy = 28;
            img = Bitmap.createScaledBitmap(img, cx, cy, false);

            float[][] bytes_img = new float[1][784];

            for (int y = 0; y < 28; y++) {
                for (int x = 0; x < 28; x++) {
                    int pixel = img.getPixel(x, y);
                    bytes_img[0][y * 28 + x] = (pixel & 0xff) / (float) 255;
                }
            }

            Interpreter tf_lite = getTfliteInterpreter("converted_model.tflite");
            Log.d("getTfliteInterpreter", Integer.toString(0));

            float[][] output = new float[1][10];
            tf_lite.run(bytes_img, output);

            float[] predictArray = new float[10];
            int zeroNum = 0;

            for (int i = 0; i < 10; i++) {
                predictArray[i] = output[0][i];
                if (String.valueOf(predictArray[i]).substring(0, 1).equals("0")) {
                    zeroNum++;
                }
            }
            Log.d("0 number : ", String.valueOf(zeroNum));

            if (zeroNum >= 5) {
                return "dark";
            } else {
                return "bright";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }


    // 모델 파일 인터프리터를 생성하는 공통 함수
    // loadModelFile 함수에 예외가 포함되어 있기 때문에 반드시 try, catch 블록이 필요하다.
    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(act, modelPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 모델을 읽어오는 함수로, 텐서플로 라이트 홈페이지에 있다.
    // MappedByteBuffer 바이트 버퍼를 Interpreter 객체에 전달하면 모델 해석을 할 수 있다.
    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}