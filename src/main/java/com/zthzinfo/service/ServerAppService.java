package com.zthzinfo.service;

import cn.hutool.core.date.BetweenFormater;
import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zthzinfo.beans.ServerApp;
import com.zthzinfo.utils.ConfigUtil;
import com.zthzinfo.utils.MailUtil;
import com.zthzinfo.utils.TelnetUtil;
import lombok.Getter;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.util.*;

public class ServerAppService {
	private static final Log log = LogFactory.get();


	@Getter
	private List<ServerApp> apps = new ArrayList<>();

	public void initApps() {
		log.info("---------初始化监控对象---------");
		apps.clear();
		Set<String> appKeys = new LinkedHashSet<>();

		for (String name : ConfigUtil.configs.getGroups()) {
			String ip = ConfigUtil.configs.getStr("ip", name, null);
			Integer port = ConfigUtil.configs.getInt("port", name);

			if (ip == null || port == null) {
				continue;
			}

			ServerApp serverApp = new ServerApp(ip, port, name);

			apps.add(serverApp);
			log.info(String.format("%s\t\t%s:%s", name, ip, port+""));

		}
		log.info("--------------------------------");

	}

	public void checkApps() {
		for (ServerApp app : this.getApps()) {

			TelnetClient telnetClient = TelnetUtil.getTelnetClient();
			try {
				telnetClient.connect(app.getIp(), app.getPort());
				telnetClient.disconnect();
				log.info("{}:服务正常 [{}:{}]", app.getName(), app.getIp(), app.getPort());
				if (app.getDownTime() != null) {
					huifu(app);
				}
			} catch (IOException e) {
				log.info("{}:服务异常 [{}:{}]", app.getName(), app.getIp(), app.getPort());
				if (app.getDownTime() == null) {
					yichang(app);
				}
			}
		}
	}

	public void huifu(ServerApp app) {
		if (app.getDownTime() == null) {
			return;
		}
		long between =  new Date().getTime() - app.getDownTime().getTime();
		String formatBetween = DateUtil.formatBetween(between, BetweenFormater.Level.SECOND);

		String title =  String.format("%s%s已恢复服务！本次累计停止服务%s", ConfigUtil.getApplicationName2(), app.getName(), formatBetween);
		String content = String.format("服务:%s\nIP:%s\n端口:%s\n累计停止时间:%s", app.getName(), app.getIp(), app.getPort(), formatBetween);


		log.info("发送邮件：\n{}\n{}\n--------------------------", title, content);
		MailUtil.sendEMail(title, content, ConfigUtil.getUsers(), false);

		app.setDownTime(null);
	}

	public void yichang(ServerApp app) {
		if (app.getDownTime() != null) {
			return;
		}

		String title =  String.format("%s%s已停止服务！请注意检查！", ConfigUtil.getApplicationName2(), app.getName());
		String content = String.format("服务:%s\nIP:%s\n端口:%s", app.getName(), app.getIp(), app.getPort());


		log.info("发送邮件：\n{}\n{}\n--------------------------", title, content);
		MailUtil.sendEMail(title, content, ConfigUtil.getUsers(), false);

		app.setDownTime(new Date());
	}


}
