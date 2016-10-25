package org.sai.audit.meta.rest;

import akka.dispatch.Futures;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.sai.audit.meta.config.ActorFactory;
import org.sai.audit.meta.es.ESInitializer;
import org.sai.audit.meta.model.EventConfig;
import org.sai.audit.meta.util.CallbackFunctionLibrary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import scala.concurrent.Future;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by saipkri on 08/07/16.
 */
@Api("Rest API for the audit config microservice")
@RestController
public class ConfigResource {

    private final ActorFactory actorFactory;
    private final ESInitializer esInitializer;

    private static final ObjectMapper m = new ObjectMapper();

    @Inject
    public ConfigResource(final ActorFactory actorFactory, final ESInitializer esInitializer) {
        this.actorFactory = actorFactory;
        this.esInitializer = esInitializer;
    }

    @ApiOperation("Gets all the audit processing configs configured in the system")
    @CrossOrigin(methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS, RequestMethod.GET})
    @RequestMapping(value = "/configs", method = RequestMethod.GET, produces = "application/json")
    public DeferredResult<ResponseEntity<List<EventConfig>>> allEventConfigs() throws Exception {
        DeferredResult<ResponseEntity<List<EventConfig>>> deferredResult = new DeferredResult<>(5000L);
        Future<Object> results = Futures.successful(m.readValue(ConfigResource.class.getClassLoader().getResourceAsStream("Configs.json"), List.class));
        OnFailure failureCallback = CallbackFunctionLibrary.onFailure(t -> deferredResult.setErrorResult(new ResponseEntity<>(t.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));

        results.onSuccess(new OnSuccess<Object>() {
            public void onSuccess(final Object results) {
                deferredResult.setResult(new ResponseEntity<>((List<EventConfig>) results, HttpStatus.OK));
            }
        }, actorFactory.executionContext());

        results.onFailure(failureCallback, actorFactory.executionContext());
        return deferredResult;
    }

    @ApiOperation("Resyncs the configs stored in the system with elasticsearch")
    @CrossOrigin(methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS, RequestMethod.GET})
    @RequestMapping(value = "/configs/resync", method = RequestMethod.PUT, produces = "application/json")
    public DeferredResult<ResponseEntity<?>> resyncConfigs(@RequestParam("forceRecreateEsIndex") final boolean forceRecreateEsIndex) throws Exception {
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(10000L);
        Future<Void> results = Futures.successful(esInitializer.init(forceRecreateEsIndex));
        OnFailure failureCallback = CallbackFunctionLibrary.onFailure(t -> deferredResult.setErrorResult(new ResponseEntity<>(t.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));

        results.onSuccess(new OnSuccess<Void>() {
            public void onSuccess(final Void results) {
                deferredResult.setResult(new ResponseEntity<>(HttpStatus.CREATED));
            }
        }, actorFactory.executionContext());

        results.onFailure(failureCallback, actorFactory.executionContext());
        return deferredResult;
    }
}
