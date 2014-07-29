package sarah.api;

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

import sarah.api.communication.SarahCentralDiscover;
import sarah.api.communication.message.MessageHandShakeConfirmation;
import sarah.api.communication.message.MessageOperationsUrl;
import sarah.api.communication.message.MessageSignal;
import sarah.api.communication.message.MessageThingId;
import sarah.api.communication.message.SarahMessage;
import sarah.api.handler.LogEventArgs;
import sarah.api.handler.SarahEventArgs;
import sarah.api.interfaces.ILogEvent;
import sarah.api.interfaces.ISarahEvent;
import sarah.api.tools.SarahConstants;
import sarah.api.tools.SarahLog;
import sarah.api.tools.SarahStatus;

public class Sarah extends EventObject implements Runnable, MqttCallback
{
	// Properties
	private static final long serialVersionUID = 4335218002356779457L;
	private SarahLog log;
	private List<ISarahEvent> eventListeners;
	private SarahStatus sarahStatus;
	private Thread threadSarah;
	private String threadName;
	private Boolean isRunning;
	private List<SarahMessage> messagesToSend;
	private String thingId;
	private String operationsUrl;
	List<String> validIps;

	// Constructors
	public Sarah(Object sender)
	{
		// Call super constructor
		super(sender);

		// New instances and default values
		log = new SarahLog(this);
		eventListeners = new ArrayList<ISarahEvent>();
		sarahStatus = SarahStatus.OFFLINE;
		isRunning = false;
		threadName = SarahConstants.THREAD_SARA_NAME;
		messagesToSend = new LinkedList<SarahMessage>();
		thingId = "Device01";
		operationsUrl = "http://sara.com";

		// Log
		log.add("Sara created");
	}

	// Public methods
	@Override
	public void run()
	{
		// IP Variables loop
		String actualIpBroker = "";
		int actualIpBrokerIndex = 0;

		MemoryPersistence persistence = new MemoryPersistence();
		MqttConnectOptions connectionOptions = new MqttConnectOptions();
		connectionOptions.setCleanSession(true);
		isRunning = true;

		try
		{
			// Mqtt client
			MqttClient sampleClient = null;

			while (isRunning)
			{

				// Performing Sarah actions
				if (sarahStatus.equals(SarahStatus.FINDING_CENTRAL))
				{
					// Try next ip
					actualIpBroker = "tcp://" + validIps.get(actualIpBrokerIndex) + ":60001";

					// Get next good index for ip's list
					if ((validIps.size() - 1) == actualIpBrokerIndex)
					{
						actualIpBrokerIndex = 0;
					}
					else
					{
						actualIpBrokerIndex++;
					}

					// Setting configurations
					sampleClient = new MqttClient(actualIpBroker, thingId, persistence);

					try
					{
						// Log
						log.add("Try broker connection at: " + actualIpBroker);

						// Try to connect, if the server doesnt exists, it will
						// generate a ConnectException
						sampleClient.connect(connectionOptions);

						// Update internal status and run events
						sarahStatus = SarahStatus.CENTRAL_FOUND;

						// Log
						log.add("Connected at: " + actualIpBroker);
					}
					catch (Exception ex)
					{
						if (ex instanceof ConnectException || ex instanceof MqttException)
						{
							// Server not found, try another one
							// Main exception
							ex.printStackTrace();

							// Log
							log.add("Server not found, try another address");
						}
						else
						{
							// Main exception
							ex.printStackTrace();

							// Log
							log.add("Sarah connection is not working");
						}
					}
				}
				else if (sarahStatus.equals(SarahStatus.CENTRAL_FOUND))
				{
					// The server exists, register callback behavior
					sampleClient.setCallback(this);
					onCentralFoundEventHandler(new SarahEventArgs(""));

					// Update internal status and run events
					sarahStatus = SarahStatus.CONNECTED_AT_CENTRAL;

					// Log
					log.add("Sending and receiving behaviors completed");

				}
				else if (sarahStatus.equals(SarahStatus.CONNECTED_AT_CENTRAL))
				{
					if (messagesToSend.size() > 0)
					{
						SarahMessage messageToSend = messagesToSend.remove(0);
						sampleClient.publish(messageToSend.getTopic(), messageToSend.getMqttMessage());
					}
				}

				// Sleeping for a better synchronization
				Thread.sleep(100);
			}
		}
		catch (Exception ex)
		{

		}

	}

	public void start()
	{
		if (threadSarah == null)
		{
			// Creating the server thread and running it
			threadSarah = new Thread(this, threadName);
			threadSarah.start();
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
		// Needs to make the ip research and send sara signal to accept
		SarahCentralDiscover discover = new SarahCentralDiscover();
		this.validIps = discover.discover();

		if (validIps.size() == 0)
		{
			// 0 ips found, so we have no central
			// Log
			log.add("Central not found");
			return;
		}
		else
		{
			for (String validIp : validIps)
			{
				// Log
				log.add("Valid ip found: " + validIp);
			}

			sarahStatus = SarahStatus.FINDING_CENTRAL;
		}
	}

	public void sendThingId()
	{
		SarahMessage message = new MessageThingId(thingId);
		messagesToSend.add(message);
	}

	public void sendThingOperationsUrl()
	{
		SarahMessage message = new MessageOperationsUrl(operationsUrl);
		messagesToSend.add(message);
	}

	public void sendHandShakeConfirmation()
	{
		SarahMessage message = new MessageHandShakeConfirmation();
		messagesToSend.add(message);
	}

	public void sendSignal()
	{
		MqttMessage mqttMessage = new MqttMessage(("Detailed message").getBytes());
		mqttMessage.setQos(2);

		SarahMessage message = new MessageSignal("/sometopic/", mqttMessage);
		messagesToSend.add(message);
	}

	public SarahLog getSaraLog()
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
		if (topic.equals(SarahConstants.SARA_HANDSHAKE_TOPIC))
		{
			// HandShake requested

			if (message.toString().equals(SarahConstants.REQUEST_DEFAULT_MESSAGE))
			{
				// The HandShake was requested by the Sara server
				onHandShakeConfirmationRequestedEventHandler(new SarahEventArgs(""));
			}

		}
		else if (topic.equals(SarahConstants.SARA_URL_TOPIC))
		{
			// URL requested
			if (message.toString().equals(SarahConstants.REQUEST_DEFAULT_MESSAGE))
			{
				// The Url was requested by the Sara server
				onThingOperationsUrlRequestedEventHandler(new SarahEventArgs(""));
			}
		}

		// Log
		log.add("Message received from Sara Central: " + message.toString());
	}

	@Override
	public void connectionLost(Throwable cause)
	{
		// Update status
		sarahStatus = SarahStatus.OFFLINE;

		// Log
		log.add("Sara connection lost");
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{

	}

	// Public methods for event handle
	public synchronized void addSarahEventsListener(ISarahEvent listener)
	{
		eventListeners.add(listener);
	}

	public synchronized void removeSaraEventsListener(ISarahEvent listener)
	{
		eventListeners.remove(listener);
	}

	// Private Methods
	// Private methods for event handle
	private synchronized void onCentralFoundEventHandler(SarahEventArgs e)
	{
		Sarah sender = new Sarah(this);
		Iterator<ISarahEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISarahEvent) i.next()).onCentralFound(sender, e);
		}
	}

	private synchronized void onHandShakeConfirmationRequestedEventHandler(SarahEventArgs e)
	{
		Sarah sender = new Sarah(this);
		Iterator<ISarahEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISarahEvent) i.next()).onHandShakeConfirmationRequested(sender, e);
		}
	}

	private synchronized void onThingIdRequestedEventHandler(SarahEventArgs e)
	{
		Sarah sender = new Sarah(this);
		Iterator<ISarahEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISarahEvent) i.next()).onThingIdRequested(sender, e);
		}
	}

	private synchronized void onThingOperationsUrlRequestedEventHandler(SarahEventArgs e)
	{
		Sarah sender = new Sarah(this);
		Iterator<ISarahEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISarahEvent) i.next()).onThingOperationsUrlRequested(sender, e);
		}
	}

	private synchronized void onSignalReceivedEventHandler(SarahEventArgs e)
	{
		Sarah sender = new Sarah(this);
		Iterator<ISarahEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISarahEvent) i.next()).onSignalReceived(sender, e);
		}
	}
}
