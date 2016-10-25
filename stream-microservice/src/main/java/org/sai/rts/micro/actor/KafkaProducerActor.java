package org.sai.rts.micro.actor;

import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Map;

/**
 * Created by saipkri on 08/09/16.
 */
public class KafkaProducerActor extends UntypedActor {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static long timeout_in_seconds = 5 * 1000;
    private final KafkaProducer<String, String> sender;

    public KafkaProducerActor(final KafkaProducer<String, String> sender) {
        this.sender = sender;
    }

    @Override
    public void onReceive(final Object message) throws Throwable {
        if (message instanceof Map) {
            String topicName = ((Map) message).get("topic").toString();
            String jsonRaw = MAPPER.writeValueAsString(message);
            try {
                sender.send(new ProducerRecord<>(
                        topicName,
                        jsonRaw));
            }catch (Exception ex) {
                ex.printStackTrace();
            }
            getSender().tell(true, getSelf());
        } else {
            getSender().tell(false, getSelf());
        }
    }
}
