package org.sai.rts.bolts;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * Created by saipkri on 18/08/16.
 */
public class ESPercolatedSearchBolt extends BaseRichBolt {
    private OutputCollector outputCollector;
    private int boltId;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        this.boltId = topologyContext.getThisTaskId();
    }

    @Override
    public void execute(final Tuple tuple) {
        System.out.println(boltId+" - Tuple Reached in the percolated bolt: " + tuple);
//        outputCollector.emit(tuple, new Values(tuple.getString(0)));
        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(final OutputFieldsDeclarer outputFieldsDeclarer) {
//        outputFieldsDeclarer.declare(new Fields("record"));
    }
}
