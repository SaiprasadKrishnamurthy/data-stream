package org.sai.rts.micro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by saipkri on 07/09/16.
 */
@Component
@ConfigurationProperties("dap")
@Data
public class AppProperties {
    private String esUrl;
}
