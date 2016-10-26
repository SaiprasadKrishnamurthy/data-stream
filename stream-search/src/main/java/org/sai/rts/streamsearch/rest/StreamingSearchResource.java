package org.sai.rts.streamsearch.rest;

import akka.actor.ActorRef;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.sai.rts.streamsearch.actor.KafkaConsumerActor;
import org.sai.rts.streamsearch.config.ActorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import scala.concurrent.Future;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by saipkri on 08/07/16.
 */
@Api("Rest API to submit the data as a stream to be searched on the move")
@RestController
public class StreamingSearchResource {

    private final ActorFactory actorFactory;

    private static final ObjectMapper m = new ObjectMapper();

    @Inject
    public StreamingSearchResource(final ActorFactory actorFactory) {
        this.actorFactory = actorFactory;
    }

    @ApiOperation("Submits the data asynchronously to be searched realtime using streaming search.")
    @CrossOrigin(methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS, RequestMethod.GET})
    @RequestMapping(value = "/streamingsearch/start/{dataCategoryName}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public DeferredResult<ResponseEntity<?>> submitData(@PathVariable("dataCategoryName") final String dataCategoryName, @RequestParam(value = "concurrencyFactor", defaultValue = "1") final int concurrencyFactor) throws Exception {
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(5000L);
        ActorRef kafkaConsumerActor = actorFactory.newActor(KafkaConsumerActor.class);
        Map<String, Object> request = new HashMap<>();
        request.put("topic", dataCategoryName);
        request.put("noOfConsumers", concurrencyFactor);

        Future<Object> submitPayloadToKafkaTopicFuture = Patterns.ask(kafkaConsumerActor, request, KafkaConsumerActor.timeout_in_seconds);

        submitPayloadToKafkaTopicFuture.onComplete(new OnComplete<Object>() {
            @Override
            public void onComplete(Throwable failure, Object success) throws Throwable {
                if (failure != null || !(Boolean) success) {
                    deferredResult.setResult(new ResponseEntity<>("Message: " + failure != null ? failure.getMessage() : "NA", HttpStatus.BAD_REQUEST));
                } else {
                    deferredResult.setResult(new ResponseEntity<>(HttpStatus.OK));
                }
            }
        }, actorFactory.executionContext());
        return deferredResult;
    }
}
