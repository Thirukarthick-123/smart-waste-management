# AI-Based Smart Waste Management System

A premium, real-time waste management solution featuring AI-driven route optimization, high-fidelity road-network mapping (OSRM), and IoT-integrated bin monitoring.

## 🚀 Key Features

- **AI Route Optimization**: Automatically calculates the most efficient collection path using a Nearest-Neighbor algorithm.
- **Road-Network Visualization**: Uses OSRM API to display actual road paths instead of straight lines.
- **Real-Time IoT Monitoring**: 1-second telemetry updates from ESP32/Wokwi via MQTT.
- **Role-Based Dashboards**:
  - **Admin**: Full control over bin management, analytics, and alerts.
  - **Worker**: Specialized view for collection tasks and navigation.
  - **Public**: Transparency view for citizens to see bin status.
- **Glassmorphism UI**: High-end modern aesthetic with live status indicators and micro-animations.

## 🛠️ Technology Stack

| Layer      | Technology |
| ----------- | ----------- |
| **Backend** | Java 17, Spring Boot, Spring Security, JPA/Hibernate, H2 Database |
| **Frontend** | React, Vite, Leaflet Maps, Axios, CSS3 (Glassmorphism) |
| **IoT/Sim** | ESP32 (Firmware), Wokwi Simulator, MQTT (EMQX Broker) |
| **Routing** | OSRM (Open Source Routing Machine) API |

## 🚀 Quick Start (Windows)

Simply double-click **`run_project.bat`** in the root folder. 
This will:
1. Verify Java & Node.js installation.
2. Install dependencies (`npm install`).
3. Start the Backend and Frontend in separate windows.
4. Auto-open http://localhost:3000 in your browser.

## 📖 Documentation

- [Installation & Setup Guide](INSTALLATION.md)
- [System Walkthrough & Testing](.system_generated/walkthrough.md)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
