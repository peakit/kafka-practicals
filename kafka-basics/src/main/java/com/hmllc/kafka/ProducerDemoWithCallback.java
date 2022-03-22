package com.hmllc.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Due to StickyPartitioner at play, all message will go to same partition as Kafka client internally is optimizing for
 * performance and sending the messages as one batch
 */
public class ProducerDemoWithCallback {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerDemoWithCallback.class.getSimpleName());

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
            // create a producer record
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java", "hello world - " + i);

            // send the data - asynchronous
            kafkaProducer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        LOG.info("Topic: " + metadata.topic());
                        LOG.info("Partition: " + metadata.partition());
                        LOG.info("Offset :" + metadata.offset());
                        LOG.info("Timestamp: " + metadata.timestamp());
                    } else {
                        LOG.error("An error occurred while sending messages using callback feature!");
                    }
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error("Sleep was interrupted!");
            }
        }
        // flush the data - synchronous
        kafkaProducer.flush();

        // internally flushes and then closes the producer
        kafkaProducer.close();
    }
}
