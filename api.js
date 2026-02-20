import axios from "axios";

/**
 * Axios instance for Smart Waste frontend
 *
 * • GET requests work without token
 * • JWT is attached automatically when present
 * • 401 → auto logout (token expired / invalid)
 */
const api = axios.create({
  baseURL: "http://localhost:8080", // Spring Boot backend
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Request interceptor – attach JWT if available
 */
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

/**
 * Response interceptor – handle auth errors globally
 */
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token invalid or expired → logout
      localStorage.removeItem("token");
      localStorage.removeItem("role");
      // Do NOT force redirect (dashboard must stay public)
      console.warn("Unauthorized – token cleared");
    }
    return Promise.reject(error);
  }
);

export default api;
