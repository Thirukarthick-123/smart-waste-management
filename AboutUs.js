import React from "react";

function AboutUs() {
  return (
    <div className="about-card">
      <h3>About Smart Waste Management System</h3>
      <p>
        This project simulates IoT-enabled smart bins that monitor waste
        fill levels in real time. The system helps municipalities optimize
        waste collection using data-driven decisions.
      </p>
      <p>
        Built using <strong>Spring Boot</strong> for backend,
        <strong> React</strong> for frontend, and <strong>MySQL</strong> for
        data persistence.
      </p>
    </div>
  );
}

export default AboutUs;
