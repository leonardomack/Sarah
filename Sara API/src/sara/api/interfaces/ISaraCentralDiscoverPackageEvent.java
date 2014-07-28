package sara.api.interfaces;

import java.util.EventObject;

import sara.api.handler.SaraCentralDiscoverPackageEventArgs;

public interface ISaraCentralDiscoverPackageEvent
{
	void onSaraCentralDiscoverPackageCentralFound(EventObject sender, SaraCentralDiscoverPackageEventArgs e);
}
