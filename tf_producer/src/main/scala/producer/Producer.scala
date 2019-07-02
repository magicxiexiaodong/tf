package producer

import java._
import java.text.DecimalFormat
import java.util.Calendar

import com.alibaba.fastjson.JSON
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import utils.PropertyUtil

import scala.util.Random

/**
  * Created by 38636 on 2019/6/28.
  */
object Producer {
  def main(args: Array[String]): Unit = {
    val properties = PropertyUtil.properties

    val producer = new KafkaProducer[String,String](properties)

    //模拟实时产生数据
    var startTime = Calendar.getInstance().getTimeInMillis() / 1000
    val trafficCycle = 300

    val df  = new DecimalFormat("0000")
    while(true){
      // 模拟产生监测点id : 0001-0020
      val randomMonitorId = df.format(Random.nextInt(20) + 1)
      //模拟车速
      var randomSpeed = "000"
      // 得到本条数据产生时的当前时间，单位：秒
      val currentTime = Calendar.getInstance().getTimeInMillis / 1000

      //每5分钟切换一次公路状态
      if( currentTime - startTime > trafficCycle){
        randomSpeed = new DecimalFormat("000").format(Random.nextInt(15))
        if(currentTime - startTime > trafficCycle * 2){
          startTime = currentTime
        }
      }else {
        randomSpeed = new DecimalFormat("000").format(Random.nextInt(30) +31)
      }
      //该Map集合用于存放产生出来的数据
      val jsonMap = new util.HashMap[String,String]()
      jsonMap.put("monitor_id",randomMonitorId)
      jsonMap.put("speed",randomSpeed)

      //序列化
      val event = JSON.toJSON(jsonMap)
      println(event)

      //发送事件到kafka集群中

      producer.send(new ProducerRecord[String, String](PropertyUtil.getProperty("kafka.topics"), event.toString))
      Thread.sleep(200)
    }
  }
}
