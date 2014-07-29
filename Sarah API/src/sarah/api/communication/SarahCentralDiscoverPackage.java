package sarah.api.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import sarah.api.handler.SarahCentralDiscoverPackageEventArgs;
import sarah.api.interfaces.ISarahCentralDiscoverPackageEvent;

public class SarahCentralDiscoverPackage extends EventObject implements Runnable
{
	private static final long serialVersionUID = -4922259577692743874L;
	private String threadName;
	private Thread thread;
	private String ipToDiscover;
	private List<ISarahCentralDiscoverPackageEvent> eventListeners;

	public SarahCentralDiscoverPackage(Object sender)
	{
		super(sender);

		this.threadName = "";
		this.ipToDiscover = "";
		eventListeners = new ArrayList<ISarahCentralDiscoverPackageEvent>();
	}

	@Override
	public void run()
	{
		try
		{
			if (InetAddress.getByName(ipToDiscover).isReachable(5000))
			{
				// Ip ok
				onSaraCentralDiscoverPackageResultEventHandler(new SarahCentralDiscoverPackageEventArgs(ipToDiscover, true));
			}
			else
			{
				// Ip not ok
				onSaraCentralDiscoverPackageResultEventHandler(new SarahCentralDiscoverPackageEventArgs(ipToDiscover, false));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
			// Ip not ok
			onSaraCentralDiscoverPackageResultEventHandler(new SarahCentralDiscoverPackageEventArgs(ipToDiscover, false));
		}

	}

	public void discover(String threadName, String ipToDiscover)
	{
		this.threadName = threadName;
		this.ipToDiscover = ipToDiscover;

		if (thread == null)
		{
			thread = new Thread(this, threadName);
			thread.start();
		}
	}

	public String getThreadName()
	{
		return threadName;
	}

	public String getIpToDiscover()
	{
		return ipToDiscover;
	}

	public synchronized void addEventsListener(ISarahCentralDiscoverPackageEvent listener)
	{
		eventListeners.add(listener);
	}

	public synchronized void removeEventsListener(ISarahCentralDiscoverPackageEvent listener)
	{
		eventListeners.remove(listener);
	}

	private synchronized void onSaraCentralDiscoverPackageResultEventHandler(SarahCentralDiscoverPackageEventArgs e)
	{
		SarahCentralDiscoverPackage sender = new SarahCentralDiscoverPackage(this);
		Iterator<ISarahCentralDiscoverPackageEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISarahCentralDiscoverPackageEvent) i.next()).onSaraCentralDiscoverPackageResult(sender, e);
		}
	}
}
