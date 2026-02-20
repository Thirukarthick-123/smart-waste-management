import api from "./api";

/**
 * -------------------------------------------------------------
 * Helper – convert the back‑end BinDTO (the shape returned from
 *   the Spring controller) into the shape the UI expects.
 *
 * Back‑end DTO (BinDTO):
 *   { id, binId, fill, lat, lng, status, lastUpdate }
 *
 * UI shape (used everywhere in the UI):
 *   {
 *     id,
 *     binCode,               // ← binId
 *     area,                  // (no back‑end field – placeholder)
 *     capacityLiters,        // ← fill (percentage)
 *     currentFillPercentage: fill,
 *     status,
 *     latitude:  lat,
 *     longitude: lng,
 *     createdAt: lastUpdate
 *   }
 * -------------------------------------------------------------
 */
const toUI = (dto) => ({
  id: dto.id,
  binCode: dto.binId,
  area: "", // placeholder – UI does not have lat/lng description
  capacityLiters: dto.fill,
  currentFillPercentage: dto.fill,
  status: dto.status,
  latitude: dto.lat,
  longitude: dto.lng,
  createdAt: dto.lastUpdate,
});

/* ---------- convert UI form → back‑end DTO ---------- */
const toDTO = (ui) => ({
  binId: ui.binCode,
  fill: Number(ui.capacityLiters) || 0,
  // The UI does not collect real lat/lng – set to 0 (or map from ‘area’ if you like)
  lat: 0,
  lng: 0,
  status: ui.status || "OK",
});

/* ------------------- PUBLIC ENDPOINTS ------------------- */
export const getAllBins = async () => {
  const resp = await api.get("/api/bins");
  // Normalise each element so the UI can keep using its old field names
  return { ...resp, data: resp.data.map(toUI) };
};

/* ------------------- ADMIN ENDPOINTS ------------------- */
export const addBin = (uiBin) => api.post("/api/bins", toDTO(uiBin));

export const updateBin = (id, uiBin) =>
  api.put(`/api/bins/${id}`, toDTO(uiBin));

export const deleteBin = (id) => api.delete(`/api/bins/${id}`);
