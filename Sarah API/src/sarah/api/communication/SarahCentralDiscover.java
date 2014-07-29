package sarah.api.communication;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import sarah.api.handler.SarahCentralDiscoverPackageEventArgs;
import sarah.api.interfaces.ISarahCentralDiscoverPackageEvent;

public class SarahCentralDiscover implements ISarahCentralDiscoverPackageEvent
{
	private List<String> potentialIpClients;
	private List<String> validIpClients;

	public SarahCentralDiscover()
	{
		potentialIpClients = new ArrayList<String>();
		validIpClients = new ArrayList<String>();

		String baseDomain = "192.168.0.";
		for (int i = 1; i < 254; i++)
		{
			potentialIpClients.add(baseDomain + i);
		}
	}

	public List<String> discover()
	{
		for (String ip : potentialIpClients)
		{
			SarahCentralDiscoverPackage thread = new SarahCentralDiscoverPackage(this);
			thread.addEventsListener(this);

			// Start the discover command
			thread.discover("SaraCentralDiscover-thread-" + ip, ip);
		}

		// Wait while clients needs to answer
		try
		{
			Thread.sleep(10000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		return validIpClients;
	}

	@Override
	public void onSaraCentralDiscoverPackageResult(EventObject sender, SarahCentralDiscoverPackageEventArgs e)
	{
		if (e.getIsIpReachable())
		{
			validIpClients.add(e.getIp());
		}		
	}
}
