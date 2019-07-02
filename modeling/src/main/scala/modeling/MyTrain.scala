package modeling

import java.io.{File, PrintWriter}
import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}
import utils.RedisUtil

import scala.collection.mutable.ArrayBuffer

/**
  * Created by 38636 on 2019/6/28.
  */
object MyTrain {
  def main(args: Array[String]): Unit = {
    val writer = new PrintWriter(new File("model_train.txt"))

    //初始化spark
    val sparkConf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("TrafficModel")
    val sc = new SparkContext(sparkConf)

    //定义redis的数据库相关
    val dbIndex = 1
    //获取redis连接
    val jedis = RedisUtil.pool.getResource
    jedis.select(dbIndex)

    //设立目标监测点：你要对哪几个监测点进行建模
    val monitorIDs = List("0005", "0015")
    //取出相关监测点
    val monitorRelations = Map[String, Array[String]](
      "0005" -> Array("0003", "0004", "0005", "0006", "0007"),
      "0015" -> Array("0013", "0014", "0015", "0016", "0017"))

    //遍历上边所有的监测点，读取数据
    monitorIDs.map(monitorID => {
      //得到当前“目标监测点”的相关监测点
      val monitorRelationArray = monitorRelations(monitorID)//得到的是Array（相关监测点）

      //初始化时间
      val currentDate = Calendar.getInstance().getTime
      //当前小时分钟数
      val hourMinuteSDF = new SimpleDateFormat("HHmm")
      //当前年月日
      val dateSDF = new SimpleDateFormat("yyyyMMdd")
      //以当前时间，格式化好年月日的时间
      val dateOfString = dateSDF.format(currentDate)

      //根据“相关监测点”，取得当日的所有的监测点的平均车速
      //最终结果样式：(0005, {1033=93_2, 1034=1356_30})
      val relationsInfo = monitorRelationArray.map(monitorID => {
        (monitorID, jedis.hgetAll(dateOfString + "_" + monitorID))//
      })
      //确定使用多少小时内的数据进行建模
      val hours = 1
      //创建3个数组，一个数组用于存放特征向量，一数组用于存放Label向量，一个数组用于存放前两者之间的关联
      val dataX = ArrayBuffer[Double]()
      val dataY = ArrayBuffer[Double]()

      //用于存放特征向量和特征结果的映射关系
      val dataTrain = ArrayBuffer[LabeledPoint]()

      //将时间拉回到1个小时之前，倒序，拉回单位：分钟
      for(i <- Range(60 * hours, 2, -1)){
        print(i+"  ")
      }

    })

  }
}
