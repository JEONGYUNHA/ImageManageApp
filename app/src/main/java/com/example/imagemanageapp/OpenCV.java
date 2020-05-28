package com.example.imagemanageapp;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

class RGB {
    public int r;
    public int g;
    public int b;

    public RGB(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + r +"," + g + "," + b + ")";
    }
}

public class OpenCV extends AppCompatActivity {
    private static final String TAG = "opencv";
    private AppCompatActivity act;


    public OpenCV(AppCompatActivity activity) {
        this.act = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    static {
        /*System.loadLibrary("c++_shared");*/
        System.loadLibrary("opencv_java4");
        //System.loadLibrary("native-lib");
    }

   private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(act) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, act, mLoaderCallback);
        } else {
            Log.d(TAG, "onResume :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public Double isShaken(String path) {
        try {
            Mat image, gray = new Mat();
            Mat dst = new Mat();
            int ddepth = CvType.CV_64F;

            //String path = "/storage/emulated/0/DCIM/Camera/noshake.jpg";
            image = Imgcodecs.imread(path);
            double width = (image.width() * 0.5);
            double height = (image.height() * 0.5);
            Imgproc.resize(image, image, new Size(width, height));
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

    public List<RGB> color(String path) {
        Mat image = new Mat();
        image = Imgcodecs.imread(path);
        double width = (image.width() * 0.5);
        double height = (image.height() * 0.5);
        Imgproc.resize(image, image, new Size(width, height));
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB);
        List<RGB> rgb = cluster(image, 3);

        return rgb;
    }

    public static List<RGB> cluster(Mat cutout, int k) {
        Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());
        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);

        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        centers.reshape(3);

        List<RGB> rgbList = new ArrayList<RGB>();
        boolean first = false;
        boolean second = false;
        boolean third = false;

        int rows = 0;

        for (int y = 0; y < cutout.rows(); y++) {
            for (int x = 0; x < cutout.cols(); x++) {
                int label = (int) labels.get(rows, 0)[0];
                if (label == 0) {
                    if (!first) {
                        int b = (int) centers.get(label, 2)[0];
                        int g = (int) centers.get(label, 1)[0];
                        int r = (int) centers.get(label, 0)[0];
                        first = true;
                        rgbList.add(new RGB(r, g, b));
                    }
                }
                if (label == 1) {
                    if (!second) {
                        int b = (int) centers.get(label, 2)[0];
                        int g = (int) centers.get(label, 1)[0];
                        int r = (int) centers.get(label, 0)[0];
                        second = true;
                        rgbList.add(new RGB(r, g, b));
                    }
                }
                if (label == 2) {
                    if (!third) {
                        int b = (int) centers.get(label, 2)[0];
                        int g = (int) centers.get(label, 1)[0];
                        int r = (int) centers.get(label, 0)[0];
                        third = true;
                        rgbList.add(new RGB(r, g, b));
                    }
                }
                if (first && second && third)
                    break;
                rows++;
            }
        }

        return rgbList;
    }

}