# Zserio Pub/Sub Paho MQTT Backend

Sample implementation of Zserio Pub/Sub Paho MQTT backend in **Java**.

## Prerequisites

1. [Mosquitto](https://mosquitto.org) message broker running.
   > On Ubuntu check `systemctl status mosquitto`.
2. Java SDK
3. Apache Maven

## Usage

### Calculator Example

```bash
# download the latest zserio version
mvn dependency:copy -Dmaven.repo.local="build/download" \
        -Dartifact=io.github.ndsev:zserio:LATEST \
        -DoutputDirectory="build" -Dmdep.stripVersion=true

# download the latest zserio runtime version
mvn dependency:copy -Dmaven.repo.local="build/download" \
        -Dartifact=io.github.ndsev:zserio-runtime:LATEST \
        -DoutputDirectory="build" -Dmdep.stripVersion=true

# download paho client
mvn dependency:copy -Dmaven.repo.local="build/download" \
        -Dartifact=org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.2 \
        -DoutputDirectory="build" -Dmdep.stripVersion=true

# generate service using Zserio
java -jar build/zserio.jar \
     -src examples/paho/mqtt/examples/calculator calculator.zs -java build \
     -setTopLevelPackage paho.mqtt.examples.calculator.gen

# compile example
javac -d build -cp build/zserio-runtime.jar:build/org.eclipse.paho.client.mqttv3.jar \
      src/paho/mqtt/*.java \
      examples/paho/mqtt/examples/calculator/*.java \
      build/paho/mqtt/examples/calculator/gen/calculator/*.java

# run example
java -cp build/zserio-runtime.jar:build/org.eclipse.paho.client.mqttv3.jar:build \
     paho.mqtt.examples.calculator.PowerOfTwoProvider &
java -cp build/zserio-runtime.jar:build/org.eclipse.paho.client.mqttv3.jar:build \
     paho.mqtt.examples.calculator.SquareRootOfProvider &
java -cp build/zserio-runtime.jar:build/org.eclipse.paho.client.mqttv3.jar:build \
     paho.mqtt.examples.calculator.CalculatorClient
```

> For more understandable output run both providers and client in a separate terminal.
