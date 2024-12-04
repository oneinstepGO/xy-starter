package com.oneinstep.starter.business.api.producer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.oneinstep.starter.core.mq.kafka.config.CustomKafkaProperties;
import com.oneinstep.starter.core.utils.DateTimeUtil;
import com.oneinstep.starter.core.utils.SnowflakeGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.Future;

@Component
@Slf4j
public class DemoProducer {

    @Resource
    private CustomKafkaProperties kafkaProperties;

    @PostConstruct
    public void init() {
        KafkaProducer<String, String> producer;
        // 初始化
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "client-id-demo");
        producer = new KafkaProducer<>(properties);
        SnowflakeGenerator snowflakeGenerator = SnowflakeGenerator.getInstance();
//        Thread thread = new Thread(() -> {
//            while (true) {
        // 消费消息
        try {
            JSONObject json = new JSONObject();
            long id = snowflakeGenerator.generateId();
            json.put("id", id);
            String message = "message-demo-" + DateTimeUtil.formatDateTime_YYYY_MM_DD_HH_MM_SS(LocalDateTime.now());
            json.put("message", message);
            Future<RecordMetadata> future = producer.send(new ProducerRecord<>("kafka-topic-demo", String.valueOf(id), JSON.toJSONString(json)));
            RecordMetadata recordMetadata = future.get();
            int partition = recordMetadata.partition();
            long offset = recordMetadata.offset();
            log.info("发送消息成功, partition:{}, offset:{}", partition, offset);
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }

//                try {
//                    Thread.sleep(60000);
//                } catch (InterruptedException e) {
//                    log.error("线程休眠失败", e);
//                }

//            }
//        });
//        thread.start();

    }
}
