# Zserio Pub/Sub Paho MQTT Backend

Sample implementation of Zserio Pub/Sub Paho MQTT backend in **Java**.

## Prerequisites

1. Install [Mosquitto](https://mosquitto.org) according to
[official instructions](https://mosquitto.org/download/). Mosquitto is the message broker which implements
MQTT protocol.
   * Check that mosquitto broker is running
     > On Ubuntu check `systemctl status mosquitto`.

2. Java SDK
3. Zserio Java runtime library (`zserio_runtime.jar`)
4. Zserio compiler (`zserio.jar`)

> Zserio prerequisites are included in this repo in 3rdparty folder.

## Usage

### Calculator Example

```bash
mkdir build
# generate service using Zserio
java -jar 3rdparty/zserio.jar \
     -src examples/zserio/pubsub/paho/mqtt/examples/calculator calculator.zs -java build \
     -setTopLevelPackage zserio.pubsub.paho.mqtt.examples.calculator.gen

javac -d build -cp 3rdparty/zserio_runtime.jar:3rdparty/org.eclipse.paho.client.mqttv3-1.2.2.jar \
      src/zserio/pubsub/paho/mqtt/*.java \
      examples/zserio/pubsub/paho/mqtt/examples/calculator/*.java \
      build/zserio/pubsub/paho/mqtt/examples/calculator/gen/calculator/*.java
java -cp 3rdparty/zserio_runtime.jar:3rdparty/org.eclipse.paho.client.mqttv3-1.2.2.jar:build \
     zserio.pubsub.paho.mqtt.examples.calculator.PowerOfTwoProvider &
java -cp 3rdparty/zserio_runtime.jar:3rdparty/org.eclipse.paho.client.mqttv3-1.2.2.jar:build \
     zserio.pubsub.paho.mqtt.examples.calculator.SquareRootOfProvider &
java -cp 3rdparty/zserio_runtime.jar:3rdparty/org.eclipse.paho.client.mqttv3-1.2.2.jar:build \
     zserio.pubsub.paho.mqtt.examples.calculator.CalculatorClient
```

> For more understandable output run both providers and client in a separate terminal.
