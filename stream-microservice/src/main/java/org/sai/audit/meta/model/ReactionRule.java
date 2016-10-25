package org.sai.audit.meta.model;

import lombok.Data;

import java.util.List;

/**
 * Created by saipkri on 24/10/16.
 */
@Data
public class ReactionRule {
    private WhenMatchedQuery whenMatchedQuery;
    private List<ThenFetchQuery> thenFetchQueries;
}
