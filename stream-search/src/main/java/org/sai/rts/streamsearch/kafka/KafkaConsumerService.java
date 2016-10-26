package org.sai.rts.streamsearch.kafka;

import akka.actor.ActorRef;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.sai.rts.streamsearch.actor.KafkaConsumerActor;
import org.sai.rts.streamsearch.config.ActorFactory;
import org.sai.rts.streamsearch.config.AppProperties;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by saipkri on 25/10/16.
 */
@Component
public class KafkaConsumerService {

    private final ActorFactory actorFactory;
    private final AppProperties appProperties;

    @Inject
    public KafkaConsumerService(final ActorFactory actorFactory, final AppProperties appProperties) {
        this.actorFactory = actorFactory;
        this.appProperties = appProperties;
        init();
    }

    private void init() {
        String groupId = appProperties.getConsumerGroup();
        List<String> topics = Arrays.asList(appProperties.getTopicsCsv().split(","));
        final ExecutorService executor = Executors.newFixedThreadPool(appProperties.getConsumerConcurrencyFactor());
        final List<ConsumerLoop> consumers = new ArrayList<>();
        ActorRef kafkaConsumerActor = actorFactory.newActor(KafkaConsumerActor.class);
        Consumer<ConsumerRecord<String, String>> callback = record -> kafkaConsumerActor.tell(record, ActorRef.noSender());
        for (int i = 0; i < appProperties.getConsumerConcurrencyFactor(); i++) {
            ConsumerLoop consumer = new ConsumerLoop(i, groupId, topics, appProperties.getKafkaBrokersCsv(), callback);
            consumers.add(consumer);
            executor.submit(consumer);
        }

        // Very important to tear down!
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (ConsumerLoop consumer : consumers) {
                    consumer.shutdown();
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


    }


}
