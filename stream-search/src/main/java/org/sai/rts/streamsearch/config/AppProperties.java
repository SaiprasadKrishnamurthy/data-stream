package org.sai.rts.streamsearch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by saipkri on 07/09/16.
 */
@Component
@ConfigurationProperties("rts")
@Data
public class AppProperties {
    private String esUrl;
    private int consumerConcurrencyFactor;
    private int overallConcurrencyFactor;
    private String kafkaBrokersCsv;
    private String topicsCsv;
    private String consumerGroup;
}
