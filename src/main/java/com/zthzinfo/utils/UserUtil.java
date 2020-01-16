package com.zthzinfo.utils;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zthzinfo.beans.User;
import com.zthzinfo.beans.Webhook;

import java.util.*;
import java.util.stream.Collectors;

public class UserUtil {
	private static final Log log = LogFactory.get();
	public static Map<String, User> allUsers = new HashMap<>();

	public static void reloadUsers() {
		List<String> keys = ConfigUtil.configs.keySet().stream().filter(k -> k.matches("^users\\.[^.]*\\..*$")).sorted().collect(Collectors.toList());
		allUsers = new HashMap<>();

		if (keys == null || keys.size() == 0) {
			return;
		}
		for (String key : keys) {
			String[] keyArr = key.split("\\.");
			if (keyArr.length < 3) {
				continue;
			}
			String userName = keyArr[1];
			String propName = keyArr[2];

			User user = allUsers.get(userName);
			if (user == null) {
				user = new User();
				user.setName(userName);
				allUsers.put(userName, user);
			}
			String value = ConfigUtil.configs.get(key);
			if (propName.equals("mail")) {
				user.setMail(value);
			} else if (propName.equals("webhook") && value.matches("^[^.]*\\.[^.]*$")) {
				String[] webhookValueArr = value.split("\\.");
				String webhookName = webhookValueArr[0];
				String webhookUserId = webhookValueArr[1];
				Webhook webhookByName = WebhookUtil.getWebhookByName(webhookName);
				if (webhookByName == null) {
					log.warn("找不到名为：" + webhookName + "的网络通知配置");
					continue;
				}
				user.setWebhook(webhookByName);
				user.setWebhookUserid(webhookUserId);
			}


		}
	}

	public static User getUserByName(String name) {
		return allUsers.get(name);
	}

	public static List<User> getUsersByNames(String...names) {
		List<User> result = new ArrayList<>();
		for (String key : names) {
			result.add(allUsers.get(key));
		}
		return result;
	}
}
