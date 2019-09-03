package com.zthzinfo.utils;

import org.apache.commons.net.telnet.TelnetClient;

public class TelnetUtil {

	public static TelnetClient getTelnetClient() {
		TelnetClient telnet = new TelnetClient("VT220");
		telnet.setDefaultTimeout(5000); //socket延迟时间：5000ms
		return telnet;
	}
}
