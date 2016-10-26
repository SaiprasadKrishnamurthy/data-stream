package org.sai.rts.spouts

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.storm.spout.SpoutOutputCollector
import org.apache.storm.task.TopologyContext
import org.apache.storm.topology.OutputFieldsDeclarer
import org.apache.storm.topology.base.BaseRichSpout
import org.apache.storm.tuple.{Fields, Values}

/**
  * Created by saipkri on 25/10/16.
  */
class StreamingSearchKafkaConsumerSpout(consumer: KafkaConsumer[String, String]) extends BaseRichSpout {

  private var _collector: SpoutOutputCollector = null
  private var _consumer: KafkaConsumer[String, String] = null

  _consumer = consumer

  override def declareOutputFields(outputFieldsDeclarer: OutputFieldsDeclarer) {
    outputFieldsDeclarer.declare(new Fields("state", "totalPageSize"))
  }

  override def open(map: java.util.Map[_, _], topologyContext: TopologyContext, spoutOutputCollector: SpoutOutputCollector) {
    _collector = spoutOutputCollector
  }

  override def nextTuple {
    _collector.emit(new Values("delhi", new Integer(5)))
  }
}
