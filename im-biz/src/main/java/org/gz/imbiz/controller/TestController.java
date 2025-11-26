package org.gz.imbiz.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gz.qfinfra.rocketmq.producer.RocketmqProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
@RequiredArgsConstructor
public class TestController {
    private final RocketmqProducer rocketmqProducer;
    @Value("${rocketmq.topic:im-chat}")
    private String topic;
    @GetMapping("send-mq")
    public void sendMq(){
        //发送消息
        rocketmqProducer.sendSyncMessage(
                topic,
                null,
                null,
                "重试次数",
                (msgContent, topic, tags, keys, ex, sendResult) -> {
                    // 异常日志
                    log.error("发送消息异常 | topic={}, tags={}, keys={},msg={}",
                            msgContent, topic, tags, keys, ex);

                }
        );
    }
}
