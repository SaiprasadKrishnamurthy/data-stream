package org.sai.rts.streamsearch.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.sai.rts.streamsearch.actor.ESActor;
import org.sai.rts.streamsearch.actor.KafkaConsumerActor;
import org.sai.rts.streamsearch.es.ESFacade;
import scala.concurrent.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by saipkri on 08/07/16.
 */
public class ActorFactory {

    private Map<String, ActorRef> actors = new HashMap<>();

    private final ActorSystem actorSystem;
    private final AppProperties appProperties;
    private final ESFacade esFacade;


    public ActorFactory(final ActorSystem actorSystem, final AppProperties appProperties, final KafkaProducer<String, String> kafkaTemplate, final ESFacade esFacade) {
        this.actorSystem = actorSystem;
        this.appProperties = appProperties;
        this.esFacade = esFacade;
        // Create the actor pool.
        actors.put(KafkaConsumerActor.class.getName(), actorSystem.actorOf(Props.create(KafkaConsumerActor.class).withRouter(new RoundRobinPool(appProperties.getOverallConcurrencyFactor()))));
        actors.put(ESActor.class.getName(), actorSystem.actorOf(Props.create(ESActor.class, this, esFacade).withRouter(new RoundRobinPool(appProperties.getOverallConcurrencyFactor()))));
    }

    public <T> ActorRef newActor(final Class<T> actorType) {
        return actors.get(actorType.getName());
    }

    public ExecutionContext executionContext() {
        return actorSystem.dispatcher();
    }
}
