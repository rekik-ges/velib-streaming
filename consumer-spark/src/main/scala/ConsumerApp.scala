import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

object ConsumerApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("VelibConsumer")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val kafkaDf = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "velib-topic")
      .option("startingOffsets", "latest")
      .option("failOnDataLoss", "false")
      .load()

    val rawJsonDf = kafkaDf.selectExpr("CAST(value AS STRING) as json")

    val schema = new StructType()
      .add("results", ArrayType(new StructType()
        .add("stationcode", StringType)
        .add("numbikesavailable", IntegerType)
        .add("numdocksavailable", IntegerType)
        .add("mechanical", IntegerType)
        .add("ebike", IntegerType)
        .add("duedate", StringType)
        .add("capacity", IntegerType)
        .add("coordonnees_geo", new StructType()
          .add("lon", DoubleType)
          .add("lat", DoubleType)
        )
      ))

    val parsedDf = rawJsonDf
      .select(from_json($"json", schema).as("data"))
      .selectExpr("explode(data.results) as record")
      .selectExpr(
        "record.stationcode as station_id",
        "record.numbikesavailable as num_bikes",
        "record.numdocksavailable as num_docks",
        "record.ebike",
        "record.mechanical",
        "record.duedate as timestamp",
        "record.capacity",
        "record.coordonnees_geo.lon as lon",
        "record.coordonnees_geo.lat as lat"
      )
      .withColumn("alert", when($"num_bikes" === 0, "empty")
        .when($"num_docks" === 0, "full")
        .otherwise("ok"))

    parsedDf.writeStream
      .format("json")
      .option("path", "processed_zone/velib_data")
      .option("checkpointLocation", "checkpoint/velib_consumer")
      .outputMode("append")
      .start()
      .awaitTermination()
  }
}