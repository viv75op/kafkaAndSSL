package com.lz.kafka.test.support.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Sender {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    //发送消息方法
    public void send(Map<String, String> map, String topic) {
        String msg = JSONObject.toJSONString(map);
        kafkaTemplate.send(topic, msg);
    }

    //发送消息方法
    public void send(Object object, String topic) {
        String msg = JSONObject.toJSONString(object);
        kafkaTemplate.send(topic, msg);
    }
}