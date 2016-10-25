package org.sai.rts.micro.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sai.rts.micro.config.AppProperties;
import org.sai.rts.micro.model.StreamingSearchConfig;
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
public class ESFacade {
    private final AppProperties appProperties;
    private final static ObjectMapper JSONSERIALIZER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(ESFacade.class);

    @Inject
    public ESFacade(final AppProperties appProperties) throws Exception {
        this.appProperties = appProperties;
    }

    // Blocking API
    public Void init(final boolean forceRecreateEsIndex, final List<Map> configs) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        configs.forEach(eventConfig -> {
                    try {
                        StreamingSearchConfig config = JSONSERIALIZER.convertValue(eventConfig, StreamingSearchConfig.class);
                        if (forceRecreateEsIndex) {
                            restTemplate.delete(appProperties.getEsUrl() + "/" + config.getDataCategoryName());
                        }
                        if (isIndexMissing(restTemplate, config)) {
                            LOG.info("\n\n");

                            LOG.info("Creating es index: " + config.getDataCategoryName());
                            // create index.
                            restTemplate.postForObject(appProperties.getEsUrl() + "/" + config.getDataCategoryName(), "{}", Map.class, Collections.emptyMap());

                            LOG.info("Creating es mapping for the type: " + config.getDataName());
                            // apply mappings.
                            restTemplate.postForObject(appProperties.getEsUrl() + "/" + config.getDataCategoryName() + "/_mapping/" + config.getDataName(), JSONSERIALIZER.writeValueAsString(config.getEsIndexMappings()), Map.class, Collections.emptyMap());
                            LOG.info("\n\n");
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
        );
        return null;
    }

    private boolean isIndexMissing(final RestTemplate restTemplate, final StreamingSearchConfig config) {
        try {
            restTemplate.headForHeaders(appProperties.getEsUrl() + "/" + config.getDataCategoryName());
        } catch (Exception ex) {
            return ex.getMessage().contains("404");
        }
        return false;
    }
}
