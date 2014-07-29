package sarah.api.handler;

public class SarahCentralDiscoverPackageEventArgs
{
	// Properties
	private String ip;
	private Boolean isIpReachable;

	// Constructors
	public SarahCentralDiscoverPackageEventArgs(String ip, Boolean isIpReachable)
	{
		this.ip = ip;
		this.isIpReachable = isIpReachable;
	}

	// Getters
	public String getIp()
	{
		return ip;
	}

	public Boolean getIsIpReachable()
	{
		return isIpReachable;
	}
}
