package org.sai.rts.micro.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by saipkri on 24/10/16.
 */
@Data
public class EventConfig {
    private String eventCategory;
    private String eventName;
    private Map<String, Object> esIndexMappings;
    private List<ReactionRule> reactionRules;
}
