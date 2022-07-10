package com.example.springbatch;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PushAlarmVO {

    private String userId;
    private String deviceId;
    private int maxRetryCount;
    private String pushAlarmTitle;
    private String pushAlarmContent;
    private String result;

}
