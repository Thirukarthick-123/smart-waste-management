import React, { useState } from "react";
import { login } from "../api/authApi";
import { loginUser } from "../utils/auth";

/**
 * Minimal login modal – used from MainLayout.
 * On success we store the JWT (and role) then notify the parent.
 */
export default function Login({ onSuccess, onClose }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async () => {
    setError("");
    try {
      const res = await login({ username, password });
      const token = res.data.token; // back‑end returns { token }
      if (!token) throw new Error("No token in response");
      loginUser(token); // stores token + role (extracted from JWT)
      onSuccess();
    } catch (err) {
      console.error(err);
      setError("Invalid username or password");
    }
  };

  return (
    <div className="add-card">
      <h3>Staff Login</h3>
      <p style={{marginBottom: "10px", fontSize: "0.9em", color: "#888"}}>Choose your role to auto-fill credentials:</p>
      
      <div style={{display: "flex", gap: "10px", marginBottom: "15px"}}>
        <button onClick={() => {setUsername("admin"); setPassword("admin123");}} style={{flex: 1, background: "#10b981", color: "white"}}>Admin</button>
        <button onClick={() => {setUsername("worker"); setPassword("worker123");}} style={{flex: 1, background: "#3b82f6", color: "white"}}>Worker</button>
      </div>

      {error && <p style={{ color: "red", marginBottom: "10px" }}>{error}</p>}

      <input
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        style={{marginBottom: "10px", width: "100%", boxSizing: "border-box"}}
      />

      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        style={{marginBottom: "15px", width: "100%", boxSizing: "border-box"}}
      />

      <button onClick={handleLogin} style={{width: "100%", marginBottom: "10px"}}>Login</button>

      {onClose && (
        <button onClick={onClose} style={{ width: "100%", background: "#4b5563" }}>
          Cancel
        </button>
      )}
    </div>
  );
}
