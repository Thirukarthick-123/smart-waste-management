import api from "./api";

/**
 * Authentication API – called by the Login page.
 * Returns { token } (JWT) on success.
 */
export const login = (credentials) => api.post("/api/auth/login", credentials);
