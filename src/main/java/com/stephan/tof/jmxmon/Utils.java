package com.stephan.tof.jmxmon;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Utils {

	private Utils(){}
	
	public static String getHostNameForLinux() {
		try {
			return (InetAddress.getLocalHost()).getHostName();
		} catch (UnknownHostException uhe) {
			String host = uhe.getMessage(); // host = "hostname: hostname"
			if (host != null) {
				int colon = host.indexOf(':');
				if (colon > 0) {
					return host.substring(0, colon);
				}
			}
			return "UnknownHost";
		}
	}
	
}
