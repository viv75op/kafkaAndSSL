package com.lz.kafka.test.api;

import com.lz.kafka.test.support.util.Sender;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/***
 *
 *
 *                                                    __----~~~~~~~~~~~------___
 *                                   .  .   ~~//====......          __--~ ~~
 *                   -.            \_|//     |||\\  ~~~~~~::::... /~
 *                ___-==_       _-~o~  \/    |||  \\            _/~~-
 *        __---~~~.==~||\=_    -_--~/_-~|-   |\\   \\        _/~
 *    _-~~     .=~    |  \\-_    '-~7  /-   /  ||    \      /
 *  .~       .~       |   \\ -_    /  /-   /   ||      \   /
 * /  ____  /         |     \\ ~-_/  /|- _/   .||       \ /
 * |~~    ~~|--~~~~--_ \     ~==-/   | \~--===~~        .\
 *          '         ~-|      /|    |-~\~~       __--~~
 *                      |-~~-_/ |    |   ~\_   _-~            /\
 *                           /  \     \__   \/~                \__
 *                       _--~ _/ | .-~~____--~-/                  ~~==.
 *                      ((->/~   '.|||' -_|    ~~-/ ,              . _||
 *                                 -_     ~\      ~~---l__i__i__i--~~_/
 *                                 _-~-__   ~)  \--______________--~~
 *                               //.-~~~-~_--~- |-------~~~~~~~~
 *                                      //.-~~~--\
 *                               神兽保佑
 *                              代码无BUG!
 * @author sy
 * @date 2019年10月31日
 */

@RestController
@Api(value = "kafka", tags = "kafka")
public class kafkaSendController {
    private static final String SUCCESS = "Success";
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Sender sender;

    @PostMapping("/kafka/sender")
    @ApiOperation(value = "kafka_send_test")
    public String send2Kafka(@RequestBody String sendStr) {
        sender.send(sendStr, "kafka_send_test1");
        return SUCCESS;
    }

    /**
     * 免费票使用处理
     *
     * @param record
     * @param ack
     */
    @KafkaListener(topics = {"kafka_send_test1"})
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            logger.info("listen==========监听到免费票消费数据:" + record.value());
            System.out.println("listen==========监听到免费票消费数据:" + record.value());
            Thread thread = Thread.currentThread();
            logger.info("listen" + thread.getId() + "---------" + thread.getName());
            System.out.println("listen" + thread.getId() + "---------" + thread.getName());
            ack.acknowledge();
            System.out.println("listen==========免费票使用成功");
        } catch (Exception e) {
            logger.error("listen==========系统出现异常:" + e.getMessage());
            logger.error(e.toString(), e);
        }
    }

    /**
     * 免费票使用处理
     *
     * @param record
     * @param ack
     */
    @KafkaListener(topics = {"kafka_send_test1"})
    public void receive(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            logger.info("receive==========监听到免费票消费数据:" + record.value());
            System.out.println("receive==========监听到免费票消费数据:" + record.value());
            Thread thread = Thread.currentThread();
            logger.info("receive" + thread.getId() + "---------" + thread.getName());
            System.out.println("receive" + thread.getId() + "---------" + thread.getName());
            ack.acknowledge();
            System.out.println("receive==========免费票使用成功");
        } catch (Exception e) {
            logger.error("receive==========系统出现异常:" + e.getMessage());
            logger.error(e.toString(), e);
        }
    }
}