import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import sttp.client3._

object VelibProducer {
  def main(args: Array[String]): Unit = {

    // Configuration Kafka
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092") // Change si nécessaire
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    // API Vélib
    val velibUrl =
      uri"https://velib-metropole-opendata.smovengo.cloud/opendata/Velib_Metropole/station_status.json"
    val backend = HttpURLConnectionBackend()

    println("Lancement du producer Vélib...")

    // Boucle infinie
    while (true) {
      val request = basicRequest.get(velibUrl)
      val response = request.send(backend)

      response.body match {
        case Right(json) =>
          println(s"[${java.time.LocalTime.now()}] Données récupérées : $json") // Ajout pour afficher les données
          val record = new ProducerRecord[String, String]("velib", null, json)
          producer.send(record)
          println(s"[${java.time.LocalTime.now()}] Données envoyées à Kafka.")
        case Left(error) =>
          println(s"[${java.time.LocalTime.now()}] Erreur lors de la requête API : $error")
      }

      // Attendre 60 secondes
      Thread.sleep(30000)
    }

    producer.close()
  }
}
