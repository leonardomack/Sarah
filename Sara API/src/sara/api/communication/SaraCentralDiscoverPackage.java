package sara.api.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import sara.api.handler.SaraCentralDiscoverPackageEventArgs;
import sara.api.interfaces.ISaraCentralDiscoverPackageEvent;

public class SaraCentralDiscoverPackage extends EventObject implements Runnable
{
	private static final long serialVersionUID = -4922259577692743874L;
	private String threadName;
	private Thread thread;
	private String ipToDiscover;
	private List<ISaraCentralDiscoverPackageEvent> eventListeners;

	public SaraCentralDiscoverPackage(Object sender)
	{
		super(sender);

		this.threadName = "";
		this.ipToDiscover = "";
		eventListeners = new ArrayList<ISaraCentralDiscoverPackageEvent>();
	}

	@Override
	public void run()
	{
		try
		{
			if (InetAddress.getByName(ipToDiscover).isReachable(10000))
			{
				// Ip ok
				onCentralFoundEventHandler(new SaraCentralDiscoverPackageEventArgs("IP found " + ipToDiscover));
			}

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public synchronized void addEventsListener(ISaraCentralDiscoverPackageEvent listener)
	{
		eventListeners.add(listener);
	}

	public synchronized void removeEventsListener(ISaraCentralDiscoverPackageEvent listener)
	{
		eventListeners.remove(listener);
	}

	private synchronized void onCentralFoundEventHandler(SaraCentralDiscoverPackageEventArgs e)
	{
		SaraCentralDiscoverPackage sender = new SaraCentralDiscoverPackage(this);
		Iterator<ISaraCentralDiscoverPackageEvent> i = eventListeners.iterator();
		while (i.hasNext())
		{
			((ISaraCentralDiscoverPackageEvent) i.next()).onSaraCentralDiscoverPackageCentralFound(sender, e);
		}
	}
}
