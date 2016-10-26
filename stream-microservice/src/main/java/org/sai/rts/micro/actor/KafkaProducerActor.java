package org.sai.rts.micro.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.sai.rts.micro.config.ActorFactory;
import org.sai.rts.micro.model.StreamingSearchConfig;
import scala.concurrent.Future;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created by saipkri on 08/09/16.
 */
public class KafkaProducerActor extends UntypedActor {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static long timeout_in_seconds = 5 * 1000;
    private final KafkaProducer<String, String> sender;
    private final ActorFactory actorFactory;


    public KafkaProducerActor(final KafkaProducer<String, String> sender, final ActorFactory actorFactory) {
        this.sender = sender;
        this.actorFactory = actorFactory;
    }

    @Override
    public void onReceive(final Object message) throws Throwable {
        if (message instanceof Map) {
            ActorRef repositoryActor = actorFactory.newActor(RepositoryActor.class);
            Future<Object> allConfigsFuture = Patterns.ask(repositoryActor, StreamingSearchConfig.class, RepositoryActor.timeout_in_seconds);
            allConfigsFuture.onComplete(new OnComplete<Object>() {
                @Override
                public void onComplete(Throwable failure, Object success) throws Throwable {
                    List<Map> configs = (List<Map>) success;
                    String topicName = ((Map) message).get("topic").toString();
                    String jsonRaw = MAPPER.writeValueAsString(message);
                    List associatedConfigs = configs.stream().filter(config -> config.get("dataCategoryName").toString().equals(topicName)).collect(toList());
                    Map<String, Object> doc = new HashMap<>();
                    doc.put("topicName", topicName);
                    doc.put("associatedConfigs", associatedConfigs);
                    doc.put("payload", jsonRaw);
                    try {
                        sender.send(new ProducerRecord<>(
                                topicName,
                                MAPPER.writeValueAsString(doc)));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, actorFactory.executionContext());
            getSender().tell(true, getSelf());
        } else {
            getSender().tell(false, getSelf());
        }
    }
}
