package sarah.android;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import sarah.android.handler.MqttCallbackHandler;
import sarah.android.model.ActionListener;
import sarah.android.model.Connection;
import sarah.android.model.Connections;
import sarah.android.model.Connection.ConnectionStatus;
import sarah.android.tools.ActivityConstants;
import sarah.android.tools.ServiceDiscovery;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StartDiscoveryActivity extends ActionBarActivity implements OnClickListener
{
	private Integer exitPressCounter;
	private StartDiscoveryActivity clientConnections;
	private Connection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Loading default informations
		loadDefaultInformations();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		if (exitPressCounter < 1)
		{
			// Not pressed 2 times yet
			exitPressCounter++;

			// Show exit message confirmation
			Toast.makeText(this, R.string.activity_main_exit_text, Toast.LENGTH_SHORT).show();

			Handler handlerNewPage = new Handler();
			handlerNewPage.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					exitPressCounter = 0;
				}

			}, 4000);// 4 secs to reset counter
		}
		else
		{
			// Leaving
			finish();
		}
	}

	private void loadDefaultInformations()
	{
		// Default values
		exitPressCounter = 0;
		clientConnections = this;

		// Default behaviors
		Button btnConnect = (Button) findViewById(R.id.btnConnect);
		Button btnSendHandshake = (Button) findViewById(R.id.btnSendHandshake);
		Button btnSendUrl = (Button) findViewById(R.id.btnSendUrl);
		Button btnSendSignal = (Button) findViewById(R.id.btnSendSignal);

		btnConnect.setOnClickListener(this);
		btnSendHandshake.setOnClickListener(this);
		btnSendUrl.setOnClickListener(this);
		btnSendSignal.setOnClickListener(this);
	}

	// Implement the OnClickListener callback
	public void onClick(View v)
	{
		// Try to discover some IPs
		//new RetrieveFeedTask().execute("");

		Button clickedButton = (Button) v;

		switch (clickedButton.getId())
		{
		case R.id.btnConnect:
		{

			// The basic client information
			// Generate device's name
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			int hours = calendar.get(Calendar.HOUR_OF_DAY);
			int minutes = calendar.get(Calendar.MINUTE);
			int seconds = calendar.get(Calendar.SECOND);
			String deviceName = "Device-" + hours + minutes + seconds;

			// Connection info
			String server = "192.168.0.102";
			String clientId = deviceName;
			int port = 60001;
			boolean cleanSession = false;
			boolean isSsl = false;
			String uri = null;

			if (isSsl)
			{
				Log.e("SSLConnection", "Doing an SSL Connect");
				uri = "ssl://";

			}
			else
			{
				uri = "tcp://";
			}

			uri = uri + server + ":" + port;

			MqttAndroidClient client = new MqttAndroidClient(this, uri, clientId);
			// create a client handle
			String clientHandle = uri + clientId;

			// last will message
			String message = "message test";
			String topic = "/sarah/topictest";
			Integer qos = 2;
			Boolean retained = false;

			// connection options
			String username = "";
			String password = "";
			int timeout = 1000;
			int keepalive = 10;

			MqttConnectOptions conOpt = new MqttConnectOptions();
			conOpt.setCleanSession(cleanSession);
			conOpt.setConnectionTimeout(timeout);
			conOpt.setKeepAliveInterval(keepalive);
			if (!username.equals(""))
			{
				conOpt.setUserName(username);
			}
			if (!password.equals(""))
			{
				conOpt.setPassword(password.toCharArray());
			}

			// arrayAdapter.add(connection);
			ChangeListener changeListener = new ChangeListener();

			connection = new Connection(clientHandle, clientId, server, port, this, client, isSsl);
			connection.registerChangeListener(changeListener);
			// connect client

			String[] actionArgs = new String[1];
			actionArgs[0] = clientId;
			connection.changeConnectionStatus(ConnectionStatus.CONNECTING);

			final ActionListener callback = new ActionListener(this, ActionListener.Action.CONNECT, clientHandle, actionArgs);

			boolean doConnect = true;

			if ((!message.equals(ActivityConstants.empty)) || (!topic.equals(ActivityConstants.empty)))
			{
				// need to make a message since last will is set
				try
				{
					conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
				}
				catch (Exception e)
				{
					Log.e(this.getClass().getCanonicalName(), "Exception Occured", e);
					doConnect = false;
					callback.onFailure(null, e);
				}
			}
			client.setCallback(new MqttCallbackHandler(this, clientHandle));
			connection.addConnectionOptions(conOpt);
			Connections.getInstance(this).addConnection(connection);
			if (doConnect)
			{
				try
				{
					client.connect(conOpt, null, callback);
				}
				catch (MqttException e)
				{
					Log.e(this.getClass().getCanonicalName(), "MqttException Occured", e);
				}
			}

			break;
		}
		case R.id.btnSendHandshake:
		{
			MqttMessage message = new MqttMessage(("confirmed").getBytes());

			MqttAndroidClient client = connection.getClient();
			try
			{
				client.publish("/sarah/handshake/", message);
			}
			catch (MqttPersistenceException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (MqttException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		case R.id.btnSendUrl:
		{
			MqttMessage message = new MqttMessage(("http://xpper.com/download/sarah/MobileDevice01.json").getBytes());

			MqttAndroidClient client = connection.getClient();
			try
			{
				client.publish("/sarah/url/", message);
			}
			catch (MqttPersistenceException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (MqttException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		case R.id.btnSendSignal:
		{
			MqttMessage message = new MqttMessage(("whatever").getBytes());

			MqttAndroidClient client = connection.getClient();
			try
			{
				client.publish("/app/lorem", message);
			}
			catch (MqttPersistenceException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (MqttException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		default:
		{
			break;
		}
		}

	}

	class RetrieveFeedTask extends AsyncTask<String, Void, Void>
	{
		public String s_dns1;
		public String s_dns2;
		public String s_gateway;
		public String s_ipAddress;
		public String s_leaseDuration;
		public String s_netmask;
		public String s_serverAddress;
		TextView info;
		DhcpInfo d;
		WifiManager wifii;

		protected Void doInBackground(String... urls)
		{
			try
			{
				wifii = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				d = wifii.getDhcpInfo();

				s_dns1 = "DNS 1: " + String.valueOf(d.dns1);
				s_dns2 = "DNS 2: " + String.valueOf(d.dns2);
				s_gateway = "Default Gateway: " + String.valueOf(d.gateway);
				s_ipAddress = "IP Address: " + String.valueOf(d.ipAddress);
				s_leaseDuration = "Lease Time: " + String.valueOf(d.leaseDuration);
				s_netmask = "Subnet Mask: " + String.valueOf(d.netmask);
				s_serverAddress = "Server IP: " + String.valueOf(d.serverAddress);

				String connections = "";
				InetAddress host;
				try
				{
					int ipAddress = d.dns1;
					byte[] bytes = BigInteger.valueOf(ipAddress).toByteArray();

					host = InetAddress.getByAddress(bytes);
					byte[] ip = host.getAddress();

					for (int i = 1; i <= 254; i++)
					{
						ip[3] = (byte) i;
						InetAddress address = InetAddress.getByAddress(ip);
						if (address.isReachable(100))
						{
							System.out.println(address + " machine is turned on and can be pinged");
							connections += address + "\n";
						}
						else if (!address.getHostAddress().equals(address.getHostName()))
						{
							System.out.println(address + " machine is known in a DNS lookup");
						}

					}
				}
				catch (UnknownHostException e1)
				{
					e1.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				System.out.println(connections);
			}
			catch (Exception e)
			{
				return null;
			}
			return null;
		}

		protected void onPostExecute(Void feed)
		{
			// TODO: check this.exception
			// TODO: do something with the feed
		}
	}

	private class ChangeListener implements PropertyChangeListener
	{

		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(PropertyChangeEvent event)
		{

			if (!event.getPropertyName().equals(ActivityConstants.ConnectionStatusProperty))
			{
				return;
			}
			clientConnections.runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					// clientConnections.arrayAdapter.notifyDataSetChanged();
				}

			});

		}

	}

}
