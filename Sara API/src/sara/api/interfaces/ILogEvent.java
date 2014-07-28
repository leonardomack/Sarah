package sara.api.interfaces;

import java.util.EventObject;

import sara.api.handler.LogEventArgs;

public interface ILogEvent
{
	void onNewLogMessageAdded(EventObject sender, LogEventArgs e);
}
