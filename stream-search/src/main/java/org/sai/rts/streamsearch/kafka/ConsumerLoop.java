package org.sai.rts.streamsearch.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class ConsumerLoop implements Runnable {
    private final KafkaConsumer<String, String> consumer;
    private final List<String> topics;
    private final int id;
    private final Consumer<ConsumerRecord<String, String>> callbackFunction;

    public ConsumerLoop(int id,
                        String groupId,
                        List<String> topics,
                        String brokersCsv, Consumer<ConsumerRecord<String, String>> callbackFunction) {
        this.id = id;
        this.topics = topics;
        this.callbackFunction = callbackFunction;
        Properties props = new Properties();
        props.put("bootstrap.servers", brokersCsv);
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(topics);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(200);
                for (ConsumerRecord<String, String> record : records) {
                    callbackFunction.accept(record);
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            System.out.println("Closing the consumer");
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
}