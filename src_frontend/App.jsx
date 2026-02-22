import React, { useEffect, useState } from "react";
import "./styles/App.css";

import Login from "./pages/Login";
import PublicDashboard from "./components/PublicDashboard";
import BinTable from "./components/BinTable";
import MapView from "./components/MapView";

import { getAllBins } from "./api/binApi";
import { getAlerts } from "./api/alertApi";
import { isLoggedIn, logout, isAdmin, isWorker, isStaff } from "./utils/auth";
import RoutesPage from "./pages/RoutesPage";

function App() {
  const [page, setPage] = useState("dashboard");
  const [bins, setBins] = useState([]);
  const [showLogin, setShowLogin] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [lastAlertId, setLastAlertId] = useState(0);
  const lastAlertIdRef = React.useRef(0);

  // Polling for real-time updates (2-second interval)
  useEffect(() => {
    // 1. Fetch initial bins and set lastAlertId to current state
    getAllBins().then(res => setBins(res.data));
    getAlerts().then(res => {
      if (res.data.length > 0) {
        const maxId = Math.max(...res.data.map(a => a.id));
        lastAlertIdRef.current = maxId;
        setLastAlertId(maxId);
      }
    });

    // 2. Start polling for NEW updates
    const fetchData = () => {
      getAllBins()
        .then((res) => setBins(res.data))
        .catch((err) => console.error("Failed to load bins", err));

      getAlerts()
        .then((res) => {
          const newAlerts = res.data.filter(a => a.id > lastAlertIdRef.current);
          if (newAlerts.length > 0) {
            const maxId = Math.max(...res.data.map(a => a.id));
            lastAlertIdRef.current = maxId;
            setLastAlertId(maxId);
            // Add new notifications
            setNotifications(prev => [...newAlerts.map(a => ({ id: a.id, msg: a.message })), ...prev].slice(0, 5));
            // Auto-clear after 10s
            setTimeout(() => setNotifications(prev => prev.slice(0, -newAlerts.length)), 10000);
          }
        });
    };

    const interval = setInterval(fetchData, 1000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="app-root">
      {/* ================= TOPBAR ================= */}
      <header className="topbar">
        <div className="brand">
          <div className="logo">SW</div>
          <div>
            <h2>Smart Waste Management</h2>
            <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
              <p>
                {isAdmin() ? "Admin Console" : isWorker() ? "Worker Dashboard" : "Public Transparency Dashboard"}
              </p>
              <div className="live-indicator">
                <div className="dot"></div>
                Live
              </div>
            </div>
          </div>
        </div>

        {/* Login / Logout button */}
        {isLoggedIn() ? (
          <button className="login-btn logout" onClick={logout}>
            Logout
          </button>
        ) : (
          <button className="login-btn" onClick={() => setShowLogin(true)}>
            Admin Login
          </button>
        )}
      </header>

      {/* ================= LOGIN MODAL ================= */}
      {showLogin && (
        <Login
          onSuccess={() => {
            setShowLogin(false);
            window.location.reload(); // refresh token/role
          }}
          onClose={() => setShowLogin(false)}
        />
      )}

      <div className="layout">
        {/* ================= SIDEBAR ================= */}
        <aside className="sidebar">
          <button
            className={page === "dashboard" ? "active" : ""}
            onClick={() => setPage("dashboard")}
          >
            Dashboard
          </button>

          <button
            className={page === "bins" ? "active" : ""}
            onClick={() => setPage("bins")}
          >
            Smart Bins
          </button>

          {(isAdmin() || isWorker()) && (
            <button
              className={page === "routes" ? "active" : ""}
              onClick={() => setPage("routes")}
            >
              Optimized Routes
            </button>
          )}
        </aside>

        {/* ================= MAIN CONTENT ================= */}
        <main className="content">
          {page === "dashboard" && <PublicDashboard bins={bins} />}

          {page === "bins" && (
            <>
              <MapView bins={bins} />
              <BinTable bins={bins} setBins={setBins} isAdmin={isAdmin()} />
            </>
          )}

          {page === "routes" && <RoutesPage />}
        </main>
      </div>

      {/* ================= NOTIFICATIONS ================= */}
      {isStaff() && (
        <div className="notification-toast-container" style={{ position: "fixed", bottom: "30px", right: "30px", zIndex: 9999 }}>
          {notifications.map(n => (
            <div key={n.id} className="toast" style={{ background: "rgba(239, 68, 68, 0.95)", color: "white", padding: "16px 24px", borderRadius: "16px", marginBottom: "12px", boxShadow: "0 10px 25px rgba(239, 68, 68, 0.3)", backdropFilter: "blur(8px)", border: "1px solid rgba(255,255,255,0.1)" }}>
              <div style={{ display: "flex", gap: "12px", alignItems: "center" }}>
                <span style={{ fontSize: "20px" }}>⚠️</span>
                <div>
                  <strong style={{ display: "block", fontSize: "14px", textTransform: "uppercase", letterSpacing: "0.5px" }}>Critical Alert</strong>
                  <span style={{ fontSize: "15px", opacity: 0.9 }}>{n.msg}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default App;
