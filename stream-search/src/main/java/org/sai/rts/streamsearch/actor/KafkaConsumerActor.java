package org.sai.rts.streamsearch.actor;

import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.sai.rts.streamsearch.kafka.KafkaConsumerService;

import java.util.Map;

/**
 * Created by saipkri on 08/09/16.
 */
public class KafkaConsumerActor extends UntypedActor {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static long timeout_in_seconds = 5 * 1000;


    @Override
    public void onReceive(final Object message) throws Throwable {
        if (message instanceof ConsumerRecord) {
            System.out.println(message);
        }
    }
}
