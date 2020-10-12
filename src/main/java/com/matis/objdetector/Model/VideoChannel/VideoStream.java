package com.matis.objdetector.Model.VideoChannel;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class VideoStream implements Externalizable, Runnable {

    Logger logger = LoggerFactory.getLogger(VideoStream.class);

    /*** configuration parameters */
    public AtomicBoolean enableStream;
    public volatile String inputUrl;
    public volatile String outputUrl;
    public AtomicLong grabberFpsQueue1;
    public AtomicLong grabberFpsQueue2;
    public AtomicBoolean refrashStream;
    public volatile Integer id;
    public volatile String parrentId;

    /*** videoStream state */
    public AtomicBoolean running;
    public AtomicLong stateFpsStream;
    public AtomicLong stateBitrateStream;
    public AtomicInteger stateWidthStream;
    public AtomicInteger stateHeightStream;

    /*** ffmpeg necessary objects */
    public LinkedBlockingQueue<BufferedImage> bufferedImagesQueue1;
    public LinkedBlockingQueue<BufferedImage> bufferedImagesQueue2;
    private FrameConsumer mdFrameConsumer;
    private FrameConsumer objFrameConsumer;
    private ProgressListener progressListener;
    private AtomicLong trackCounter;
    private FFmpeg ffmpeg;
    private ScheduledExecutorService executorService;


    public VideoStream() {
        this.inputUrl = "";
        this.outputUrl = "";
        this.id = 0;
        this.parrentId = "";
        this.grabberFpsQueue1 = new AtomicLong(0);
        this.grabberFpsQueue2 = new AtomicLong(0);
        this.enableStream = new AtomicBoolean(false);
        this.refrashStream = new AtomicBoolean(false);
        this.running = new AtomicBoolean(false);
        this.stateFpsStream = new AtomicLong(0l);
        this.stateBitrateStream = new AtomicLong(0l);
        this.stateWidthStream = new AtomicInteger(0);
        this.stateHeightStream = new AtomicInteger(0);
        this.bufferedImagesQueue1 = new LinkedBlockingQueue<>();
        this.bufferedImagesQueue2 = new LinkedBlockingQueue<>();
        this.trackCounter = new AtomicLong(0);
        this.mdFrameConsumer = this.createFrameConsumer(this.bufferedImagesQueue1);
        this.objFrameConsumer = this.createFrameConsumer(this.bufferedImagesQueue2);
        this.progressListener = this.createProgressListner();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public LinkedBlockingQueue getQueue1(){
        return this.bufferedImagesQueue1;
    }
    public LinkedBlockingQueue getQueue2(){
        return this.bufferedImagesQueue2;
    }


    private FrameConsumer createFrameConsumer(LinkedBlockingQueue<BufferedImage> bufferedImagesQueue) {
        FrameConsumer frameConsumer = new FrameConsumer() {

            @Override
            public void consumeStreams(List<Stream> tracks) {
                trackCounter.set(tracks.size());
            }

            @Override
            public void consume(Frame frame) {
                final BufferedImage image;
                if ((image = frame.getImage()) != null) {
                    try {
                        bufferedImagesQueue.put(image);
                        if (bufferedImagesQueue.size() > 8) {
                            bufferedImagesQueue.remove();
                        }
                        //logger.info(parrentId +"-"+id+"-"+bufferedImagesQueue.size());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        return frameConsumer;
    }

    private ProgressListener createProgressListner() {
        ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(FFmpegProgress ffmpegProgress) {
                running.set(true);
                if (enableStream.get() == false) {
                    running.set(false);
                    throw new VSManualDisableException("The stream has been manually disabled");
                }
                if (refrashStream.get() == true) {
                    running.set(false);
                    throw new VSRefreshException("The stream has been disabled for a data update");
                }
                if (Thread.currentThread().isInterrupted()) {
                    throw new VSInterruptedException("The stream was interrupted from the outside");
                }
                stateFpsStream.set(ffmpegProgress.getFps().longValue());
                stateBitrateStream.set(ffmpegProgress.getBitrate().longValue());
            }
        };
        return progressListener;
    }

    private FFmpeg createFFmpeg() {
        FFmpeg ffmpeg = FFmpeg.atPath();//ffmpeg.exe
        ffmpeg.addArguments("-rtsp_transport", "tcp")
                .addArguments("-stimeout", "1000000")
                .addArguments("-i", this.inputUrl)
                .addArguments("-c:v", "copy")
                .addArguments("-f", "rtp")
                .addOutput(UrlOutput.toUrl(this.outputUrl))
                .setProgressListener(this.progressListener);
        if (this.grabberFpsQueue1.get() > 0) {
            ffmpeg.addOutput(FrameOutput.withConsumer(this.mdFrameConsumer).setFrameRate(this.grabberFpsQueue1.get())
                    .disableStream(StreamType.AUDIO)
                    .disableStream(StreamType.SUBTITLE)
                    .disableStream(StreamType.DATA));
        }
        if (this.grabberFpsQueue2.get() > 0) {
            ffmpeg.addOutput(FrameOutput.withConsumer(this.objFrameConsumer).setFrameRate(this.grabberFpsQueue2.get())
                    .disableStream(StreamType.AUDIO)
                    .disableStream(StreamType.SUBTITLE)
                    .disableStream(StreamType.DATA));
        }
        ffmpeg.setLogLevel(LogLevel.INFO);
        return ffmpeg;
    }


    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        /*** stream1 parameters */
        out.writeObject(this.enableStream);
        out.writeObject(this.inputUrl);
        out.writeObject(this.outputUrl);
        out.writeObject(this.grabberFpsQueue1);
        out.writeObject(this.grabberFpsQueue2);
        out.writeObject(this.id);
        out.writeObject(this.parrentId);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        /*** stream parameters */
        this.enableStream = (AtomicBoolean) in.readObject();
        this.inputUrl = (String) in.readObject();
        this.outputUrl = (String) in.readObject();
        this.grabberFpsQueue1 = (AtomicLong) in.readObject();
        this.grabberFpsQueue2 = (AtomicLong) in.readObject();
        this.id = (Integer)in.readObject();
        this.parrentId = (String)in.readObject();
    }


    @Override
    public void run() {
        Thread.currentThread().setName(this.parrentId+"-"+this.id);
        logger.info("stream released in new thread");
        if (this.enableStream.get()==false) {
            logger.info("Stream is waiting to be enabled");
            while (this.enableStream.get() == false) {
                if (Thread.currentThread().isInterrupted()) {    //////<---------------
                    break;
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (this.enableStream.get() == true) {
            logger.info("the stream has been enabled");
            this.ffmpeg = this.createFFmpeg();
            this.refrashStream.set(false);
            try {
                this.ffmpeg.execute();
            } catch (VSManualDisableException ex) {
                logger.info(ex.getMessage());
            } catch (VSRefreshException ex) {
                logger.info(ex.getMessage());
            } catch (VSInterruptedException ex) {
                logger.info(ex.getMessage());
            }catch (RuntimeException ex){
                logger.info(ex.getMessage());
            } finally {
                this.running.set(false);
            }
        }
        logger.info("Stream thread has finished");
    }
}
