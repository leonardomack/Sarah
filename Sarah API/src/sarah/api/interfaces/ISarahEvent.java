package sarah.api.interfaces;

import java.util.EventObject;

import sarah.api.handler.SarahEventArgs;

public interface ISarahEvent
{
	void onCentralFound(EventObject sender, SarahEventArgs e);

	void onHandShakeConfirmationRequested(EventObject sender, SarahEventArgs e);

	void onThingIdRequested(EventObject sender, SarahEventArgs e);

	void onThingOperationsUrlRequested(EventObject sender, SarahEventArgs e);

	void onSignalReceived(EventObject sender, SarahEventArgs e);

}
