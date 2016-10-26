package org.sai.rts.streamsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.HdrHistogram.Histogram;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

public class Consumer {
    public static void main(String[] args) throws IOException {
        // set up house-keeping
        ObjectMapper mapper = new ObjectMapper();
        Histogram stats = new Histogram(1, 10000000, 2);
        Histogram global = new Histogram(1, 10000000, 2);

        // and the consumer
        KafkaConsumer<String, String> consumer;
        try (InputStream props = Resources.getResource("consumer.props").openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            if (properties.getProperty("group.id") == null) {
                properties.setProperty("group.id", "group-" + new Random().nextInt(100000));
            }
            consumer = new KafkaConsumer<>(properties);
        }
        consumer.subscribe(Arrays.asList("fast-messages"));
        int timeouts = 0;
        //noinspection InfiniteLoopStatement
        while (true) {
            // read records with a short timeout. If we time out, we don't really care.
            ConsumerRecords<String, String> records = consumer.poll(200);
            System.exit(0);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (records.count() == 0) {
                timeouts++;
            } else {
                System.out.printf("Got %d records after %d timeouts\n", records.count(), timeouts);
                timeouts = 0;
            }
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record);
            }
        }
    }
}