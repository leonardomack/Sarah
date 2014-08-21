package sarah.android.viberapp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import sarah.android.viberapp.handler.MqttCallbackHandler;
import sarah.android.viberapp.model.ActionListener;
import sarah.android.viberapp.model.Connection;
import sarah.android.viberapp.model.Connection.ConnectionStatus;
import sarah.android.viberapp.model.Connections;
import sarah.android.viberapp.tools.ActivityConstants;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity
{
	// private static final String SARAH_SERVER_IP = "173.176.42.176";
	private static final String SARAH_SERVER_IP = "192.168.0.102";

	private Integer exitPressCounter;
	private MainActivity clientConnections;
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

	private void loadDefaultInformations()
	{
		// Default values
		exitPressCounter = 0;
		clientConnections = this;

		new Thread(new Runnable()
		{
			public void run()
			{
				startSarahCommunication();

				while (connection.isConnectedOrConnecting() == false)
				{
					try
					{
						Thread.sleep(1000);
						startSarahCommunication();
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	private void startSarahCommunication()
	{
		// The basic client information
		// Generate device's name
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		String deviceName = "Android-" + hours + minutes + seconds;

		// Connection info
		String server = SARAH_SERVER_IP;
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
