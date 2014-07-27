package sara.api.communication.message;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import sara.api.tools.SaraConstants;

public class MessageHandShakeConfirmation extends SaraMessage
{
	public MessageHandShakeConfirmation()
	{
		MqttMessage message = new MqttMessage(("confirmed").getBytes());
		message.setQos(2);

		super.setTopic(SaraConstants.SARA_HANDSHAKE_TOPIC);
		super.setMqttMessage(message);		
	}
}