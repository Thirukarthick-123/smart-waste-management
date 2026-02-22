import React from "react";

/**
 * Public Dashboard – visible to everyone.
 * Expects bins already normalised (see binApi).
 */
function PublicDashboard({ bins = [] }) {
  const totalBins = bins.length;
  const critical = bins.filter((b) => b.status === "FULL" || b.status === "NEAR_FULL").length;
  const avgFill = totalBins > 0
    ? Math.round(bins.reduce((acc, b) => acc + (b.currentFillPercentage || 0), 0) / totalBins)
    : 0;

  return (
    <div className="dashboard">
      <div className="kpis">
        {/* TOTAL BINS */}
        <div className="kpi green">
          <span>♻</span>
          <div>
            <h3>{totalBins}</h3>
            <p>Total Bins</p>
          </div>
        </div>

        {/* CRITICAL BINS */}
        <div className="kpi red">
          <span>⚠</span>
          <div>
            <h3>{critical}</h3>
            <p>Critical Bins</p>
          </div>
        </div>

        {/* PUBLIC VIEW */}
        <div className="kpi blue">
          <span>🌍</span>
          <div>
            <h3>Public</h3>
            <p>Live View</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default PublicDashboard;
