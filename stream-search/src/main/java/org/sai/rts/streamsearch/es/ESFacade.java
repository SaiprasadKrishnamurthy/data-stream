package org.sai.rts.streamsearch.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sai.rts.streamsearch.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * @author Sai
 */
public class ESFacade {
    private final AppProperties appProperties;
    private final static ObjectMapper JSONSERIALIZER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(ESFacade.class);

    @Inject
    public ESFacade(final AppProperties appProperties) throws Exception {
        this.appProperties = appProperties;
    }
}
