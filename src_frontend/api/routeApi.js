import api from "./api";
import { toUI } from "./binApi";

export const getOptimizedRoute = async () => {
    const resp = await api.get("/api/route/optimized");
    return { ...resp, data: resp.data.map(toUI) };
};
