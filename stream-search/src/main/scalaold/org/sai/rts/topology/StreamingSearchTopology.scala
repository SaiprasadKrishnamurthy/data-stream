//package org.sai.rts.topology
//
//import java.util
//import java.util.Properties
//
//import org.apache.kafka.clients.consumer.KafkaConsumer
//import org.apache.storm.kafka._
//import org.apache.storm.spout.SchemeAsMultiScheme
//import org.apache.storm.topology.base.BaseBasicBolt
//import org.apache.storm.topology.{BasicOutputCollector, OutputFieldsDeclarer, TopologyBuilder}
//import org.apache.storm.tuple.Tuple
//import org.apache.storm.{Config, LocalCluster}
//import org.sai.rts.spouts.StreamingSearchKafkaConsumerSpout
//
///**
//  * Created by saipkri on 25/10/16.
//  */
//object StreamingSearchTopology extends App {
//  val appProperties = new Properties()
//  appProperties.load(StreamingSearchTopology.getClass.getClassLoader.getResourceAsStream("application.properties"))
//
//  val consumerProps = new Properties()
//  consumerProps.load(StreamingSearchTopology.getClass.getClassLoader.getResourceAsStream("consumer.props"))
//
//
//  val kafkaZk = appProperties.getProperty("kafkaZkHostsCsv")
//
//  val config = new Config
//  config.put(Config.TOPOLOGY_DEBUG, Boolean.box(true))
//  config.put(Config.TOPOLOGY_WORKERS, new Integer(4))
//  config.put(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS, new Integer(10000))
//  val builder = new TopologyBuilder
//  val topic = args(0)
//
//  val streamingSearchKafkaConsumerSpout = new StreamingSearchKafkaConsumerSpout(new KafkaConsumer[String, String](consumerProps))
//  builder.setSpout("streaming_search_" + topic, streamingSearchKafkaConsumerSpout, 10)
//
//  builder.setBolt("print", new BaseBasicBolt() {
//    override def execute(tuple: Tuple, basicOutputCollector: BasicOutputCollector): Unit = println(tuple.toString)
//
//    override def declareOutputFields(outputFieldsDeclarer: OutputFieldsDeclarer): Unit = {}
//  }).shuffleGrouping("streaming_search_" + topic)
//
//  val stormTopology = builder.createTopology()
//  val cluster = new LocalCluster()
//  cluster.submitTopology("kafka", config, stormTopology);
//}
