import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object VelibConsumer {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Velib Consumer")
      .master("local[*]")
      .getOrCreate()

    spark.conf.set("spark.hadoop.fs.file.impl", "org.apache.hadoop.fs.LocalFileSystem")
  

    spark.sparkContext.setLogLevel("WARN")

    val velibStreamDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "velib")
      .option("startingOffsets", "earliest")
      .load()

    """val df = velibStreamDF.selectExpr("CAST(value AS STRING) as json")

    // Affiche uniquement quelques champs utiles à partir du JSON brut
    val parsed = df.select(
      get_json_object(col("json"), "$.stationCode").as("stationCode"),
      get_json_object(col("json"), "$.num_bikes_available").cast("int").as("availableBikes"),
      get_json_object(col("json"), "$.num_docks_available").cast("int").as("availableDocks")
    )"""

    // Schéma d’une station
    val stationSchema = new StructType()
      .add("stationCode", StringType)
      .add("num_bikes_available", IntegerType)
      .add("num_docks_available", IntegerType)

    // Schéma du JSON complet
    val fullSchema = new StructType()
      .add("data", new StructType()
        .add("stations", ArrayType(stationSchema))
      )

    // Parsing du flux Kafka
    val df = velibStreamDF.selectExpr("CAST(value AS STRING) as json")

    val parsed = df
      .select(from_json(col("json"), fullSchema).alias("data_parsed"))
      .selectExpr("explode(data_parsed.data.stations) as station")
      .select(
        col("station.stationCode"),
        col("station.num_bikes_available").alias("availableBikes"),
        col("station.num_docks_available").alias("availableDocks")
      )
    
    val query = parsed.writeStream
      .outputMode("append")
      .format("console")
      .start()

    query.awaitTermination()
  }
}