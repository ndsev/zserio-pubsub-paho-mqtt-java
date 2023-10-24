# Zserio Pub/Sub Paho MQTT Backend

[![](https://github.com/ndsev/zserio-pubsub-paho-mqtt-java/actions/workflows/build_linux.yml/badge.svg)](https://github.com/ndsev/zserio-pubsub-paho-mqtt-java/actions/workflows/build_linux.yml)
[![](https://github.com/ndsev/zserio-pubsub-paho-mqtt-java/actions/workflows/build_windows.yml/badge.svg)](https://github.com/ndsev/zserio-pubsub-paho-mqtt-java/actions/workflows/build_windows.yml)
[![](https://img.shields.io/github/watchers/ndsev/zserio-pubsub-paho-mqtt-java.svg)](https://GitHub.com/ndsev/zserio-pubsub-paho-mqtt-java/watchers)
[![](https://img.shields.io/github/forks/ndsev/zserio-pubsub-paho-mqtt-java.svg)](https://GitHub.com/ndsev/zserio-pubsub-paho-mqtt-java/network/members)
[![](https://img.shields.io/github/stars/ndsev/zserio-pubsub-paho-mqtt-java.svg?color=yellow)](https://GitHub.com/ndsev/zserio-pubsub-paho-mqtt-java/stargazers)

--------

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

# generate example using Zserio
java -jar build/zserio.jar \
     -src examples/zserio/pubsub/paho/mqtt/examples/calculator calculator.zs -java build \
     -setTopLevelPackage paho.mqtt.examples.calculator.gen

# compile example
javac -d build -cp build/zserio-runtime.jar:build/org.eclipse.paho.client.mqttv3.jar \
      src/zserio/pubsub/paho/mqtt/*.java \
      examples/zserio/pubsub/paho/mqtt/examples/calculator/*.java \
      build/paho/mqtt/examples/calculator/gen/calculator/*.java

# run example
java -cp build/zserio-runtime.jar:build/org.eclipse.paho.client.mqttv3.jar:build \
     zserio.pubsub.paho.mqtt.examples.calculator.PowerOfTwoProvider &
java -cp build/zserio-runtime.jar:build/org.eclipse.paho.client.mqttv3.jar:build \
     zserio.pubsub.paho.mqtt.examples.calculator.SquareRootOfProvider &
java -cp build/zserio-runtime.jar:build/org.eclipse.paho.client.mqttv3.jar:build \
     zserio.pubsub.paho.mqtt.examples.calculator.CalculatorClient
```

> For more understandable output run both providers and client in a separate terminal.
