import React from "react";

/**
 * Public Dashboard
 *
 * • Visible to everyone (no login required)
 * • Shows high-level KPIs only
 * • Uses read-only bin data
 */
function PublicDashboard({ bins = [] }) {
  const totalBins = bins.length;

  // Critical bins: >= 80% fill
  const criticalBins = bins.filter(
    (b) => b.currentFillPercentage >= 80
  ).length;

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
            <h3>{criticalBins}</h3>
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
