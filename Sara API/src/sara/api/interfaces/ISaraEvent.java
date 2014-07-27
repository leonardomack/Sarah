package sara.api.interfaces;

import java.util.EventObject;

import sara.api.handler.SaraEventArgs;

public interface ISaraEvent
{
	void onCentralFound(EventObject sender, SaraEventArgs e);

	void onHandShakeConfirmationRequested(EventObject sender, SaraEventArgs e);

	void onThingIdRequested(EventObject sender, SaraEventArgs e);

	void onThingOperationsUrlRequested(EventObject sender, SaraEventArgs e);

	void onSignalReceived(EventObject sender, SaraEventArgs e);

}
