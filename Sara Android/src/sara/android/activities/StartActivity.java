package sara.android.activities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Map;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import sara.android.R;
import sara.android.connection.Connection;
import sara.android.connection.Connection.ConnectionStatus;
import sara.android.connection.Connections;
import sara.android.handler.MqttCallbackHandler;
import sara.android.listener.ActionListener;
import sara.android.resources.ActivityConstants;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StartActivity extends ActionBarActivity
{
	// Private vars
	private ArrayAdapter<Connection> arrayAdapter = null;
	private ChangeListener changeListener = new ChangeListener();
	private StartActivity startActivity = this;

	// View Controls
	private TextView textViewStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Default start actions by android
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		// Default start action by Sara
		runDefaultStartActions();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
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

	private void runDefaultStartActions()
	{
		// Find components
		textViewStatus = (TextView) findViewById(R.id.ActivityStart_TextView_Status);

		// Setting parameters
		textViewStatus.setText(new Date().toString());
		arrayAdapter = new ArrayAdapter<Connection>(this, R.layout.abc_action_bar_view_list_nav_layout);

		// get all the available connections
		Map<String, Connection> connections = Connections.getInstance(this).getConnections();

		if (connections != null)
		{
			for (String s : connections.keySet())
			{
				arrayAdapter.add(connections.get(s));
			}
		}

		connectToSaraCentral();
	}

	private void connectToSaraCentral()
	{
		MqttConnectOptions conOpt = new MqttConnectOptions();

		// The basic client information
		String clientId = "Device01";
		String server = "192.168.0.104";
		int port = ActivityConstants.defaultPort;
		Boolean cleanSession = false;
		Boolean isSsl = false;
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

		MqttAndroidClient client;
		client = Connections.getInstance(this).createClient(this, uri, clientId);
		// create a client handle
		String clientHandle = uri + clientId;

		// last will message
		String message = "";
		String topic = "";
		Integer qos = 0;
		Boolean retained = false;

		// connection options

		String username = "";

		String password = "";

		int timeout = 1000;
		int keepalive = 10;

		Connection connection = new Connection(clientHandle, clientId, server, port, this, client, isSsl);
		arrayAdapter.add(connection);

		connection.registerChangeListener(changeListener);
		// connect client

		String[] actionArgs = new String[1];
		actionArgs[0] = clientId;
		connection.changeConnectionStatus(ConnectionStatus.CONNECTING);

		conOpt.setCleanSession(cleanSession);
		conOpt.setConnectionTimeout(timeout);
		conOpt.setKeepAliveInterval(keepalive);
		if (!username.equals(ActivityConstants.empty))
		{
			conOpt.setUserName(username);
		}
		if (!password.equals(ActivityConstants.empty))
		{
			conOpt.setPassword(password.toCharArray());
		}

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
			startActivity.runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					startActivity.arrayAdapter.notifyDataSetChanged();
				}

			});

		}

	}
}
