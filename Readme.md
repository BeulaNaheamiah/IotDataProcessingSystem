# Deployment Protocol: IoT Data Processing System

## 1\. Overview

This document provides instructions to set up, run, and access the IoT Data Processing System. The system consists of three main Java applications and a Docker-based infrastructure.

* **Infrastructure (Docker Compose)**: Kafka, TimescaleDB, and Adminer.
* **Applications (Java/Maven)**:
    * `IotSimulator`: Generates and sends sensor data to Kafka.
    * `IotCommandService`: Consumes data from Kafka and saves it to the database.
    * `IotQueryService`: Provides a secure REST API to query the data.

## 2\. Prerequisites

Ensure you have the following software installed on your machine:

* **Git**: To clone the repository.
* **Docker & Docker Compose**: To run the infrastructure services.
* **Java 21** (or higher): To run the applications.
* **Apache Maven**: To build and run the applications.

## 3\. Setup Instructions

#### **Step 1: Clone the Repository**

Open your terminal and clone the project to your local machine.

```bash
git clone https://github.com/BeulaNaheamiah/IotDataProcessingSystem.git
cd <your-project-directory>
```

#### **Step 2: Build the Project**

Compile all modules and package them using Maven. Run this command from the root directory of the project.(Ensure docker is running to execute the test containers)

```bash
mvn clean install
```

## 4\. Running the System

You will need three separate terminal windows to run the entire system.

#### **Step 1: Start the Infrastructure (Terminal 1)**

In the root directory of the project, start the Kafka and TimescaleDB containers using Docker Compose.

```bash
docker-compose up -d
```

This will start all required services in the background.

#### **Step 2: Start the Command Service (Terminal 2)**

Navigate to the `IotCommandService` module and start the application.

```bash
cd IotCommandService
mvn spring-boot:run
```

This service will connect to Kafka and start listening for messages.

#### **Step 3: Start the Query Service (Terminal 3)**

Navigate to the `IotInquiryService` module and start the application.

```bash
cd IotInquiryService
mvn spring-boot:run
```

This will start the secure REST API on port `8081`.

#### **Step 4: Start the Simulator (Terminal 4)**

Navigate to the `IotSimulator` module and start the application.

```bash
cd IotSimulator
mvn spring-boot:run
```

The simulator will now start generating data and sending it to Kafka.

## 5\. Accessing and Verifying the System

#### **A. Verify Data Production (Kafka)**

You can watch the live data stream being sent by the simulator by listening to the Kafka topic. Run the following command in a new terminal:

```bash
docker exec -it kafka /usr/bin/kafka-console-consumer --bootstrap-server localhost:29092 --topic iot-reading-topic
```

#### **B. Verify Data Persistence (Database)**

You can view the data being saved by the `IotCommandService` using the Adminer web UI.

1.  Open your web browser and go to **`http://localhost:8081`**.
2.  Log in with the following credentials:
    * **System**: `PostgreSQL`
    * **Server**: `timescaledb`
    * **Username**: `user`
    * **Password**: `password`
    * **Database**: `iot_data`
3.  Click on the `readings_ingestion` table and then "Select data" to see the persisted readings.

#### **C. Access the Query Service (REST API)**

You can query the aggregated data using the secure REST API. Use `curl` or any API client. You must provide a valid username and password (`operator:password`).

**Example Request:** Get the average reading for `thermostat-1` for a specific time range.

```bash
curl -u operator:password -X POST http://localhost:64440/api/readings/v1/aggregate \
-H "Content-Type: application/json" \
-d '{
    "sensorIds": ["thermostat-1"],
    "metric": "AVG",
    "from": "2025-10-01T11:00:00Z",
    "to": "2025-10-01T12:00:00Z"
}'
```

## 6\. Stopping the System

1.  **Stop the Java applications**: Go to each terminal where a service is running and press `Ctrl+C`.
2.  **Stop the infrastructure**: Go to the terminal in the root directory and run:
    ```bash
    docker-compose down
    ```
    To also remove the database volume (delete all data), run `docker-compose down -v`.