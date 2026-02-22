@echo off
SETLOCAL EnableDelayedExpansion

title Smart Waste Management - One Click Starter

echo ===================================================
echo 🚀 Smart Waste Management - System Launcher
echo ===================================================
echo.

:: 1. Check for Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Java (JDK 17+) is NOT installed or not in PATH.
    echo Please install JDK 17 from https://adoptium.net/
    pause
    exit /b
)
echo ✅ Java detected.

:: 2. Check for Node.js
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Node.js is NOT installed or not in PATH.
    echo Please install Node.js from https://nodejs.org/
    pause
    exit /b
)
echo ✅ Node.js detected.

echo.
echo 📦 Checking Frontend dependencies...
if not exist "node_modules\" (
    echo 📂 node_modules not found. Installing...
    call npm install
) else (
    echo ✅ Dependencies already installed.
)

echo.
echo 🏗️ Starting Services...
echo.

:: Start Backend in a new window
echo [1/2] Launching Spring Boot Backend (Port 8080)...
start "Smart Waste - Backend" cmd /c "mvnw.cmd spring-boot:run -DskipTests"

:: Wait a few seconds for backend to initialize
timeout /t 5 /nobreak >nul

:: Start Frontend in a new window
echo [2/2] Launching Vite Frontend (Port 3000)...
start "Smart Waste - Frontend" cmd /c "npm run dev"

echo.
echo ⏳ Waiting for services to stabilize...
timeout /t 10 /nobreak >nul

echo.
echo 🌍 Opening Application in Browser...
start http://localhost:3000

echo.
echo ===================================================
echo ✅ SYSTEM RUNNING!
echo ---------------------------------------------------
echo Back-end: http://localhost:8080
echo Front-end: http://localhost:3000
echo H2 DB Console: http://localhost:8080/h2-console
echo.
echo IMPORTANT: Keep the other two windows open!
echo To stop the system, close the Backend and Frontend windows.
echo ===================================================
echo.
pause
