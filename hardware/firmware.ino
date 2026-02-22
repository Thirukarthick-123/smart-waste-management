#include <WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

// WiFi & MQTT Config
const char* ssid = "Wokwi-GUEST";
const char* password = "";
const char* mqtt_server = "broker.emqx.io";
const char* topic = "smart-waste/telemetry";

// Ultrasonic Sensor Pins
const int trigPin = 5;
const int echoPin = 18;
const int blueLed = 2; // Onboard LED for status
const int containerHeight = 100; // cm

// Unique ID for this simulation bin
const char* binId = "BIN-WOKWI-01";

WiFiClient espClient;
PubSubClient client(espClient);

void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("📡 Connecting to WiFi: ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("✅ WiFi Connected!");
  Serial.print("🏠 IP Address: ");
  Serial.println(WiFi.localIP());
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("🔄 Attempting MQTT connection... ");
    String clientId = "ESP32Client-" + String(random(0xffff), HEX);
    if (client.connect(clientId.c_str())) {
      Serial.println("CONNECTED ✅");
    } else {
      Serial.print("FAILED, rc=");
      Serial.print(client.state());
      Serial.println(" ❌ - Retrying in 5 seconds...");
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(115200);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(blueLed, OUTPUT);
  
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  
  Serial.println("🚀 System Initializated. Starting Monitoring...");
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  // 1. Measure Distance
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  long duration = pulseIn(echoPin, HIGH);
  int distance = duration * 0.034 / 2;

  // 2. Calculate Fill Percentage
  int fill = 100 - (distance * 100 / containerHeight);
  if (fill < 0) fill = 0;
  if (fill > 100) fill = 100;

  Serial.print("📏 Sensor Reading: ");
  Serial.print(distance);
  Serial.print("cm | Fill Level: ");
  Serial.print(fill);
  Serial.println("%");

  // 3. Prepare JSON Payload
  StaticJsonDocument<128> doc;
  doc["binId"] = binId;
  doc["fill"] = fill;

  char buffer[128];
  serializeJson(doc, buffer);

  // 4. Publish
  Serial.print("📤 Publishing to Topic: ");
  Serial.println(topic);
  Serial.print("📦 Payload: ");
  Serial.println(buffer);

  if (client.publish(topic, buffer)) {
    Serial.println("✅ [MQTT] Data Published Successfully!");
    // Blink LED on success
    digitalWrite(blueLed, HIGH);
    delay(50);
    digitalWrite(blueLed, LOW);
  } else {
    Serial.println("❌ [MQTT] Failed to publish data.");
  }

  Serial.println("--------------------------------");
  delay(1000); // 1 second high-speed update rate
}
