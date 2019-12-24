package com.zthzinfo.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;

import java.util.List;

public class ConfigUtil {

	public static Setting configs = new Setting(FileUtil.getJarDir() + "/config.conf");


	private static String applicationName;
	public static String getApplicationName() {
		if (applicationName == null) {
			synchronized (ConfigUtil.class) {
				if (applicationName == null) {
					applicationName = configs.getStr("application.name");
				}
			}
		}
		return applicationName;
	}

	public static String getApplicationName2() {
		String applicationName = getApplicationName();
		if (StrUtil.isBlank(applicationName)) {
			return "";
		}
		return String.format("【" + applicationName + "】");
	}

	public static List<String> getUsers() {
		return CollUtil.newArrayList(configs.getStr("mail.to_users").split("\\s*,\\s*"));
	}
	public static List<String> getWebhooks() {
		String webhooks = configs.getStr("webhooks");
		if (webhooks == null || webhooks.trim().length() == 0) {
			return null;
		}

		return CollUtil.newArrayList(webhooks.split("\\s*,\\s*"));
	}
}
