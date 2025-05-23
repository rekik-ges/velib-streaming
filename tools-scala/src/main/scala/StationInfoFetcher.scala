import java.io.{File, PrintWriter}
import scala.util.{Try, Success, Failure}
import scalaj.http.Http
import org.json4s._
import org.json4s.native.JsonMethods._

object StationInfoFetcher {
  implicit val formats: DefaultFormats.type = DefaultFormats

  case class Station(
    station_id: String,
    name: String,
    lat: Double,
    lon: Double,
    capacity: Int
  )

  def main(args: Array[String]): Unit = {
    val url = "https://velib-metropole-opendata.smovengo.cloud/opendata/Velib_Metropole/station_information.json"
    val response = Try(Http(url).asString.body)

    response match {
      case Success(jsonString) =>
        val json = parse(jsonString)
        val stations = (json \ "data" \ "stations").extract[List[Station]]
        val outputFile = new File("station_metadata.json")
        val writer = new PrintWriter(outputFile)
        stations.foreach { s =>
          writer.println(compact(render(Extraction.decompose(s))))
        }
        writer.close()
        println(s"✅ ${stations.size} stations enregistrées dans station_metadata.json")

      case Failure(exception) =>
        println(s"Erreur de récupération de l’API : ${exception.getMessage}")
    }
  }
}
