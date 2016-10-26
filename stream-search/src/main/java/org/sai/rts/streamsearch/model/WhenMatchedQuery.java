package org.sai.rts.streamsearch.model;

import lombok.Data;

import java.util.Map;

/**
 * Created by saipkri on 24/10/16.
 */
@Data
public class WhenMatchedQuery {
    private String id;
    private Map<String, Object> query;
}
