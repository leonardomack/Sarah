package sara.desktop.model;

import java.util.EventObject;

import sara.api.Sara;
import sara.api.handler.SaraEventArgs;
import sara.api.interfaces.ISaraEvent;

public class Device implements ISaraEvent
{
	Sara sara;

	public Device()
	{
		sara = new Sara(this);
		sara.addSaraEventsListener(this);

		sara.start();
		sara.tryToFindSaraCentral();
		sara.stop();
		
		System.exit(0);
	}

	@Override
	public void onCentralFound(EventObject sender, SaraEventArgs e)
	{
		System.out.println("=== onCentralFound ===");
	}

	@Override
	public void onHandShakeConfirmationRequested(EventObject sender, SaraEventArgs e)
	{
		System.out.println("=== onHandShakeConfirmationRequested ===");
		sara.sendHandShakeConfirmation();
	}

	@Override
	public void onThingIdRequested(EventObject sender, SaraEventArgs e)
	{
		System.out.println("=== onThingIdRequested ===");
		sara.sendThingId();
	}

	@Override
	public void onThingOperationsUrlRequested(EventObject sender, SaraEventArgs e)
	{
		System.out.println("=== onThingOperationsUrlRequested ===");
		sara.sendThingOperationsUrl();
	}

	@Override
	public void onSignalReceived(EventObject sender, SaraEventArgs e)
	{
		System.out.println("=== onSignalReceived ===");
	}

}
