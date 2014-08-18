package sarah.api.communication.message;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import sarah.api.tools.SarahConstants;

public class MessageThingId extends SarahMessage
{
	public MessageThingId(String thingId)
	{
		MqttMessage message = new MqttMessage(thingId.getBytes());
		message.setQos(2);

		super.setTopic(SarahConstants.SARAH_ROOT_TOPIC);
		super.setMqttMessage(message);
	}

}