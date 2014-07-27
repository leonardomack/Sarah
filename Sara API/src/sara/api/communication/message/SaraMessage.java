package sara.api.communication.message;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public abstract class SaraMessage
{
	private String topic;
	private MqttMessage mqttMessage;

	public SaraMessage()
	{
		topic = "";
		mqttMessage = new MqttMessage();
	}

	// Setters
	protected void setTopic(String topic)
	{
		this.topic = topic;
	}

	protected void setMqttMessage(MqttMessage mqttMessage)
	{
		this.mqttMessage = mqttMessage;
	}

	// Getters
	public String getTopic()
	{
		return topic;
	}

	public MqttMessage getMqttMessage()
	{
		return mqttMessage;
	}
}
