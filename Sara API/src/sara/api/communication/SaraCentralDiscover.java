package sara.api.communication;

import java.util.EventObject;

import sara.api.handler.SaraCentralDiscoverPackageEventArgs;
import sara.api.interfaces.ISaraCentralDiscoverPackageEvent;

public class SaraCentralDiscover implements ISaraCentralDiscoverPackageEvent
{
	public SaraCentralDiscover()
	{

	}

	public void discover()
	{
		String baseDomain = "192.168.0.";
		for (int i = 1; i < 254; i++)
		{
			SaraCentralDiscoverPackage thread = new SaraCentralDiscoverPackage(this);
			thread.addEventsListener(this);

			thread.discover("SaraCentralDiscover-thread-" + i, baseDomain + i);
		}
	}

	@Override
	public void onSaraCentralDiscoverPackageCentralFound(EventObject sender, SaraCentralDiscoverPackageEventArgs e)
	{
		System.out.println(e.getArgs());
	}
}
