import streamlit as st
import pandas as pd
import os
import glob
import plotly.express as px

# Configuration générale de la page
st.set_page_config(page_title="🚲 Dashboard Vélib'", layout="wide")

st.markdown(
    """
    <style>
        .main { background-color: #f4fdf4; }
        .block-container { padding: 2rem 3rem; }
        .metric { font-size: 2rem !important; color: #2e7d32; }
        .stMetric > div > div { font-size: 1.5rem; }
        .big-title { font-size: 2.5rem; font-weight: bold; color: #2e7d32; }
    </style>
    """,
    unsafe_allow_html=True
)

st.markdown("<div class='big-title'>📊 Dashboard Vélib’ – Données Enrichies</div>", unsafe_allow_html=True)

# Chemin vers les fichiers JSON
DATA_DIR = "consumer-spark/processed_zone/velib_data"
files = glob.glob(os.path.join(DATA_DIR, "*.json"))

if not files:
    st.warning("⚠️ Aucune donnée trouvée. Vérifie que les fichiers JSON existent bien.")
    st.stop()

# Lecture et fusion des JSON
df = pd.concat((pd.read_json(f, lines=True) for f in files), ignore_index=True)
df['timestamp'] = pd.to_datetime(df['timestamp'])

# Dernière mise à jour
latest_ts = df['timestamp'].max()
latest_df = df[df['timestamp'] == latest_ts]

st.markdown(f"⏱️ Données mises à jour le : **{latest_ts.strftime('%Y-%m-%d %H:%M:%S')}**")

# KPIs globaux
k1, k2, k3, k4 = st.columns(4)
k1.metric("🚏 Stations actives", latest_df['station_id'].nunique())
k2.metric("🚲 Vélos disponibles", int(latest_df['num_bikes'].sum()))
k3.metric("🅿️ Bornettes libres", int(latest_df['num_docks'].sum()))
k4.metric("❌ Stations vides", int((latest_df['alert'] == "empty").sum()))

st.divider()

# KPIs par station
st.subheader("🔍 Détails par station")

station_metrics = latest_df[['station_id', 'num_bikes', 'num_docks', 'capacity', 'ebike', 'mechanical', 'alert', 'lon', 'lat']]
station_metrics = station_metrics.sort_values(by='num_bikes', ascending=False)

st.dataframe(station_metrics, use_container_width=True)

# Bar chart vélos par station
st.subheader("📊 Vélos disponibles par station")
bar_fig = px.bar(
    station_metrics,
    x="station_id",
    y="num_bikes",
    color="alert",
    title="Nombre de vélos disponibles par station",
    labels={"num_bikes": "Vélos disponibles", "station_id": "Station"},
)
st.plotly_chart(bar_fig, use_container_width=True)

# Carte interactive
st.subheader("🗺️ Carte des stations")
map_fig = px.scatter_mapbox(
    station_metrics,
    lat="lat",
    lon="lon",
    color="alert",
    size="capacity",
    hover_data=["station_id", "num_bikes", "num_docks", "ebike", "mechanical"],
    zoom=11,
    height=500,
)
map_fig.update_layout(mapbox_style="open-street-map", margin={"r":0,"t":0,"l":0,"b":0})
st.plotly_chart(map_fig, use_container_width=True)

# Évolution temporelle
st.subheader("📈 Évolution des vélos disponibles")
agg_df = df.groupby('timestamp')['num_bikes'].sum().reset_index()
st.line_chart(agg_df.set_index('timestamp'))

st.success("✅ Dashboard chargé avec succès.")
