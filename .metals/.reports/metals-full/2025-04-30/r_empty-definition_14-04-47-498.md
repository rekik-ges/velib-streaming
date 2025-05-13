error id: 
file:///C:/Users/moham/Documents/PERSO/SCHOOL/S2/S2%20-%20spark%20streaming/Projet/velib-streaming/src/main/scala/VelibProducer.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 309
uri: file:///C:/Users/moham/Documents/PERSO/SCHOOL/S2/S2%20-%20spark%20streaming/Projet/velib-streaming/src/main/scala/VelibProducer.scala
text:
```scala
import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import sttp.client3._

object VelibProducer {
  def main(args: Array[String]): Unit = {

    // Configuration Kafka
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092@@") // Change si nécessaire
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

```


#### Short summary: 

empty definition using pc, found symbol in pc: 