package sara.api;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import sara.api.communication.message.MessageFindCentral;
import sara.api.communication.message.MessageThingId;
import sara.api.handler.SaraEventArgs;
import sara.api.interfaces.IMessage;
import sara.api.interfaces.ISaraEvent;

public class Sara extends EventObject
{
	// Properties
	private static final long serialVersionUID = 4335218002356779457L;
	private List<ISaraEvent> eventListeners;
	private SaraStatus saraStatus;

	// Constructors
	public Sara(Object source)
	{
		// Call super constructor
		super(source);

		// New instances
		eventListeners = new ArrayList<ISaraEvent>();
		saraStatus = saraStatus.OFFLINE;
	}

	// Public methods
	public void start()
	{

	}

	public void stop()
	{
	}

	public void tryToFindSaraCentral()
	{
		// Eventually it will find the sara central
		onCentralFoundEventHandler(new SaraEventArgs(""));

		// Simulation for the handshake confirmation
		onHandShakeConfirmationRequestedEventHandler(new SaraEventArgs(""));
	}

	public void sendThingId(String id)
	{

	}

	public void sendThingOperationsUrl(String url)
	{
		// Supose to receive some signal
		onSignalReceivedEventHandler(new SaraEventArgs(""));

	}

	public void sendHandShakeConfirmation()
	{
		onThingIdRequestedEventHandler(new SaraEventArgs(""));
		onThingOperationsUrlRequestedEventHandler(new SaraEventArgs(""));
	}

	public void sendSignal()
	{

	}

	// Public methods for event handle
	public synchronized void addEventListener(ISaraEvent listener)
	{
		eventListeners.add(listener);
	}

	public synchronized void removeEventListener(ISaraEvent listener)
	{
		eventListeners.remove(listener);
	}

	// Private Methods
	private void sendMessage(IMessage message)
	{
		message.sendMessage();
	}

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
