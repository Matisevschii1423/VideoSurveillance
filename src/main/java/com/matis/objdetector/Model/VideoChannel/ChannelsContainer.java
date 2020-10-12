package com.matis.objdetector.Model.VideoChannel;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Component
public class ChannelsContainer {
    private Logger logger = LoggerFactory.getLogger(ChannelsContainer.class);

    public String serializationFile = "channels_config.bin";

    public HashMap<String, Channel> channels;
    public ScheduledExecutorService scheduledExecutorService;

    private List<String> tempId;

    public ChannelsContainer() {
        this.channels = new HashMap<>();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(8,this.getFactory());
        this.tempId = new ArrayList<>();
    }

    public void executeAllChannels() {
        if (this.channels != null) {
            if (this.channels.size() != 0) {
                Set<String> keys = this.channels.keySet();
                for (String key : keys) {
                    this.scheduledExecutorService.scheduleWithFixedDelay(this.channels.get(key), 1, 1, TimeUnit.SECONDS);
                }
            }
        }
    }

    private ThreadFactory getFactory() {
        ThreadFactory factory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("CH-");
                return t;
            }
        };
        return factory;
    }

    public void interruptAllChannels() {
        this.scheduledExecutorService.shutdownNow();
    }

    public void saveChannelsParameters() {
        this.channelsSerialization(this.channels, this.serializationFile);
    }

    private void channelsSerialization(HashMap<String, Channel> channels, String fileName) {
        logger.info("Serialization process has started");
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);) {
                Set<String> keys = channels.keySet();
                for (String key : keys) {
                    objectOutputStream.writeObject(channels.get(key));
                    logger.info("Channel:[" + "Nr:"
                            + channels.get(key).number + ",ID:"
                            + channels.get(key).id + ",Name:"
                            + channels.get(key).name + "]"
                            + " has been serialized");
                }
            } catch (IOException e) {
                logger.info(e.getMessage());
                logger.info("Serialization process has ended");
            }
        } catch (FileNotFoundException e) {
            logger.info(e.getMessage());
            logger.info("Serialization process has ended");
        } catch (IOException e) {
            logger.info(e.getMessage());
            logger.info("Serialization process has ended");
        }
        logger.info("Serialization process has ended");
    }

    public void restoreParameters() {
        this.channels = this.channelsDeserialization(this.serializationFile);
    }

    private HashMap<String, Channel> channelsDeserialization(String fileName) {
        logger.info("Deserialization process has started");
        HashMap<String, Channel> channelsContainer = new HashMap<>();
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

                for (int i = 1; i < 9; i++) {
                    Channel ch = (Channel) objectInputStream.readObject();
                    channelsContainer.put(ch.id, ch);
                    logger.info("Channel:[" + "Nr:" + ch.number + ",ID:" + ch.id + ",Name:" + ch.name + "]" + " has been deserialized");
                }
            } catch (ClassNotFoundException e) {
                logger.warn(e.getMessage());
                logger.info("Deserialization process has ended");
                return getDefaultChannels();
            }

        } catch (FileNotFoundException e) {
            logger.warn(e.getMessage());
            logger.info("Deserialization process has ended");
            return getDefaultChannels();
        } catch (IOException e) {
            logger.warn(e.getMessage());
            logger.info("Deserialization process has ended");
            return getDefaultChannels();
        }
        logger.info("Deserialization process has ended");
        return channelsContainer;
    }

    private HashMap<String, Channel> getDefaultChannels() {
        logger.info("Generating a channel list with default parameters");
        HashMap<String, Channel> emptyChannels = new HashMap<>();
        for (int i = 1; i < 9; i++) {
            Channel ch = new Channel();
            ch.id = generateRandomUnicId();
            ch.name = "Channel_" + i;
            ch.number.set(i);
            for (int j = 0; j < ch.videoStreamList.size(); j++) {
                ch.videoStreamList.get(j).id = (i * 100 + (j + 1));
                ch.videoStreamList.get(j).parrentId = ch.id;
                if (j==0) {
                    ch.videoStreamList.get(j).inputUrl = "rtsp://admin:galaxymini111@192.168.0.100:554/live/main";
                    ch.videoStreamList.get(j).outputUrl = "rtp://192.168.0.48:" + (5000 + ch.videoStreamList.get(j).id) + "?pkt_size=1300";
                }
                if (j==1){
                    ch.videoStreamList.get(j).inputUrl = "rtsp://admin:galaxymini111@192.168.0.100:554/live/sub";
                    ch.videoStreamList.get(j).outputUrl = "rtp://192.168.0.48:" + (5000 + ch.videoStreamList.get(j).id) + "?pkt_size=1300";
                }
                if (j == 0) {
                    ch.videoStreamList.get(j).grabberFpsQueue1.set(5);
                }
                if (i == 1) {
                    ch.videoStreamList.get(j).enableStream.set(true);
                    ch.videoStreamList.get(j).enableStream.set(true);
                }
            }
            ch.enableChannel.set(true);
            logger.info("Channel:[" + "Nr:" + ch.number + ",ID:" + ch.id + ",Name:" + ch.name + "]" + " was generated");
            ch.motionDetector = new MotionDetector(ch.videoStreamList);
            ch.motionDetector.parrentId = ch.id;
            ch.motionDetector.enableDetector.set(true);
            ch.motionDetector.streamSelector.set(1);
            emptyChannels.put(ch.id, ch);
        }
        tempId = new ArrayList<>();

        logger.info("channel generation is complete");
        return emptyChannels;
    }


    private String generateRandomUnicId() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 8;
        Random random = new Random();
        String generatedString = null;
        while (true) {
            generatedString = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97) && i <= 122)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            if (tempId.contains(generatedString)) {
                continue;
            } else {
                tempId.add(generatedString);
                break;
            }
        }
        return generatedString;
    }


}

