package com.zthzinfo.service;

import cn.hutool.core.date.BetweenFormater;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zthzinfo.beans.ServerApp;
import com.zthzinfo.beans.User;
import com.zthzinfo.beans.Webhook;
import com.zthzinfo.utils.*;
import lombok.Getter;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ServerAppService {
	private static final Log log = LogFactory.get();


	@Getter
	private List<ServerApp> apps = new ArrayList<>();

	public void initApps() {
		log.info("---------初始化监控对象---------");
		apps.clear();
		for (String name : ConfigUtil.configs.getGroups()) {
			String ip = ConfigUtil.configs.getStr("ip", name, null);
			Integer port = ConfigUtil.configs.getInt("port", name);

			if (ip == null || port == null) {
				continue;
			}
			String affects = ConfigUtil.configs.getStr("affects", name, "[未说明]");
			String users = ConfigUtil.configs.getStr("users", name, null);
			String groups = ConfigUtil.configs.getStr("groups", name, null);
			Set<User> allUsers = new HashSet<>();
			if (users != null && users.trim().length() > 0) {
				String[] usersArr = users.split("\\s*,\\s*");
				List<User> usersByNames = UserUtil.getUsersByNames(usersArr);
				allUsers.addAll(usersByNames);
			}

			if (groups != null && groups.trim().length() > 0) {
				String[] groupsArr = groups.split("\\s*,\\s*");
				LinkedHashSet<User> usersByGroups = GroupUtil.getUsersByGroups(groupsArr);
				allUsers.addAll(usersByGroups);
			}


			ServerApp serverApp = new ServerApp(ip, port, name);
			serverApp.setAffects(affects);

			if (allUsers.size() > 0) {
				serverApp.setUsers(new ArrayList<>(allUsers));
			}

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
				} else {
					int minute = (int)(new Date().getTime() - app.getDownTime().getTime()) / 1000 / 60;
					if (minute % 5 == 0 && !Objects.equals(minute, app.getLastNotifyMinutesFromDownTime())) {
						app.setLastNotifyMinutesFromDownTime(minute);
						yichang(app);
					}

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

		Date timeNow = new Date();
		String timeNowStr = DateUtil.formatDateTime(timeNow);

		String title =  String.format("%s%s已恢复服务！本次累计停止服务%s", ConfigUtil.getApplicationName2(), app.getName(), formatBetween);
		String content = String.format("时间：%s\n服务：%s\nIP：%s\n端口：%s\n累计停止时间：%s\n影响服务：%s", timeNowStr, app.getName(), app.getIp(), app.getPort(), formatBetween, app.getAffects());

		startNotify(app, timeNowStr, title, content, "online");

		app.setDownTime(null);
		app.setLastNotifyMinutesFromDownTime(null);
	}

	public void yichang(ServerApp app) {
		if (app.getDownTime() != null) {
			return;
		}
		Date timeNow = new Date();
		String timeNowStr = DateUtil.formatDateTime(timeNow);

		String title =  String.format("%s%s已停止服务！请注意检查！", ConfigUtil.getApplicationName2(), app.getName());
		String content = String.format("时间：%s\n服务：%s\nIP：%s\n端口：%s\n影响服务：%s", timeNowStr , app.getName(), app.getIp(), app.getPort(), app.getAffects());


		startNotify(app, timeNowStr, title, content, "offline");

		app.setDownTime(new Date());
	}

	public void yichangAgain(ServerApp app, Integer minutes) {
		if (app.getDownTime() == null) {
			return;
		}
		Date timeNow = new Date();
		String timeNowStr = DateUtil.formatDateTime(timeNow);
		String downTimeStr = DateUtil.formatDateTime(app.getDownTime());

		String title =  String.format("%s%s已停止服务超过%s分钟了！请注意检查！", ConfigUtil.getApplicationName2(), app.getName(), minutes+"");
		String content = String.format("停止时间：%s\n服务：%s\nIP：%s\n端口：%s\n影响服务：%s", downTimeStr , app.getName(), app.getIp(), app.getPort(), app.getAffects());

		startNotify(app, timeNowStr, title, content, "offline");
	}

	private void startNotify(ServerApp app, String timeNowStr, String title, String content, String online) {
		try {
			log.info("发送邮件：\n{}\n{}\n--------------------------", title, content);
			List<String> userMails = app.getUsers().stream().filter(u -> StrUtil.isNotBlank(u.getMail())).map(User::getMail).collect(Collectors.toList());
			MailUtil.sendEMail(title, content, userMails, false);
		} catch (Exception e) {
			log.error("发送邮件失败", e);
		}


		try {

			List<Webhook> webhooks = app.getUsers().stream().map(User::getWebhook).distinct().filter(w -> w != null).collect(Collectors.toList());
			for (Webhook webhook : webhooks) {
				List<User> userGroup = app.getUsers().stream().filter(u -> Objects.equals(u.getWebhook(), webhook)).distinct().collect(Collectors.toList());
				WebhookUtil.send(title, content, timeNowStr, online, userGroup, webhook, app);
			}

		} catch (Exception e) {
			log.error("网络钩子回调失败", e);
		}
	}


}
