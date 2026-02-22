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
      <h3>Admin Login</h3>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <input
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />

      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button onClick={handleLogin}>Login</button>

      {onClose && (
        <button onClick={onClose} style={{ marginLeft: "10px" }}>
          Cancel
        </button>
      )}
    </div>
  );
}
