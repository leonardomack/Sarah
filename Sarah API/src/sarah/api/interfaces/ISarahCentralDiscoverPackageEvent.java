package sarah.api.interfaces;

import java.util.EventObject;

import sarah.api.handler.SarahCentralDiscoverPackageEventArgs;

public interface ISarahCentralDiscoverPackageEvent
{
	void onSaraCentralDiscoverPackageResult(EventObject sender, SarahCentralDiscoverPackageEventArgs e);
}
