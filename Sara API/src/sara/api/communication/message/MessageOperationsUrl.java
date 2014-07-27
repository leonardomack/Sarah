package sara.api.communication.message;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import sara.api.tools.SaraConstants;

public class MessageOperationsUrl extends SaraMessage
{

	public MessageOperationsUrl(String operationsUrl)
	{
		MqttMessage message = new MqttMessage(operationsUrl.getBytes());
		message.setQos(2);

		super.setTopic(SaraConstants.SARA_URL_TOPIC);
		super.setMqttMessage(message);
	}
}