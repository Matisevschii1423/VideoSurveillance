package com.matis.objdetector.Configurations;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LibrarysLoader {
    private Logger logger = LoggerFactory.getLogger(LibrarysLoader.class);

    public LibrarysLoader(){
        this.load();
    }

    private void load(){
        try {
            OpenCV.loadShared();
        }catch (UnsatisfiedLinkError error){
            this.logger.error(error.getStackTrace().toString());
        }catch (Error | Exception error){
            this.logger.error(error.getStackTrace().toString());
        }
    }
}

