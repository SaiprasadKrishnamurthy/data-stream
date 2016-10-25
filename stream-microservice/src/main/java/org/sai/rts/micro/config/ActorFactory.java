package org.sai.rts.micro.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
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


    public ActorFactory(final ActorSystem actorSystem, final AppProperties appProperties) {
        this.actorSystem = actorSystem;
        this.appProperties = appProperties;
        // Create the actor pool.
        //actors.put(MongoDaoActor.class.getName(), actorSystem.actorOf(Props.create(MongoDaoActor.class).withRouter(new RoundRobinPool(appProperties.getConcurrencyFactor()))));
        //actors.put(JmsProducerActor.class.getName(), actorSystem.actorOf(Props.create(JmsProducerActor.class).withRouter(new RoundRobinPool(appProperties.getConcurrencyFactor()))));
    }

    public <T> ActorRef newActor(final Class<T> actorType) {
        return actors.get(actorType.getName());
    }

    public ExecutionContext executionContext() {
        return actorSystem.dispatcher();
    }
}
