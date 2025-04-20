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