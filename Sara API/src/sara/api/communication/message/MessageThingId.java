package sara.api.communication.message;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import sara.api.tools.SaraConstants;

public class MessageThingId extends SaraMessage
{
	public MessageThingId(String thingId)
	{
		MqttMessage message = new MqttMessage(thingId.getBytes());
		message.setQos(2);

		super.setTopic(SaraConstants.SARA_ROOT_TOPIC);
		super.setMqttMessage(message);
	}

}