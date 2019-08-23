package com.Tags

import com.utils.TagUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 上下文标签
  */
object TagsContext {
  def main(args: Array[String]): Unit = {
    if(args.length != 4){
      println("目录不匹配，退出程序")
      sys.exit()
    }
    val Array(inputPath,outputPath)=args
    // 创建上下文
    val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[*]")
    val sc = new SparkContext(conf)
    val sQLContext = new SQLContext(sc)
    // 读取数据
    val df = sQLContext.read.parquet(inputPath)
    // 过滤符合Id的数据
    df.filter(TagUtils.OneUserId)
      // 接下来所有的标签都在内部实现
        .map(row=>{
          // 取出用户Id
          val userId = TagUtils.getOneUserId(row)
         // 接下来通过row数据 打上 所有标签（按照需求）
        val adList = TagsAd.makeTags(row)
        })

  }
}
