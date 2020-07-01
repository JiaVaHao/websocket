package com.jwh.dobotochatserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DobotoChatServerApplication {

    public final static String USER_HEADING_ROOT_PATH="UserHeading/";
    public final static String MESSAGE_PICTURE_ROOT_PATH="MessagePicture/";

    public static void main(String[] args) {
        SpringApplication.run(DobotoChatServerApplication.class, args);
    }

}
