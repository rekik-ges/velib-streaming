import java.net.{HttpURLConnection, URL}
import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object VelibProducer {
  def main(args: Array[String]): Unit = {
    val apiUrl = "https://data.opendatasoft.com/api/explore/v2.1/catalog/datasets/velib-disponibilite-en-temps-reel@parisdata/records?limit=100"

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)
    val topic = "velib-topic"

    try {
      val url = new URL(apiUrl)
      val conn = url.openConnection().asInstanceOf[HttpURLConnection]
      conn.setRequestMethod("GET")
      val inputStream = conn.getInputStream
      val content = scala.io.Source.fromInputStream(inputStream).mkString
      inputStream.close()

      val record = new ProducerRecord[String, String](topic, null, content)
      producer.send(record)
      println("✔ Données envoyées dans Kafka")
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      producer.close()
    }
  }
}