package com.example.springbatch;

import org.springframework.batch.item.ItemProcessor;

public class PushAlarmProcessor implements ItemProcessor<PushAlarmVO, PushAlarmVO> {

    @Override
    public PushAlarmVO process(PushAlarmVO item) throws Exception {
        PushAlarmVO pushAlarm = item;
        String publishTo = item.getDeviceId();
        int maxRetryCount = item.getMaxRetryCount();
        boolean websocketSuccess = true;

        for (int i=0; i<maxRetryCount; i++) {
            // websocket 전송
            if (websocketSuccess) break;
        }

        if (websocketSuccess) {
            pushAlarm.setResult("T");
        } else {
            pushAlarm.setResult("F");
        }
        return pushAlarm;
    }
}
