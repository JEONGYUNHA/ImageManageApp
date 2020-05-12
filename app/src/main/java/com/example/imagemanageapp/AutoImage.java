package com.example.imagemanageapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AutoImage {
    private AppCompatActivity act;

    private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
    private static final boolean TF_OD_API_IS_QUANTIZED = true;
    private static final String TF_OD_API_LABELS_FILE = "labelmap.txt";
    private static final int TF_OD_API_INPUT_SIZE = 300;

    private Classifier detector;

    private Executor executor = Executors.newSingleThreadExecutor();


    public AutoImage(AppCompatActivity activity) {
        this.act = activity;
    }

    public String[] checkAutoImg(String path) {
        initTensorFlowAndLoadModel();

        Bitmap img = BitmapFactory.decodeFile(path);
        img = Bitmap.createScaledBitmap(img, TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, false);

        final List<Classifier.Recognition> results = detector.recognizeImage(img);

        String[] a = new String[2];
        a[0] = results.get(0).getTitle();
        a[1] = results.get(1).getTitle();

        return a;

    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    detector =
                            TFLiteObjectDetectionAPIModel.create(
                                    act.getAssets(),
                                    TF_OD_API_MODEL_FILE,
                                    TF_OD_API_LABELS_FILE,
                                    TF_OD_API_INPUT_SIZE,
                                    TF_OD_API_IS_QUANTIZED);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }
}
