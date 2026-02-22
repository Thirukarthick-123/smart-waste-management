// Save token & role (role is read from the JWT payload)
export const loginUser = (token) => {
  localStorage.setItem("token", token);
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    const role = payload.role || "";
    localStorage.setItem("role", role);
  } catch (e) {
    console.warn("Could not decode JWT payload", e);
    localStorage.setItem("role", "");
  }
};

// Logout – clear everything and refresh the page
export const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("role");
  window.location.reload();
};

// Quick helpers
export const isLoggedIn = () => !!localStorage.getItem("token");
export const getRole = () => localStorage.getItem("role");

// ADMIN check – works with both “ADMIN” and “ROLE_ADMIN”
export const isAdmin = () => {
  const role = getRole();
  return isLoggedIn() && (role === "ADMIN" || role === "ROLE_ADMIN");
};

// WORKER check
export const isWorker = () => {
  const role = getRole();
  return isLoggedIn() && (role === "WORKER" || role === "ROLE_WORKER");
};

// Check if user is either Admin or Worker
export const isStaff = () => isAdmin() || isWorker();
