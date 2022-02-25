package paho.mqtt.examples.calculator;

import java.math.BigInteger;
import java.lang.Math;

// SquareRootOfProvider used with fully qualified name due to name conflict
import paho.mqtt.examples.calculator.gen.calculator.I32;
import paho.mqtt.examples.calculator.gen.calculator.Double;

import paho.mqtt.MqttClient;

import zserio.runtime.pubsub.PubsubCallback;

public class SquareRootOfProvider
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
            final paho.mqtt.examples.calculator.gen.calculator.SquareRootOfProvider provider =
                    new paho.mqtt.examples.calculator.gen.calculator.SquareRootOfProvider(mqttClient);

            provider.subscribeRequest(
                new PubsubCallback<I32>()
                {
                    public void invoke(String topic, I32 value)
                    {
                        System.out.println("SquareRootOfProvider: request=" + value.getValue());

                        final Double response = new Double(Math.sqrt(value.getValue()));
                        provider.publishSquareRootOf(response);
                    }
                }
            );

            System.out.println("Square root of provider, waiting for calculator/request...");
            System.out.println("Press Ctrl+C to quit.");

            synchronized(lock)
            {
                lock.wait();
            }
        }
        catch (Exception e)
        {
            System.err.println("SquareRootOfProvider error: " + e.getMessage());
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
