package com.hmllc.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * As the messages are send to specific keys, they always land on the same partition in every run.
 */
public class ProducerDemoKeys {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerDemoKeys.class.getSimpleName());

    public static void main(String[] args) {
        LOG.info("I am a Kafka Producer!");

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create Kafka producer
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        for (int i = 0; i < 10; i++) {
            String topic = "demo_java";
            String value = "hello world " + i;
            String key = "id_" + i;
            // create a producer record
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);

            // send the data - asynchronous
            kafkaProducer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        LOG.info("Topic: " + metadata.topic());
                        LOG.info("Key: " + key);
                        LOG.info("Partition: " + metadata.partition());
                        LOG.info("Offset :" + metadata.offset());
                        LOG.info("Timestamp: " + metadata.timestamp());
                    } else {
                        LOG.error("An error occurred while sending messages using callback feature!");
                    }
                }
            });
        }
        // flush the data - synchronous
        kafkaProducer.flush();

        // internally flushes and then closes the producer
        kafkaProducer.close();
    }
}
