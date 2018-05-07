package org.tyq.netty.entity;

import lombok.Data;

/**
 * Created by Administrator on 2018/5/4 0004.
 */
@Data
public class TuLingRequest {

    private Integer reqType = 0;

    private UserInfo userInfo = new UserInfo();

    private Perception perception = new Perception();

    @Data
    public class UserInfo{

        private String apiKey = "9c33f7e57a8043c48f6e8ea1a894192e";

        private String userId = "258071";
    }

    @Data
    public class Perception{

        private InputText inputText = new InputText();

        private  InputImage inputImage = new InputImage();

        private SelfInfo selfInfo = new SelfInfo();

    }

    @Data
    public class InputText{
        private String text;
    }

    @Data
    public class InputImage{
        private String url;
    }

    @Data
    public class SelfInfo{
        private Location location = new Location();
    }

    @Data
    public class Location{

        private String city;

        private String province;

        private String street;

    }

}
