import React, { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup, Polyline, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { getOptimizedRoute } from "../api/routeApi";

// Fix leaflet icon issue
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png",
  iconUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png",
  shadowUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png",
});

function MapRecenter({ center }) {
  const map = useMap();
  useEffect(() => {
    if (center) map.setView(center, 13);
  }, [center, map]);
  return null;
}

export default function RoutesPage() {
  const [route, setRoute] = useState([]);
  const [loading, setLoading] = useState(true);
  const [userPos, setUserPos] = useState([28.6139, 77.2090]); // Default New Delhi
  const [roadPath, setRoadPath] = useState([]);

  useEffect(() => {
    // Get user location
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => setUserPos([pos.coords.latitude, pos.coords.longitude]),
        (err) => console.warn("Location access denied", err)
      );
    }

    getOptimizedRoute()
      .then(async (res) => {
        const stops = res.data;
        setRoute(stops);

        if (stops.length > 0) {
          await fetchRoadPath(userPos, stops);
        }
        setLoading(false);
      })
      .catch((err) => {
        console.error("Failed to load route", err);
        setLoading(false);
      });
  }, [userPos]);

  const fetchRoadPath = async (startPos, stops) => {
    try {
      const coords = [
        `${startPos[1]},${startPos[0]}`,
        ...stops.map(s => `${s.longitude},${s.latitude}`)
      ].join(";");

      const osrmUrl = `https://router.project-osrm.org/route/v1/driving/${coords}?overview=full&geometries=geojson`;
      const response = await fetch(osrmUrl);
      const data = await response.json();

      if (data.code === "Ok" && data.routes.length > 0) {
        const points = data.routes[0].geometry.coordinates.map(coord => [coord[1], coord[0]]);
        setRoadPath(points);
      } else {
        setRoadPath([startPos, ...stops.map(s => [s.latitude, s.longitude])]);
      }
    } catch (error) {
      console.error("OSRM Fetch Error:", error);
      setRoadPath([startPos, ...stops.map(s => [s.latitude, s.longitude])]);
    }
  };

  if (loading) return <div className="loading">Calculating optimal road path...</div>;

  const polylinePoints = [userPos, ...route.map(r => [r.latitude, r.longitude])];

  return (
    <div className="routes-page">
      <header className="page-header">
        <div className="title-group">
          <h1>AI Optimized Route</h1>
          <p>Road-network navigation starting from your location</p>
        </div>
      </header>

      <div className="route-grid">
        {/* MAP SECTION */}
        <div className="route-map-container card">
          <MapContainer center={userPos} zoom={13} style={{ height: "400px", width: "100%", borderRadius: "12px" }}>
            <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
            <MapRecenter center={userPos} />

            {/* User Location Marker */}
            <Marker position={userPos} icon={new L.Icon({
              iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-gold.png',
              shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
              iconSize: [25, 41],
              iconAnchor: [12, 41]
            })}>
              <Popup>You are here (Collection Start)</Popup>
            </Marker>

            {/* Stop Markers */}
            {route.map((stop, i) => (
              <Marker key={stop.id} position={[stop.latitude, stop.longitude]}>
                <Popup>
                  <strong>Stop {i + 1}: {stop.binCode}</strong><br />
                  Fill: {stop.currentFillPercentage}%
                </Popup>
              </Marker>
            ))}

            <Polyline positions={roadPath} color="#3b82f6" weight={5} opacity={0.8} />
          </MapContainer>
        </div>

        {/* LIST SECTION */}
        <div className="stops-list scrollbar-hidden">
          {route.length === 0 ? (
            <div className="no-route-msg">
              <h3>All Clear!</h3>
              <p>No bins currently require collection.</p>
            </div>
          ) : (
            route.map((stop, index) => (
              <div key={stop.id} className="stop-card">
                <div className="stop-index">{index + 1}</div>
                <div className="stop-info">
                  <h3>{stop.binCode}</h3>
                  <p>Fill: {stop.currentFillPercentage}% | {stop.status}</p>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
