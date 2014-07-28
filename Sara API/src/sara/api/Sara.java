package sara.api;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import sara.api.communication.message.MessageHandShakeConfirmation;
import sara.api.communication.message.MessageOperationsUrl;
import sara.api.communication.message.MessageThingId;
import sara.api.communication.message.SaraMessage;
import sara.api.handler.LogEventArgs;
import sara.api.handler.SaraEventArgs;
import sara.api.interfaces.ILogEvent;
import sara.api.interfaces.ISaraEvent;
import sara.api.tools.SaraConstants;
import sara.api.tools.SaraLog;
import sara.api.tools.SaraStatus;

public class Sara extends EventObject implements Runnable, MqttCallback
{
	// Properties
	private static final long serialVersionUID = 4335218002356779457L;
	private SaraLog log;
	private List<ISaraEvent> eventListeners;
	private SaraStatus saraStatus;
	private Thread threadSara;
	private String threadName;
	private Boolean isRunning;
	private List<SaraMessage> messagesToSend;
	private String thingId;
	private String operationsUrl;

	// Constructors
	public Sara(Object sender)
	{
		// Call super constructor
		super(sender);

		// New instances and default values
		log = new SaraLog(this);
		eventListeners = new ArrayList<ISaraEvent>();
		saraStatus = SaraStatus.OFFLINE;
		isRunning = false;
		threadName = SaraConstants.THREAD_SARA_NAME;
		messagesToSend = new LinkedList<SaraMessage>();
		thingId = "Device01";
		operationsUrl = "http://sara.com";

		// Log
		log.add("Sara created");
	}

	// Public methods
	@Override
	public void run()
	{
		String topic = "MQTT Examples";
		String serverBroker = "tcp://192.168.0.104:60001";

		MemoryPersistence persistence = new MemoryPersistence();
		MqttConnectOptions connectionOptions = new MqttConnectOptions();
		connectionOptions.setCleanSession(true);
		isRunning = true;

		try
		{
			MqttClient sampleClient = new MqttClient(serverBroker, thingId, persistence);
			while (isRunning)
			{

				if (saraStatus.equals(SaraStatus.FINDING_CENTRAL))
				{
					// Setting configurations
					sampleClient = new MqttClient(serverBroker, thingId, persistence);

					// Log
					log.add("Try broker connection: " + serverBroker);

					try
					{
						// Try to connect, if the server doesnt exists, it will
						// generate a ConnectException
						sampleClient.connect(connectionOptions);

						// The server exists, register callback behavior
						sampleClient.setCallback(this);

						// Update internal status and run events
						saraStatus = SaraStatus.CENTRAL_FOUND;
						onCentralFoundEventHandler(new SaraEventArgs(""));
					}
					catch (Exception ex)
					{
						if (ex instanceof ConnectException)
						{
							// Server not found, try another one
							// Log
							log.add("Server not found, try another address");
						}
						else if (ex instanceof MqttException)
						{
							// Some another Mqtt exception
							System.out.println("reason " + ((MqttException) ex).getReasonCode());
							System.out.println("msg " + ex.getMessage());
							System.out.println("loc " + ex.getLocalizedMessage());
							System.out.println("cause " + ex.getCause());
							System.out.println("excep " + ex);
							ex.printStackTrace();

							// Log
							log.add("Sara connection is not working");
						}
						else
						{
							// Main exception
							ex.printStackTrace();

							// Log
							log.add("Sara is not responding");
						}
					}
				}
				else if (saraStatus.equals(SaraStatus.CENTRAL_FOUND))
				{
					if (messagesToSend.size() > 0)
					{
						SaraMessage messageToSend = messagesToSend.remove(0);
						sampleClient.publish(messageToSend.getTopic(), messageToSend.getMqttMessage());
					}
				}

				// Sleeping for a better synchronization
				Thread.sleep(1000);
			}
		}
		catch (Exception ex)
		{

		}

	}

	public void start()
	{
		if (threadSara == null)
		{
			// Creating the server thread and running it
			threadSara = new Thread(this, threadName);
			threadSara.start();
		}

		// Log
		log.add("Sara started");
	}

	public void stop()
	{
		isRunning = false;
	}

	public void tryToFindSaraCentral()
	{
		saraStatus = SaraStatus.FINDING_CENTRAL;
	}

	public void sendThingId()
	{
		SaraMessage message = new MessageThingId(thingId);
		messagesToSend.add(message);
	}

	public void sendThingOperationsUrl()
	{
		SaraMessage message = new MessageOperationsUrl(operationsUrl);
		messagesToSend.add(message);
	}

	public void sendHandShakeConfirmation()
	{
		SaraMessage message = new MessageHandShakeConfirmation();
		messagesToSend.add(message);
	}

	public void sendSignal()
	{

	}

	public SaraLog getSaraLog()
	{
		return this.log;
	}

	/*
	 * Coordenates received messages
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception
	{
		// Check if it's sara commands
		if (topic.equals(SaraConstants.SARA_HANDSHAKE_TOPIC))
		{
			// HandShake requested

			if (message.toString().equals(SaraConstants.REQUEST_DEFAULT_MESSAGE))
			{
				// The HandShake was requested by the Sara server
				onHandShakeConfirmationRequestedEventHandler(new SaraEventArgs(""));
			}

		}
		else if (topic.equals(SaraConstants.SARA_URL_TOPIC))
		{
			// URL requested
			if (message.toString().equals(SaraConstants.REQUEST_DEFAULT_MESSAGE))
			{
				// The Url was requested by the Sara server
				onThingOperationsUrlRequestedEventHandler(new SaraEventArgs(""));
			}
		}

		// Log
		log.add("Message received from Sara Central: " + message.toString());
	}

	@Override
	public void connectionLost(Throwable cause)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		// TODO Auto-generated method stub

	}

	// Public methods for event handle
	public synchronized void addSaraEventsListener(ISaraEvent listener)
	{
		eventListeners.add(listener);
	}

	public synchronized void removeSaraEventsListener(ISaraEvent listener)
	{
		eventListeners.remove(listener);
	}

	// Private Methods
	// Private methods for event handle
	private synchronized void onCentralFoundEventHandler(SaraEventArgs e)
	{
		Sara sender = new Sara(this);
		Iterator<ISaraEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISaraEvent) i.next()).onCentralFound(sender, e);
		}
	}

	private synchronized void onHandShakeConfirmationRequestedEventHandler(SaraEventArgs e)
	{
		Sara sender = new Sara(this);
		Iterator<ISaraEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISaraEvent) i.next()).onHandShakeConfirmationRequested(sender, e);
		}
	}

	private synchronized void onThingIdRequestedEventHandler(SaraEventArgs e)
	{
		Sara sender = new Sara(this);
		Iterator<ISaraEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISaraEvent) i.next()).onThingIdRequested(sender, e);
		}
	}

	private synchronized void onThingOperationsUrlRequestedEventHandler(SaraEventArgs e)
	{
		Sara sender = new Sara(this);
		Iterator<ISaraEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISaraEvent) i.next()).onThingOperationsUrlRequested(sender, e);
		}
	}

	private synchronized void onSignalReceivedEventHandler(SaraEventArgs e)
	{
		Sara sender = new Sara(this);
		Iterator<ISaraEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISaraEvent) i.next()).onSignalReceived(sender, e);
		}
	}
}
