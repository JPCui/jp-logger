package cn.cjp.logger.model;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Host implements AbstractModel {

	private String hostAddress;

	private String hostName;

	public Host() {
	}

	public Host(String hostAddress, String hostName) {
		this.setHostAddress(hostAddress);
		this.setHostName(hostName);
	}

	public static Host instance() throws UnknownHostException {
		Host host = new Host();
		InetAddress address = InetAddress.getLocalHost();
		host.setHostAddress(address.getHostAddress());
		host.setHostName(address.getHostName());
		return host;
	}

	public static Host parse(InetAddress address) {
		Host host = new Host();
		host.setHostAddress(address.getHostAddress());
		host.setHostName(address.getHostName());
		return host;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	public DBObject toDBObject() {
		DBObject dbo = new BasicDBObject();
		dbo.put("hostAddress", hostAddress);
		dbo.put("hostName", hostName);
		return dbo;
	}

	public static Host fromDBObject(DBObject dbo) {
		if (!dbo.containsField("host")) {
			return null;
		}
		String hostAddress = ((DBObject) dbo.get("host")).get("hostAddress").toString();
		String hostName = ((DBObject) dbo.get("host")).get("hostName").toString();

		Host host = new Host();
		host.setHostAddress(hostAddress);
		host.setHostName(hostName);
		return host;
	}

}
