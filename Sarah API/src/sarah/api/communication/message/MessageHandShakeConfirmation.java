package sarah.api.communication.message;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import sarah.api.tools.SarahConstants;

public class MessageHandShakeConfirmation extends SarahMessage
{
	public MessageHandShakeConfirmation()
	{
		MqttMessage message = new MqttMessage(("confirmed").getBytes());
		message.setQos(2);

		super.setTopic(SarahConstants.SARA_HANDSHAKE_TOPIC);
		super.setMqttMessage(message);		
	}
}