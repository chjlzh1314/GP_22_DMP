package com.Rpt

import com.utils.RptUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

/**
  * 地域分布指标
  */
object LocationRpt {
  def main(args: Array[String]): Unit = {


    System.setProperty("hadoop.home.dir", "D:\\Huohu\\下载\\hadoop-common-2.2.0-bin-master")
    // 判断路径是否正确
    if(args.length != 2){
      println("目录参数不正确，退出程序")
      sys.exit()
    }
    // 创建一个集合保存输入和输出目录
    val Array(inputPath,outputPath) = args
    val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[*]")
      // 设置序列化方式 采用Kyro序列化方式，比默认序列化方式性能高
      .set("spark.serializer","org.apache.spark.serializer.KryoSerializer")
    // 创建执行入口
    val sc = new SparkContext(conf)
    val sQLContext = new SQLContext(sc)
    // 获取数据
    val df = sQLContext.read.parquet(inputPath)
    // 将数据进行处理，统计各个指标
    df.map(row=>{
      // 把需要的字段全部取到
      val requestmode = row.getAs[Int]("requestmode")
      val processnode = row.getAs[Int]("processnode")
      val iseffective = row.getAs[Int]("iseffective")
      val isbilling = row.getAs[Int]("isbilling")
      val isbid = row.getAs[Int]("isbid")
      val iswin = row.getAs[Int]("iswin")
      val adorderid = row.getAs[Int]("adorderid")
      val WinPrice = row.getAs[Double]("winprice")
      val adpayment = row.getAs[Double]("adpayment")
      // key 值  是地域的省市
      val pro = row.getAs[String]("provincename")
      val city = row.getAs[String]("cityname")
      // 创建三个对应的方法处理九个指标
      val reqlist = RptUtils.request(requestmode,processnode)
      val clicklist = RptUtils.click(requestmode,iseffective)
      val adlist = RptUtils.Ad(iseffective,isbilling,isbid,iswin,adorderid,WinPrice,adpayment)
      ((pro,city),reqlist++clicklist++adlist)
    })
      // 根据Key聚合Value
      .reduceByKey(
      (list1,list2)=>{
        // list(（1,1），（2,2），（3,3）)
      list1.zip(list2).map(t=>t._1+t._2)
    })
      // 整理元素
      .map(t=>{
      t._1+","+t._2.mkString(",")
    })
      .saveAsTextFile(outputPath)
    // 如果存入mysql的话，你需要使用foreachPartition
    // 需要自己写一个连接池
    // 作业
  }
}
