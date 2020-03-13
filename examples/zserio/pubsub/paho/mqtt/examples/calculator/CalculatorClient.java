package zserio.pubsub.paho.mqtt.examples.calculator;

import java.util.Scanner;

// CalculatorClient used with fully qualified name due to name conflict
import zserio.pubsub.paho.mqtt.examples.calculator.gen.calculator.I32;
import zserio.pubsub.paho.mqtt.examples.calculator.gen.calculator.Double;
import zserio.pubsub.paho.mqtt.examples.calculator.gen.calculator.U64;

import zserio.pubsub.paho.mqtt.MqttClient;

import zserio.runtime.pubsub.PubsubCallback;

public class CalculatorClient
{
    public static void main(String args[])
    {
        mainThread = Thread.currentThread();

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

            System.out.println("Welcome to Zserio Calculator Paho MQTT Pub/Sub Client example!");
            System.out.print("Creating client and subscriptions (terminate with ^C) ...");

            installShutdownHook();

            // instance of Zserio Pub/Sub Paho MQTT client to be used as an PubsubInterface
            mqttClient = new MqttClient(host, port);

            // calculator client uses the Paho MQTT client backend
            final zserio.pubsub.paho.mqtt.examples.calculator.gen.calculator.CalculatorClient client =
                    new zserio.pubsub.paho.mqtt.examples.calculator.gen.calculator.CalculatorClient(mqttClient);

            final PubsubCallback<U64> powerOfTwoCallback =
                new PubsubCallback<U64>()
                {
                    public void invoke(String topic, U64 value)
                    {
                        System.out.println("power of two: " + value.getValue());
                    }
                };
            int powerOfTwoId = client.subscribePowerOfTwo(powerOfTwoCallback);
            boolean powerOfTwoSubscribed = true;

            final PubsubCallback<Double> squareRootOfCallback =
                new PubsubCallback<Double>()
                {
                    public void invoke(String topic, Double value)
                    {
                        System.out.println("square root of: " + value.getValue());
                    }
                };
            int squareRootOfId = client.subscribeSquareRootOf(squareRootOfCallback);
            boolean squareRootOfSubscribed = true;

            System.out.println(" OK!");
            System.out.println("Write 'h' + ENTER for help.");

            final Scanner scanner = new Scanner(System.in);

            while (true && !mainThread.isInterrupted())
            {
                System.out.println(
                        (powerOfTwoSubscribed ? "p" : "") + (squareRootOfSubscribed ? "s" : "") + "> ");

                if (!hasNextLine())
                    break;

                final String input = scanner.nextLine();
                if (input.isEmpty())
                    continue;

                if (input.charAt(0) == 'q')
                {
                    System.out.println("Quit.");
                    break;
                }

                if (input.charAt(0) == 'h')
                {
                    printHelp();
                    continue;
                }

                if (input.charAt(0) == 'p')
                {
                    if (powerOfTwoSubscribed)
                    {
                        client.unsubscribe(powerOfTwoId);
                        powerOfTwoSubscribed = false;
                    }
                    else
                    {
                        powerOfTwoId = client.subscribePowerOfTwo(powerOfTwoCallback);
                        powerOfTwoSubscribed = true;
                    }
                    continue;
                }

                if (input.charAt(0) == 's')
                {
                    if (squareRootOfSubscribed)
                    {
                        client.unsubscribe(squareRootOfId);
                        squareRootOfSubscribed = false;
                    }
                    else
                    {
                        squareRootOfId = client.subscribeSquareRootOf(squareRootOfCallback);
                        squareRootOfSubscribed = true;
                    }
                    continue;
                }

                publishRequest(client, input);
            }
        }
        catch (Exception e)
        {
            System.err.println("CalculatorClient error: " + e.getMessage());
        }

        if (mqttClient != null)
            mqttClient.close();

        synchronized(lock)
        {
            running = false;
            lock.notify();
        }
    }

    // hack to use instead of Scanner.hasNextLine since it is not interruptible
    private static boolean hasNextLine()
    {
        try
        {
            while (System.in.available() == 0)
            {
                Thread.currentThread().sleep(100);
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    private static void installShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(
            new Thread()
            {
                @Override
                public void run()
                {
                    mainThread.interrupt();
                    try
                    {
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

    private static void printHelp()
    {
        System.out.println("Help:");
        System.out.println(" INPUT        Any valid 32bit integer.");
        System.out.println(" p            Subscribes/Unsubscribes calculator/power_of_two topic.");
        System.out.println(" s            Subscribes/Unsubscribes calculator/square_root_of topic.");
        System.out.println(" h            Prints this help.");
        System.out.println(" q            Quits the client.");
        System.out.println("");
        System.out.println("Note that the letters before the '>' denotes the subscribed topics.");
    }

    private static void publishRequest(
            zserio.pubsub.paho.mqtt.examples.calculator.gen.calculator.CalculatorClient client, String input)
    {
        I32 request;
        try
        {
            final int value = Integer.parseInt(input);
            request = new I32(value);
        }
        catch (Exception e)
        {
            System.out.println("Error: '" + input + "' cannot be converted to int32!");
            System.out.println(e.getMessage());
            return;
        }

        client.publishRequest(request);
    }

    private static Thread mainThread = null;
    private static MqttClient mqttClient = null;
    private static final Object lock = new Object();
    private static boolean running = true;
}
