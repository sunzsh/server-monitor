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

}
