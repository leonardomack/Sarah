package sarah.api.communication.message;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import sarah.api.tools.SarahConstants;

public class MessageOperationsUrl extends SarahMessage
{

	public MessageOperationsUrl(String operationsUrl)
	{
		MqttMessage message = new MqttMessage(operationsUrl.getBytes());
		message.setQos(2);

		super.setTopic(SarahConstants.SARAH_URL_TOPIC);
		super.setMqttMessage(message);
	}
}