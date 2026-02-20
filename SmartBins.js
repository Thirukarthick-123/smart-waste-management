import React, { useEffect, useState } from "react";
import { getAllBins, addBin, updateBin, deleteBin } from "../api/binApi";
import BinTable from "../components/BinTable";

/**
 * Admin view – loads bins, lets the admin add / edit / delete.
 * The UI form uses `location` / `capacity` as field names, but we
 * map them to the shape expected by the back‑end (`binCode`,
 * `capacityLiters`).
 */
export default function SmartBins() {
  const [bins, setBins] = useState([]);
  const [form, setForm] = useState({ location: "", capacity: "" });

  const load = async () => {
    const resp = await getAllBins();
    setBins(resp.data);
  };

  useEffect(() => {
    load();
  }, []);

  const handleAdd = async () => {
    // Map UI fields → the UI‑shape the binApi expects
    const uiBin = {
      binCode: form.location,
      area: "", // area placeholder (not used)
      capacityLiters: form.capacity,
      status: "OK",
    };
    await addBin(uiBin);
    setForm({ location: "", capacity: "" });
    load();
  };

  return (
    <>
      {/* ----- ADD BIN FORM (kept exactly as you wrote) ----- */}
      <div className="add-card">
        <h3>Add Bin</h3>

        <input
          placeholder="Location"
          value={form.location}
          onChange={(e) => setForm({ ...form, location: e.target.value })}
        />

        <input
          type="number"
          placeholder="Capacity"
          value={form.capacity}
          onChange={(e) => setForm({ ...form, capacity: e.target.value })}
        />

        <button onClick={handleAdd}>Add Bin</button>
      </div>

      {/* ----- BIN TABLE (admin actions are already inside BinTable) ----- */}
      <BinTable bins={bins} setBins={setBins} isAdmin={true} />
    </>
  );
}
