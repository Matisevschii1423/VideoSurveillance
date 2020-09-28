package com.matis.objdetector.Model.VideoChannel;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MotionDetector implements Serializable, Runnable {

    private Logger logger = LoggerFactory.getLogger(MotionDetector.class);

    /*** detector parameters */
    public AtomicBoolean enableDetector;
    public AtomicInteger threshold;
    public AtomicInteger streamSelector;
    public String motionZone;
    public String parrentId;
    public transient AtomicBoolean refreshDetector;

    /*** detector state*/
    public transient AtomicBoolean running;

    /*** processing objects */
    private transient Mat firstImage;
    private transient Mat lastImage;
    private transient Mat diffImage;
    private transient Mat matZone;
    private transient Mat hierarchy;
    private transient Mat image;
    private transient Size blurKsize;
    private transient List<MatOfPoint> contours;
    private List<VideoStream> videoStreamList;

    public MotionDetector(List<VideoStream> videoStreamList) {
        this.enableDetector = new AtomicBoolean(false);
        this.threshold = new AtomicInteger(5);
        this.streamSelector = new AtomicInteger(2);
        this.refreshDetector = new AtomicBoolean(false);
        this.running = new AtomicBoolean(false);
        this.motionZone = this.createDefaultMdZone();
        this.videoStreamList = videoStreamList;
        this.matZone = createMaskMat(this.motionZone);
    }

    private Mat createMaskMat(String mask) {
        String[] rows = new String[32];
        rows = mask.split(";");
        Mat matMask = new Mat(32, 32, CvType.CV_8UC1);
        for (int row = 0; row < rows.length; row++) {
            String[] cols = rows[row].split(",");
            for (int col = 0; col < cols.length; col++) {
                matMask.put(row, col, Double.parseDouble(cols[col]));
            }
        }
        return matMask;
    }

    private Size calcBlurKernelSize(int width, int height) {
        /**OPENCV-->:ksize.width > 0 && ksize.width % 2 == 1 && ksize.height > 0 && ksize.height % 2 == 1*/
        float kSize;

        if (width > height) {
            if (width > 100) {
                kSize = width / 100;
                if (kSize % 2 == 0) {
                    kSize += 1;
                }
            } else {
                kSize = 1;
            }
        } else {
            if (height > 100) {
                kSize = height / 100;
                if (kSize % 2 == 0) {
                    kSize += 1;
                }
            } else {
                kSize = 1;
            }
        }
        logger.info("size-->" + width + "--" + height);
        logger.info("kernel size-->" + kSize);
        return new Size(kSize, kSize);
    }

    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    private void findMotion(BufferedImage image) {
        try {
            if (this.firstImage == null) {
                this.firstImage = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC1);
                Imgproc.cvtColor(bufferedImageToMat(image), this.firstImage, Imgproc.COLOR_BGR2GRAY);
                Imgproc.GaussianBlur(this.firstImage, this.firstImage, blurKsize, 0);
            } else {
                this.lastImage = firstImage;
                this.firstImage = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC1);
                Imgproc.cvtColor(bufferedImageToMat(image), this.firstImage, Imgproc.COLOR_BGR2GRAY);
                Imgproc.GaussianBlur(this.firstImage, this.firstImage, blurKsize, 0);
            }

            if (this.diffImage == null) {
                this.diffImage = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC1);
            }
            if (this.lastImage != null) {
                contours = new ArrayList<>();
                hierarchy = new Mat();
                this.image = Mat.zeros(32, 32, CvType.CV_8UC3);

                Core.absdiff(this.firstImage, this.lastImage, this.diffImage);
                Imgproc.threshold(this.diffImage, this.diffImage, this.threshold.get(), 255, Imgproc.THRESH_BINARY);
                Imgproc.resize(this.diffImage, this.diffImage, new Size(32, 32));
                Core.multiply(this.diffImage, this.matZone, this.diffImage);

                Imgproc.findContours(this.diffImage, this.contours, this.hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
                if (contours.size() > 0) {

                }
                //canvasFrame.showImage(converter.convert(diffImage));
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.blurKsize = null;
        }
    }

    public static String createDefaultMdZone() {
        String maskZOne = "0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1;" +
                "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0;" +
                "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0;";
        return maskZOne;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(Thread.currentThread().getName()+"MD");
        BufferedImage image = null;
        this.running.set(true);
        this.refreshDetector.set(false);
        logger.info("MD started");
        while (this.refreshDetector.get() == false && !Thread.currentThread().isInterrupted()) {
            try {

                image = this.videoStreamList.get(this.streamSelector.get()).bufferedImagesQueue1.poll(1l, TimeUnit.SECONDS);

                if (image != null) {
                    if (((DataBufferByte) image.getRaster().getDataBuffer()).getData() != null) {
                        if (this.blurKsize == null) {
                            this.blurKsize = this.calcBlurKernelSize(image.getWidth(), image.getHeight());
                        } else {
                            this.findMotion(image);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        logger.info("MD stoped");
        this.running.set(false);
    }


}
