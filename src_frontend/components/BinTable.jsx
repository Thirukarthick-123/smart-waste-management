import React, { useState } from "react";
import { deleteBin } from "../api/binApi"; // <-- now using binApi
import "../styles/BinTable.css";

function BinTable({ bins, setBins, isAdmin }) {
  const [idFilter, setIdFilter] = useState("");
  const [fillFilter, setFillFilter] = useState("");

  // FILTER LOGIC (unchanged)
  const filteredBins = bins.filter((bin) => {
    const idMatch = idFilter === "" || bin.id.toString().includes(idFilter);
    const fillMatch =
      fillFilter === "" || bin.currentFillPercentage >= Number(fillFilter);
    return idMatch && fillMatch;
  });

  // DELETE BIN (uses the binApi helper)
  const handleDelete = async (id) => {
    if (!window.confirm("Delete this bin?")) return;
    await deleteBin(id);
    setBins(bins.filter((b) => b.id !== id));
  };

  return (
    <div className="bin-wrapper">
      {/* FILTER BAR */}
      <div className="filter-bar">
        <input
          placeholder="Filter by ID"
          value={idFilter}
          onChange={(e) => setIdFilter(e.target.value)}
        />
        <input
          type="number"
          placeholder="Fill % ≥"
          value={fillFilter}
          onChange={(e) => setFillFilter(e.target.value)}
        />
      </div>

      {/* BIN TABLE */}
      <table className="bin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Bin Code</th>
            <th>Area</th>
            <th>Capacity</th>
            <th>Fill %</th>
            <th>Status</th>
            <th>Latitude</th>
            <th>Longitude</th>
            <th>Created</th>
            <th>Action</th>
          </tr>
        </thead>

        <tbody>
          {filteredBins.map((bin) => (
            <tr key={bin.id}>
              <td>{bin.id}</td>
              <td>{bin.binCode}</td>
              <td>{bin.area}</td>
              <td>{bin.capacityLiters}</td>

              <td>
                <span
                  className={`badge ${bin.currentFillPercentage >= 90
                      ? "danger"
                      : bin.currentFillPercentage >= 80
                        ? "warning"
                        : "ok"
                    }`}
                >
                  {bin.currentFillPercentage}%
                </span>
              </td>

              <td>{bin.status}</td>
              <td>{bin.latitude}</td>
              <td>{bin.longitude}</td>
              <td>{bin.createdAt}</td>

              <td>
                {isAdmin ? (
                  <>
                    <button className="edit-btn">Edit</button>
                    <button
                      className="delete-btn"
                      onClick={() => handleDelete(bin.id)}
                    >
                      Delete
                    </button>
                  </>
                ) : (
                  "—"
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default BinTable;
