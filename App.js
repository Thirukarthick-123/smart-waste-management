import React, { useEffect, useState } from "react";
import "./App.css";

import Login from "./pages/Login";
import PublicDashboard from "./components/PublicDashboard";
import BinTable from "./components/BinTable";
import MapView from "./components/MapView";

import { getAllBins } from "./api/binApi";
import { isLoggedIn, getRole, logout, isAdmin } from "./utils/auth";

function App() {
  const [page, setPage] = useState("dashboard");
  const [bins, setBins] = useState([]);
  const [showLogin, setShowLogin] = useState(false);

  // Load bins (public API) – already normalised by binApi
  useEffect(() => {
    getAllBins()
      .then((res) => setBins(res.data))
      .catch((err) => console.error("Failed to load bins", err));
  }, []);

  return (
    <div className="app-root">
      {/* ================= TOPBAR ================= */}
      <header className="topbar">
        <div className="brand">
          <div className="logo">SW</div>
          <div>
            <h2>Smart Waste Management</h2>
            <p>
              {isAdmin() ? "Admin Dashboard" : "Public Transparency Dashboard"}
            </p>
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
        </main>
      </div>
    </div>
  );
}

export default App;
