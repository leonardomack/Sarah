package sarah.api.communication.message;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MessageSignal extends SarahMessage
{

	public MessageSignal(String topic, MqttMessage message)
	{
		super.setTopic(topic);
		super.setMqttMessage(message);
	}

}