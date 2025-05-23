# velib-streaming
Pipeline Spark streaming Vélib / Producer + Consumer 


## Installation et Exécution du Producer

1. **Cloner le dépôt**  
   ```bash
   git clone git@github.com:MonOrg/velib-streaming.git
   cd velib-streaming/producer-pyspark
# Crée un environnement avec Python 3.10
pip install --upgrade pip
pip install pyspark requests kafka-python

# Depuis le dossier producer-pyspark
python src/producer.py

# 1. Lancer Kafka
docker-compose up -d

# 2. Lancer le Producer (Scala)
sbt run # dans producer-scala/

# 3. Lancer le Consumer (Scala + Spark)
sbt run # dans consumer-spark/

# 4. Lancer le Dashboard
streamlit run dashboard/dashboard.py
( s'assurer d'etre dans un env virtuel rt pip install streamlit pandas plotly
)
 http://localhost:8501/