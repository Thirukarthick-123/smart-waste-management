package com.example.smartwaste.service;

import com.example.smartwaste.dto.BinDTO;
import com.example.smartwaste.model.BinStatus;
import com.fazecast.jSerialComm.SerialPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Reads newline‑terminated JSON strings from a serial port (Arduino) and
 * forwards them
 * to {@link BinService}. If the configured port cannot be opened the bean stays
 * in a
 * “head‑less” mode so the rest of the application continues to run.
 */
@Component
@ConditionalOnProperty(name = "serial.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class SerialPortReader {

    /* ------------------------------------------------------------------ */
    /* Collaborators */
    /* ------------------------------------------------------------------ */
    private final BinService binService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /* ------------------------------------------------------------------ */
    /* Configuration properties (read from application.yml) */
    /* ------------------------------------------------------------------ */
    @Value("${serial.port-name}")
    private String portName;

    @Value("${serial.baud-rate}")
    private int baudRate;

    /* ------------------------------------------------------------------ */
    /* Runtime state */
    /* ------------------------------------------------------------------ */
    private SerialPort serialPort; // the actual COM/tty port
    private final StringBuilder lineBuffer = new StringBuilder();

    /* ------------------------------------------------------------------ */
    /* Bean initialisation – try to open the serial port */
    /* ------------------------------------------------------------------ */
    @PostConstruct
    public void init() {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baudRate);

        if (!serialPort.openPort()) {
            log.warn("Serial port {} could NOT be opened (baudRate={}); running in head‑less mode",
                    portName, baudRate);
            // Setting to null makes the scheduled method a no‑op.
            serialPort = null;
        } else {
            log.info("Serial port {} opened successfully (baudRate={})",
                    portName, baudRate);
            // Non‑blocking read with a short timeout (1 s). The scheduler runs every 2 s.
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
        }
    }

    /* ------------------------------------------------------------------ */
    /* Scheduler – runs every 2 seconds, pulls any available bytes */
    /* ------------------------------------------------------------------ */
    @Scheduled(fixedDelay = 2000)
    public void readFromPort() {
        if (serialPort == null || !serialPort.isOpen()) {
            // No hardware attached – nothing to do.
            return;
        }

        byte[] buffer = new byte[1024];
        int bytesRead = serialPort.readBytes(buffer, buffer.length);

        if (bytesRead > 0) {
            String chunk = new String(buffer, 0, bytesRead);
            lineBuffer.append(chunk);

            int nlIdx;
            while ((nlIdx = lineBuffer.indexOf("\n")) >= 0) {
                String line = lineBuffer.substring(0, nlIdx).trim(); // strip trailing '\r'
                lineBuffer.delete(0, nlIdx + 1);
                if (!line.isEmpty()) {
                    processLine(line);
                }
            }
        }
    }

    /* ------------------------------------------------------------------ */
    /** Convert a JSON line into a {@link BinDTO} and hand it to the service */
    /* ------------------------------------------------------------------ */
    private void processLine(String jsonLine) {
        try {
            JsonNode node = objectMapper.readTree(jsonLine);

            BinDTO dto = BinDTO.builder()
                    .binId(node.get("binId").asText())
                    .fill(node.get("fill").asInt())
                    .lat(node.get("lat").asDouble())
                    .lng(node.get("lng").asDouble())
                    .status(parseStatus(node)) // tolerant parsing
                    .build();

            // Log the inbound DTO – helps you verify that the Arduino payload really
            // arrives.
            log.info("📡 Received BinDTO from Arduino: {}", dto);

            binService.processIncomingData(dto);
        } catch (Exception ex) {
            // Any parsing problem (malformed JSON, unknown enum, etc.) is only logged.
            log.warn("Failed to parse line from serial port: '{}'. Error: {}",
                    jsonLine, ex.getMessage());
        }
    }

    /**
     * Convert the JSON <code>status</code> field to {@link BinStatus}.
     * Accepts any case (e.g. "ok", "OK", "Ok") and defaults to {@code OK}
     * if the value is unknown.
     */
    private BinStatus parseStatus(JsonNode node) {
        JsonNode st = node.get("status");
        if (st == null || st.isNull()) {
            return BinStatus.OK;
        }
        try {
            return BinStatus.valueOf(st.asText().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown BinStatus value '{}', defaulting to OK", st.asText());
            return BinStatus.OK;
        }
    }
}
