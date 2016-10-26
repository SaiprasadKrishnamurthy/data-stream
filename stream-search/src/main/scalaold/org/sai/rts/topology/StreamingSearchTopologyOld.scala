package org.sai.rts.topology

import java.util.Properties

import org.apache.storm.kafka._
import org.apache.storm.spout.SchemeAsMultiScheme
import org.apache.storm.topology.base.BaseBasicBolt
import org.apache.storm.topology.{BasicOutputCollector, OutputFieldsDeclarer, TopologyBuilder}
import org.apache.storm.tuple.Tuple
import org.apache.storm.{StormSubmitter, Config, LocalCluster}

/**
  * Created by saipkri on 25/10/16.
  */
object StreamingSearchTopologyOld extends App {
  val appProperties = new Properties()
  appProperties.load(StreamingSearchTopologyOld.getClass.getClassLoader.getResourceAsStream("application.properties"))
  val kafkaZk = appProperties.getProperty("kafkaZkHostsCsv")

  val config = new Config
  config.put(Config.TOPOLOGY_DEBUG, Boolean.box(true))
  config.put(Config.TOPOLOGY_WORKERS, new Integer(4))
  config.put(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS, new Integer(10000))
  val builder = new TopologyBuilder
  val topic = args(0)

  val brokerHosts = new ZkHosts(kafkaZk)
  val kafkaConfig = new SpoutConfig(brokerHosts, topic, "", StreamingSearchTopologyOld.getClass.getCanonicalName)
  kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme())
  val kafkaSpout: KafkaSpout = new KafkaSpout(kafkaConfig)
  builder.setSpout("streaming_search_" + topic, kafkaSpout, 10);
  builder.setBolt("print", new BaseBasicBolt() {
    override def execute(tuple: Tuple, basicOutputCollector: BasicOutputCollector): Unit = println(tuple.toString)

    override def declareOutputFields(outputFieldsDeclarer: OutputFieldsDeclarer): Unit = {}
  }).shuffleGrouping("streaming_search_" + topic)

  val stormTopology = builder.createTopology()

  if (args != null && args.length > 1) {
    System.out.println("Submitting...")
    config.setNumWorkers(2)
    StormSubmitter.submitTopology(args(0), config, builder.createTopology)
    System.out.println("Submitted...")
  } else {
    val cluster = new LocalCluster()
    cluster.submitTopology("kafka", config, stormTopology)
  }
}
