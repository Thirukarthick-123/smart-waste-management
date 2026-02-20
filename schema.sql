DROP DATABASE IF EXISTS smart_waste;
CREATE DATABASE smart_waste;
USE smart_waste;

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    email VARCHAR(150),
    phone VARCHAR(20),
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE user_roles (
    user_id INT,
    role_id INT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE bins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bin_code VARCHAR(50) NOT NULL UNIQUE,
    latitude DOUBLE,
    longitude DOUBLE,
    area VARCHAR(100),
    capacity_liters INT,
    current_fill_percentage INT DEFAULT 0,
    status ENUM('OK','NEAR_FULL','FULL','OFFLINE') DEFAULT 'OK',
    last_collected_at DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bin_sensor_readings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bin_id INT NOT NULL,
    reading_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    fill_percentage INT,
    battery_level INT,
    temperature DOUBLE,
    FOREIGN KEY (bin_id) REFERENCES bins(id)
);

CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    license_plate VARCHAR(50),
    capacity_liters INT,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE collection_routes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    route_date DATE,
    status ENUM('PLANNED','IN_PROGRESS','COMPLETED') DEFAULT 'PLANNED',
    algorithm_used VARCHAR(100),
    vehicle_id INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

CREATE TABLE route_stops (
    id INT AUTO_INCREMENT PRIMARY KEY,
    route_id INT NOT NULL,
    bin_id INT NOT NULL,
    stop_order INT,
    planned_arrival DATETIME NULL,
    actual_arrival DATETIME NULL,
    pickup_confirmed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (route_id) REFERENCES collection_routes(id),
    FOREIGN KEY (bin_id) REFERENCES bins(id)
);

CREATE TABLE alerts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bin_id INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    alert_type VARCHAR(50),
    message TEXT,
    resolved BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (bin_id) REFERENCES bins(id)
);

CREATE TABLE bin_fill_predictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bin_id INT,
    predicted_time DATETIME,
    predicted_fill_percentage INT,
    model_version VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bin_id) REFERENCES bins(id)
);
