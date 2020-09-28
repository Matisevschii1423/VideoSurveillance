package com.matis.objdetector.Model.VideoChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Channel implements Externalizable, Runnable {
    private Logger logger = LoggerFactory.getLogger(Channel.class);
    private static final long serialVersionUID = 1L;
    /*** channel parameters */
    public AtomicInteger number;
    public AtomicBoolean enableChannel;
    public String name;
    public String id;

    /*** channel state*/
    public AtomicBoolean stateChannel;

    /*** streams: mainstream & substream objects */
    public List<VideoStream> videoStreamList;

    /*** detectors */
    public MotionDetector motionDetector;

    /*** scheduledExecutorServices for channel componnents */
    private ScheduledExecutorService videoStreamsExecutor;
    private ScheduledExecutorService schExecutor;


    public Channel() {
        this.number = new AtomicInteger(0);
        this.enableChannel = new AtomicBoolean(false);
        this.stateChannel = new AtomicBoolean(false);
        this.videoStreamList = Arrays.asList(new VideoStream(),new VideoStream());
        this.motionDetector = new MotionDetector(videoStreamList);
        this.schExecutor = Executors.newScheduledThreadPool(3);
    }

    private void launchStreamsExecutors() {
        this.logger.info("Channel_"+this.id+"_launch componnents");
        this.schExecutor = Executors.newScheduledThreadPool(3);
        if (this.videoStreamList.size()!=0){
            for (int i = 0;i<videoStreamList.size();i++){
                schExecutor.scheduleWithFixedDelay(this.videoStreamList.get(i), 1, 2, TimeUnit.SECONDS);
                this.logger.info("Channel_"+this.id+"_stream_"+this.videoStreamList.get(i).id+"_launched");
            }
        }
        this.schExecutor.scheduleWithFixedDelay(this.motionDetector, 1, 2, TimeUnit.SECONDS);
        this.logger.info("Channel_"+this.id+"_mdetector_launched");
        this.logger.info("Componnents launched");
    }

    private void interruptStreamsExecutors() {
        this.schExecutor.shutdownNow();
        this.logger.info("Componnents stopped");
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        /*** channels parameters */
        out.writeObject(this.number);
        out.writeObject(this.name);
        out.writeObject(this.id);
        out.writeObject(this.enableChannel);
        /*** streams parameters */
        out.writeObject(this.videoStreamList);
        /*** motion detector parameters*/
        out.writeObject(this.motionDetector.streamSelector);
        out.writeObject(this.motionDetector.motionZone);
        out.writeObject(this.motionDetector.threshold);
        out.writeObject(this.motionDetector.enableDetector);
        //[6SBnNelb, RnE3IAPL, 3oXIuKcq, 4bzKX0lA, TX4USBQO, 14HcISeD, 54KqrMJ6, bnpyomNQ]
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.number = (AtomicInteger) in.readObject();
        this.name = (String)in.readObject();
        this.id = (String)in.readObject();
        this.enableChannel = (AtomicBoolean) in.readObject();
        /*** stream1 parameters */
        this.videoStreamList = (List) in.readObject();
    }

    @Override
    public void run() {
        Thread.currentThread().setName(this.id);
        this.logger.info("Channel "+this.id+"-->released in new thread");
        if (this.enableChannel.get() == false) {
            this.logger.info("Channel "+this.id+"-->is waiting to be enabled");
            while (this.enableChannel.get() == false) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (this.enableChannel.get()) {
            this.logger.info("Channel "+this.id+"-->channel has been enabled");
            this.launchStreamsExecutors();
            while (this.enableChannel.get()) {
                if (Thread.currentThread().isInterrupted()){
                    this.logger.info("Channel "+this.id+"-->the thread was interrupted");
                    break;
                }
                this.stateChannel.set(true);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    this.logger.info("Channel "+this.id+"-->the thread was interrupted");
                }
            }
            this.interruptStreamsExecutors();
        }
        this.stateChannel.set(false);
        this.logger.info("Channel "+this.id+"-->the thread has ended");
    }

}
