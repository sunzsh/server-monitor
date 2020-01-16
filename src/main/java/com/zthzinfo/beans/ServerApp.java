package com.zthzinfo.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerApp {

	public ServerApp(String ip, Integer port, String name) {
		this.ip = ip;
		this.port = port;
		this.name = name;
	}

	private String ip;
	private Integer port;
	private String name;
	private String affects;

	private List<User> users;


	private Date downTime;

}
