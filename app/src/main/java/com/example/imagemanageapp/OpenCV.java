package com.example.imagemanageapp;


import androidx.appcompat.app.AppCompatActivity;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class OpenCV {
    private static final String TAG = "opencv";
    private AppCompatActivity act;

    public OpenCV(AppCompatActivity activity) {
        this.act = activity;
    }

    static {
        System.loadLibrary("opencv_java4");
        //System.loadLibrary("native-lib");
    }

/*
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(act) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    isShaken("");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public void checkShaken() {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, act, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }*/

    public Double isShaken(String path) {
        try {
            Mat image, gray = new Mat();
            Mat dst = new Mat();
            int ddepth = CvType.CV_64F;

            //String path = "/storage/emulated/0/DCIM/Camera/noshake.jpg";
            image = Imgcodecs.imread(path);

            Imgproc.resize(image, image, new Size(300, 300));
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

            Imgproc.Laplacian(gray, dst, ddepth, 3, 1, 0);
            //Imgproc.Laplacian(gray, dst, ddepth);

            // labplacian 값 구하는 코드
            MatOfDouble median = new MatOfDouble();
            MatOfDouble std = new MatOfDouble();
            Core.meanStdDev(dst, median, std);
            double fm = Math.pow(std.get(0, 0)[0], 2);

            return fm;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}