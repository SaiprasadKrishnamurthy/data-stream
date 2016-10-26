package org.sai.rts.streamsearch.actor;

import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sai.rts.streamsearch.config.ActorFactory;
import org.sai.rts.streamsearch.es.ESFacade;

/**
 * Created by saipkri on 08/09/16.
 */
public class ESActor extends UntypedActor {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static long timeout_in_seconds = 5 * 1000;
    private final ActorFactory actorFactory;
    private final ESFacade esFacade;

    public ESActor(final ActorFactory actorFactory, final ESFacade esFacade) {
        this.actorFactory = actorFactory;
        this.esFacade = esFacade;
    }

    @Override
    public void onReceive(final Object forceRecreateEsIndex) throws Throwable {
        if (forceRecreateEsIndex instanceof Boolean) {
            getSender().tell(true, getSelf());
        }
    }
}
