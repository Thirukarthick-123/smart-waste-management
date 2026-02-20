import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

// ---------- PIN ICON FACTORY ----------
const getIcon = (fill) => {
  let color = "green";

  if (fill >= 90) color = "red";
  else if (fill >= 80) color = "orange";

  return new L.Icon({
    iconUrl: `https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-${color}.png`,
    shadowUrl:
      "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
    iconSize: [25, 41],
    iconAnchor: [12, 41],
  });
};

function MapView({ bins }) {
  return (
    <div className="map-card">
      <MapContainer
        center={[12.9716, 77.5946]} // Bengaluru
        zoom={11}
        style={{ height: "400px", width: "100%" }}
      >
        <TileLayer
          attribution="© OpenStreetMap contributors"
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        {bins.map((bin) => (
          bin.latitude && bin.longitude && (
            <Marker
              key={bin.id}
              position={[bin.latitude, bin.longitude]}
              icon={getIcon(bin.currentFillPercentage)}
            >
              <Popup>
                <b>{bin.binCode}</b> <br />
                Area: {bin.area} <br />
                Fill: {bin.currentFillPercentage}% <br />
                Status: {bin.status}
              </Popup>
            </Marker>
          )
        ))}
      </MapContainer>
    </div>
  );
}

export default MapView;
