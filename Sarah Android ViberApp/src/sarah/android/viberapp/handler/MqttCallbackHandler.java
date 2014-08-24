package sarah.android.viberapp.handler;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import sarah.android.viberapp.R;
import sarah.android.viberapp.model.Connection;
import sarah.android.viberapp.model.Connection.ConnectionStatus;
import sarah.android.viberapp.model.Connections;
import sarah.android.viberapp.model.Notify;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.widget.Toast;

public class MqttCallbackHandler implements MqttCallback
{

	/**
	 * {@link Context} for the application used to format and import external
	 * strings
	 **/
	private Context context;
	/**
	 * Client handle to reference the connection that this handler is attached
	 * to
	 **/
	private String clientHandle;

	/**
	 * Creates an <code>MqttCallbackHandler</code> object
	 * 
	 * @param context
	 *            The application's context
	 * @param clientHandle
	 *            The handle to a {@link Connection} object
	 */
	public MqttCallbackHandler(Context context, String clientHandle)
	{
		this.context = context;
		this.clientHandle = clientHandle;
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
	 */
	@Override
	public void connectionLost(Throwable cause)
	{
		// cause.printStackTrace();
		if (cause != null)
		{
			Connection c = Connections.getInstance(context).getConnection(clientHandle);
			c.addAction("Connection Lost");
			c.changeConnectionStatus(ConnectionStatus.DISCONNECTED);

			// format string to use a notification text
			Object[] args = new Object[2];
			args[0] = c.getId();
			args[1] = c.getHostName();

			String message = context.getString(R.string.connection_lost, args);

			// build intent
			Intent intent = new Intent();
			intent.setClassName(context, "org.eclipse.paho.android.service.sample.ConnectionDetails");
			intent.putExtra("handle", clientHandle);

			// notify the user
			Notify.notifcation(context, message, intent, R.string.notifyTitle_connectionLost);
		}
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String,
	 *      org.eclipse.paho.client.mqttv3.MqttMessage)
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception
	{
		// build intent
		Intent intent = new Intent();
		intent.setClassName(context, "org.eclipse.paho.android.service.sample.ConnectionDetails");
		intent.putExtra("handle", clientHandle);

		// // Get connection object associated with this object
		Connection connection = Connections.getInstance(context).getConnection(clientHandle);

		// Check if is handshake get, url get or other message
		if (topic.equals("/sarah/handshake/"))
		{
			// handshake get

			if (message.toString().equals("get"))
			{
				// notify the user
				// Notify.notifcation(context, "handshake", intent,
				// R.string.notifyTitle);

				MqttMessage messageHandshake = new MqttMessage(("confirmed").getBytes());

				MqttAndroidClient client = connection.getClient();
				try
				{
					client.publish("/sarah/handshake/", messageHandshake);
				}
				catch (MqttPersistenceException e)
				{
					e.printStackTrace();
				}
				catch (MqttException e)
				{
					e.printStackTrace();
				}
			}
		}
		else if (topic.equals("/sarah/url/"))
		{
			// url get

			if (message.toString().equals("get"))
			{
				// notify the user
				// Notify.notifcation(context, "url", intent,
				// R.string.notifyTitle);

				MqttMessage messageHandshake = new MqttMessage(("http://xpper.com/download/sarah/DeviceDemonstracaoAndroidCelular.json").getBytes());

				MqttAndroidClient client = connection.getClient();
				try
				{
					client.publish("/sarah/url/", messageHandshake);
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
			}
		}
		else
		{
			// other message type, general business logic type
			if (topic.equals("/viber/"))
			{
				viber();
			}
			else if (topic.equals("/ring/"))
			{
				ring();
			}
			else if (topic.equals("/show-time/"))
			{
				showTime();
			}

		}
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		// Do nothing
	}

	public void viber()
	{
		// // Get connection object associated with this object
		Connection connection = Connections.getInstance(context).getConnection(clientHandle);

		MqttAndroidClient client = connection.getClient();
		try
		{
			MqttMessage message = new MqttMessage(("").getBytes());
			client.publish("/viber-called/", message);
		}
		catch (MqttPersistenceException e)
		{

		}
		catch (MqttException e)
		{

		}

		Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(1500);
	}

	public void ring()
	{
		// // Get connection object associated with this object
		Connection connection = Connections.getInstance(context).getConnection(clientHandle);

		MqttAndroidClient client = connection.getClient();
		try
		{
			MqttMessage message = new MqttMessage(("").getBytes());
			client.publish("/ring-called/", message);
		}
		catch (MqttPersistenceException e)
		{

		}
		catch (MqttException e)
		{

		}

		try
		{
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(context, notification);
			r.play();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void showTime()
	{
		// // Get connection object associated with this object
		Connection connection = Connections.getInstance(context).getConnection(clientHandle);

		MqttAndroidClient client = connection.getClient();
		try
		{
			MqttMessage message = new MqttMessage(("").getBytes());
			client.publish("/show-time-called/", message);
		}
		catch (MqttPersistenceException e)
		{

		}
		catch (MqttException e)
		{

		}

		// Updating time
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());	
		Toast.makeText(context, calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
	}
}