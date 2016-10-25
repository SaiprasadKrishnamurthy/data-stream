package org.sai.rts.micro.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sai.rts.micro.config.AppProperties;
import org.sai.rts.micro.model.EventConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Sai
 */
public class ESInitializer {
    private final AppProperties appProperties;
    private final static ObjectMapper JSONSERIALIZER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(ESInitializer.class);

    @Inject
    public ESInitializer(final AppProperties appProperties) throws Exception {
        this.appProperties = appProperties;
        init(false);
    }

    public Void init(final boolean forceRecreateEsIndex) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        List<Map> configs = JSONSERIALIZER.readValue(ESInitializer.class.getClassLoader().getResourceAsStream("Configs.json"), List.class);
        configs.forEach(eventConfig -> {
                    try {
                        EventConfig config = JSONSERIALIZER.convertValue(eventConfig, EventConfig.class);
                        if (forceRecreateEsIndex) {
                            restTemplate.delete(appProperties.getEsUrl() + "/" + config.getEventCategory());
                        }
                        if (isIndexMissing(restTemplate, config)) {
                            LOG.info("\n\n");

                            LOG.info("Creating es index: " + config.getEventCategory());
                            // create index.
                            restTemplate.postForObject(appProperties.getEsUrl() + "/" + config.getEventCategory(), "{}", Map.class, Collections.emptyMap());

                            LOG.info("Creating es mapping for the type: " + config.getEventName());
                            // apply mappings.
                            restTemplate.postForObject(appProperties.getEsUrl() + "/" + config.getEventCategory() + "/_mapping/" + config.getEventName(), JSONSERIALIZER.writeValueAsString(config.getEsIndexMappings()), Map.class, Collections.emptyMap());
                            LOG.info("\n\n");
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
        );
        return null;
    }

    private boolean isIndexMissing(final RestTemplate restTemplate, final EventConfig config) {
        try {
            restTemplate.headForHeaders(appProperties.getEsUrl() + "/" + config.getEventCategory());
        } catch (Exception ex) {
            return ex.getMessage().contains("404");
        }
        return false;
    }
}
