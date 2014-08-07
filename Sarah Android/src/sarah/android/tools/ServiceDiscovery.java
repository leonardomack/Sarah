package sarah.android.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.util.Log;

public class ServiceDiscovery extends Activity
{
	

	public void discover()
	{
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress())
					{
						String Ip = inetAddress.getHostAddress().toString();
						System.out.println(Ip);
					}
				}
			}

		}
		catch (SocketException obj)
		{
			Log.e("Error occurred during IP fetching: ", obj.toString());
		}

	}
}
