package com.utils

/**
  * 数据类型转换
  */
object Utils2Type {

  // String转换Int
  def toInt(str:String) :Int={
    try {
      str.toInt
    }catch {
      case _ :Exception => 0
    }
  }
  // String转换Double
  def toDouble(str:String) :Double={
    try {
      str.toDouble
    }catch {
      case _ :Exception => 0.0
    }
  }
}
