package sarah.android.tools.discovery;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import sarah.android.R;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityDiscovery extends ActivityNet implements OnItemClickListener
{

	private final String TAG = "ActivityDiscovery";
	public final static long VIBRATE = (long) 250;
	public final static int SCAN_PORT_RESULT = 1;
	public static final int MENU_SCAN_SINGLE = 0;
	public static final int MENU_OPTIONS = 1;
	public static final int MENU_HELP = 2;
	private long network_ip = 0;
	private long network_start = 0;
	private long network_end = 0;
	private List<HostBean> hosts = null;
	private AbstractDiscovery mDiscoveryTask = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		LayoutInflater.from(ctxt);

		setInfo();
		startDiscovering();
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return false;
	}

	protected void setInfo()
	{
		// Get ip information
		network_ip = NetInfo.getUnsignedLongFromIp(net.ip);
		if (prefs.getBoolean(Prefs.KEY_IP_CUSTOM, Prefs.DEFAULT_IP_CUSTOM))
		{
			// Custom IP
			network_start = NetInfo.getUnsignedLongFromIp(prefs.getString(Prefs.KEY_IP_START, Prefs.DEFAULT_IP_START));
			network_end = NetInfo.getUnsignedLongFromIp(prefs.getString(Prefs.KEY_IP_END, Prefs.DEFAULT_IP_END));
		}
		else
		{
			// Custom CIDR
			if (prefs.getBoolean(Prefs.KEY_CIDR_CUSTOM, Prefs.DEFAULT_CIDR_CUSTOM))
			{
				net.cidr = Integer.parseInt(prefs.getString(Prefs.KEY_CIDR, Prefs.DEFAULT_CIDR));
			}
			// Detected IP
			int shift = (32 - net.cidr);
			if (net.cidr < 31)
			{
				network_start = (network_ip >> shift << shift) + 1;
				network_end = (network_start | ((1 << shift) - 1)) - 1;
			}
			else
			{
				network_start = (network_ip >> shift << shift);
				network_end = (network_start | ((1 << shift) - 1));
			}
			// Reset ip start-end (is it really convenient ?)
			Editor edit = prefs.edit();
			edit.putString(Prefs.KEY_IP_START, NetInfo.getIpFromLongUnsigned(network_start));
			edit.putString(Prefs.KEY_IP_END, NetInfo.getIpFromLongUnsigned(network_end));
			edit.commit();
		}
	}

	protected void cancelTasks()
	{
		if (mDiscoveryTask != null)
		{
			mDiscoveryTask.cancel(true);
			mDiscoveryTask = null;
		}
	}

	// Listen for Activity results
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case SCAN_PORT_RESULT:
			if (resultCode == RESULT_OK)
			{
				// Get scanned ports
				if (data != null && data.hasExtra(HostBean.EXTRA))
				{
					HostBean host = data.getParcelableExtra(HostBean.EXTRA);
					if (host != null)
					{
						hosts.set(host.position, host);
					}
				}
			}
		default:
			break;
		}
	}

	/**
	 * Discover hosts
	 */
	private void startDiscovering()
	{
		int method = 0;
		try
		{
			method = Integer.parseInt(prefs.getString(Prefs.KEY_METHOD_DISCOVER, Prefs.DEFAULT_METHOD_DISCOVER));
		}
		catch (NumberFormatException e)
		{
			Log.e(TAG, e.getMessage());
		}
		switch (method)
		{
		case 1:
			mDiscoveryTask = new DnsDiscovery(ActivityDiscovery.this);
			break;
		case 2:
			// Root
			break;
		case 0:
		default:
			mDiscoveryTask = new DefaultDiscovery(ActivityDiscovery.this);
		}

		// IP
		try
		{
			InetAddress testview = InetAddress.getByAddress(BigInteger.valueOf(network_ip).toByteArray());
			testview.getHostAddress();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}

		mDiscoveryTask.setNetwork(network_ip, network_start, network_end);
		mDiscoveryTask.execute();

		makeToast(R.string.discover_start);
		setProgressBarVisibility(true);
		setProgressBarIndeterminateVisibility(true);
		initList();
	}

	public void stopDiscovering()
	{
		// At this point, we have the ip's list
		Log.e(TAG, "stopDiscovering()");
		mDiscoveryTask = null;

		setProgressBarVisibility(false);
		setProgressBarIndeterminateVisibility(false);
	}

	private void initList()
	{
		hosts = new ArrayList<HostBean>();
	}

	public void addHost(HostBean host)
	{
		host.position = hosts.size();
		hosts.add(host);
	}

	public void makeToast(int msg)
	{
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void setButtons(boolean disable)
	{
		// TODO Auto-generated method stub

	}
}