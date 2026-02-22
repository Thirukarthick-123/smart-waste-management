import React from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { logout } from "../utils/auth";

function MainLayout() {
  const navigate = useNavigate();

  return (
    <div className="app-root">
      <header className="topbar">
        <div className="brand">
          <div className="logo">SW</div>
          <div>
            <h2>Smart Waste Management</h2>
            <p>Admin Dashboard</p>
          </div>
        </div>

        <button
          className="login-btn logout"
          onClick={() => {
            logout();
            navigate("/login");
          }}
        >
          🔓 Logout
        </button>
      </header>

      <div className="layout">
        <aside className="sidebar">
          <NavLink to="/">📊 Dashboard</NavLink>
          <NavLink to="/bins">🗑 Smart Bins</NavLink>
          <NavLink to="/routes">🚛 Routes</NavLink>
          <NavLink to="/settings">⚙ Settings</NavLink>
        </aside>

        <main className="content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default MainLayout;
