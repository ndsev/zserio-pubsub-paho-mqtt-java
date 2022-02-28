package zserio.pubsub.paho.mqtt.examples.calculator;

import java.math.BigInteger;

// PowerOfTwoProvider used with fully qualified name due to name conflict
import paho.mqtt.examples.calculator.gen.calculator.I32;
import paho.mqtt.examples.calculator.gen.calculator.U64;

import zserio.pubsub.paho.mqtt.MqttClient;

import zserio.runtime.pubsub.PubsubCallback;

public class PowerOfTwoProvider
{
    public static void main(String args[])
    {
        MqttClient mqttClient = null;

        try
        {
            for (String arg : args)
            {
                if (arg.equals("-h") || arg.equals("--help"))
                {
                    System.out.println("java -cp CLASSPATH " + args[0] + " [HOST [PORT]]");
                    return;
                }
            }

            String host = "localhost";
            if (args.length > 1)
                host = args[1];
            int port = 1883;
            if (args.length > 2)
                port = Integer.parseInt(args[2]);

            installShutdownHook();

            // instance of Zserio Pub/Sub Paho MQTT client to be used as an PubsubInterface
            mqttClient = new MqttClient(host, port);

            // power of two provider uses the Paho MQTT client backend
            final paho.mqtt.examples.calculator.gen.calculator.PowerOfTwoProvider provider =
                    new paho.mqtt.examples.calculator.gen.calculator.PowerOfTwoProvider(mqttClient);

            provider.subscribeRequest(
                new PubsubCallback<I32>()
                {
                    public void invoke(String topic, I32 value)
                    {
                        System.out.println("PowerOfTwoProvider: request=" + value.getValue());

                        final U64 response = new U64(BigInteger.valueOf(value.getValue()).pow(2));
                        provider.publishPowerOfTwo(response);
                    }
                }
            );

            System.out.println("Power of two provider, waiting for calculator/request...");
            System.out.println("Press Ctrl+C to quit.");

            synchronized(lock)
            {
                lock.wait();
            }
        }
        catch (Exception e)
        {
            System.err.println("PowerOfTwoProvider error: " + e.getMessage());
        }

        if (mqttClient != null)
            mqttClient.close();

        synchronized(lock)
        {
            running = false;
            lock.notify();
        }
    }

    private static void installShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(
            new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        synchronized(lock)
                        {
                            lock.notify();
                        }

                        synchronized(lock)
                        {
                            if (running)
                            {
                                lock.wait();
                            }
                        }
                    }
                    catch (InterruptedException e)
                    {}
                }
            }
        );
    }

    private static final Object lock = new Object();
    private static boolean running = true;
}
