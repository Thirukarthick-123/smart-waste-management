# Installation & Setup Guide

Follow these steps to set up the Smart Waste Management system on a new machine.

## 📋 Prerequisites

Ensure you have the following installed:
1. **Java Development Kit (JDK) 17 or higher**: [Download](https://adoptium.net/)
2. **Node.js (v18+) & npm**: [Download](https://nodejs.org/)
3. **Git**: [Download](https://git-scm.com/)
4. **Internet Connection**: Required for MQTT broker and Map tiles.

## 🚀 Quick Launch (Recommended for Windows)

The easiest way to run the project is using the automated starter:

1.  **Double-click `run_project.bat`** in the root directory.
2.  The script will automatically:
    - Verify Java & Node.js.
    - Install missing dependencies.
    - Start the Backend (8080) and Frontend (3000) in new windows.
    - Open the dashboard in your browser.

---

## 🏗️ Manual Setup (Alternative)

1. Open a terminal in the project root.
2. Build and run the backend using the Maven wrapper:
   ```bash
   ./mvnw clean spring-boot:run
   ```
   *(On Windows, use `.\mvnw.cmd`)*

- **Port**: 8080
- **Database**: H2 (In-memory). Console accessible at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:smartwaste`).
- **Default Credentials**:
  - Admin: `admin` / `admin123`
  - Worker: `worker` / `worker123`

---

## 💻 2. Frontend Setup (React + Vite)

1. Open a second terminal in the project root.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```

- **Port**: 3000
- **Accessibility**: Open `http://localhost:3000` in your browser.

---

## 📡 3. Hardware Simulation (Wokwi)

1. Visit [Wokwi.com](https://wokwi.com).
2. Create an **ESP32** project.
3. Use the code provided in `hardware/firmware.ino`.
4. **MQTT Details**:
   - Broker: `broker.emqx.io`
   - Topic: `smart-waste/telemetry`
5. Ensure the **Serial Monitor** in Wokwi is set to **115200 baud**.

---

## ❗ Troubleshooting

- **Port 8080 is Busy**: Kill any existing Java processes (`Stop-Process -Name java` in PowerShell).
- **MQTT Data Not Updating**: Ensure your machine has internet access to connect to the EMQX broker.
- **Map Not Loading**: Check your browser console; usually due to an internet connection issue or CSP block.

---

## ✅ System Verification

Once both services are running:
1. Log in to the Dashboard.
2. The "LIVE" indicator should be green/pulsing.
3. Trigger a fill level change in Wokwi; it should appear on the dashboard within 1 second.
