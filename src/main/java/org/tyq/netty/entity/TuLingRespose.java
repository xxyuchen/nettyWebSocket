package org.tyq.netty.entity;

import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2018/5/4 0004.
 */
@Data
public class TuLingRespose {

    private Intent intent;

    private List<Results> results;


    @Data
    public class Intent{
        private String actionName;

        private Integer code;

        private String intentName;
    }
    @Data
    public class Results{
        private Integer groupType;
        private String resultType;
        private Values values;

    }
    @Data
    public class Values{
        private String text;
    }
}
