import time, json, requests
from pyspark.sql import SparkSession

# On crée un SparkSession nommé "VelibProducer" pour piloter notre Producer
spark = SparkSession.builder \
    .appName("VelibProducer") \
    .getOrCreate()

VELIB_API = (
  "https://velib-metropole-opendata.smovengo.cloud/opendata/Velib_Metropole/station_status.json"
)


# Boucle infinie pour simuler un flux continu


while True:
    try:
        # Appel à l’API avec un timeout de 10 s
        resp = requests.get(VELIB_API, timeout=10)
        resp.raise_for_status()
        stations = resp.json()["data"]["stations"]
    except Exception as e:
        print(f"Erreur API : {e}")
        time.sleep(30)
        continue

    #  Transforme le json global en mini‑batches (un dictionnaire par station)
    for st in stations:
        record = {
            "station_id": st["station_id"],
            "bikes":    st.get("num_bikes_available", None),
            "docks":    st.get("num_docks_available", None),
            "ts":       st.get("last_reported", None)
        }
        # TO DO : plus tard on enverra via Kafka/socket
        print(json.dumps(record))

    time.sleep(30)
