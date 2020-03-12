package zserio.pubsub.paho.mqtt;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;

import zserio.runtime.pubsub.PubsubInterface;
import zserio.runtime.pubsub.PubsubException;

/**
 * Sample Zserio PubsubInterface implementation using Paho MQTT.
 */
public class MqttClient implements PubsubInterface
{
    /**
     * Constructor.
     *
     * @param host Host to connect.
     * @param port Port to connect.
     */
    public MqttClient(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    /**
     * Implementation of PubsubInterface.publish.
     */
    @Override
    public void publish(String topic, byte[] data, Object context)
    {
        try
        {
            final MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(60);

            final String clientId = UUID.randomUUID().toString();

            final IMqttClient client = new org.eclipse.paho.client.mqttv3.MqttClient(
                    "tcp://" + host + ":" + port, clientId);
            client.connect(options);

            final MqttMessage message = new MqttMessage(data);
            client.publish(topic, message);

            client.disconnect();
            client.close();
        }
        catch (MqttException e)
        {
            throw new PubsubException(e.toString());
        }
    }

    /**
     * Implementation of PubsubInterface.subscribe.
     */
    @Override
    public int subscribe(String topic, PubsubInterface.Callback callback, Object context)
    {
        final int subscriptionId = numIds++;
        subscriptions.put(subscriptionId, new MqttSubscription(host, port, topic, callback, context));
        return subscriptionId;
    }

    /**
     * Implementation of PubsubInterface.unsubscribe.
     */
    public void unsubscribe(int subscriptionId)
    {
        final MqttSubscription subscription = subscriptions.remove(subscriptionId);
        if (subscription == null)
            throw new PubsubException("MqttClient: Subscription '" + subscriptionId + "' doesn't exist!");
        subscription.close();
    }

    /**
     * Closes all active subscriptions.
     */
    public void close()
    {
        for (MqttSubscription subscription: subscriptions.values())
            subscription.close();
    }

    private static class MqttSubscription
    {
        public MqttSubscription(String host, int port, String topic,
                PubsubInterface.Callback callback, Object context)
        {
            this.topic = topic;
            this.callback = callback;
            this.context = context;

            final MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(KEEPALIVE);

            final String clientId = UUID.randomUUID().toString();

            try
            {
                client = new org.eclipse.paho.client.mqttv3.MqttClient("tcp://" + host + ":" + port, clientId);
                client.connect(options);

                client.subscribe(topic,
                    new IMqttMessageListener()
                    {
                        public void messageArrived(String topic, MqttMessage message)
                        {
                            callback.invoke(topic, message.getPayload());
                        }
                    }
                );
            }
            catch (MqttException e)
            {
                throw new PubsubException(e.toString());
            }
        }

        public void close()
        {
            try
            {
                client.unsubscribe(topic);
                client.disconnect();
                client.close();
            }
            catch (MqttException e)
            {
                throw new PubsubException(e.toString());
            }
        }

        private final String topic;
        private final PubsubInterface.Callback callback;
        private final Object context;

        private final IMqttClient client;
    };

    private final static int KEEPALIVE = 60;

    private final String host;
    private final int port;

    private int numIds = 0; // simple naive implementation, reusing of subscription ID is not safe
    private java.util.Map<Integer, MqttSubscription> subscriptions =
            new java.util.HashMap<Integer, MqttSubscription>();
}
