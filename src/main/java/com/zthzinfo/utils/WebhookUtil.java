package com.zthzinfo.utils;


import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zthzinfo.beans.ServerApp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class WebhookUtil {
	private WebhookUtil() {}
	private static String webhooks;
	private static String generateRegex(String word) {
		return String.format("\\{\\{\\s*%s\\s*\\}\\}", word.replaceAll("\\_", "\\\\_"));
	}

	private final static String W_MSG = "msg";
	private final static String W_DESC = "desc";
	private final static String W_SERVERNAME = "server_name";
	private final static String W_IP = "ip";
	private final static String W_PORT = "port";
	private final static String W_AFFECTS = "affects";
	private final static String W_DOWNTIME = "down_time";
	private final static String W_STATUS = "status";
	private final static String W_CURRENTTIME = "current_time";

	private static String replaceParam(String param, String value, String url) throws UnsupportedEncodingException {
		return url.replaceAll(generateRegex(param), URLEncoder.encode(value, "utf-8"));
	}


	private static void send(String msg, String desc, String timeNow, String status, ServerApp app, String url) {
		String finalUrl = null;
		try {
			finalUrl = replaceParam(W_IP, app.getIp(), url);
			finalUrl = replaceParam(W_PORT, app.getPort()+"", finalUrl);
			finalUrl = replaceParam(W_SERVERNAME, app.getName(), finalUrl);
			finalUrl = replaceParam(W_AFFECTS, app.getAffects(), finalUrl);
			finalUrl = replaceParam(W_STATUS, status, finalUrl);
			finalUrl = replaceParam(W_CURRENTTIME, timeNow, finalUrl);
			if (app.getDownTime() != null) {
				finalUrl = replaceParam(W_DOWNTIME, DateUtil.formatDateTime(app.getDownTime()), finalUrl);
			} else {
				finalUrl = replaceParam(W_DOWNTIME, "", finalUrl);
			}
			finalUrl = replaceParam(W_DESC, desc, finalUrl);
			finalUrl = replaceParam(W_MSG, msg, finalUrl);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			int canshuIndex = finalUrl.indexOf("?");
			String urlWithoutParam = canshuIndex >= 0 ? finalUrl.substring(0, canshuIndex) : finalUrl;
			String param = canshuIndex >= 0 ? finalUrl.substring(canshuIndex + 1) : "";

			String s = HttpRequest.post(urlWithoutParam).body(param).timeout(20000).execute().body();
			log.info("请求Webhook: {}   结果：{}", finalUrl.replaceAll("\n", "\\\\n"), s);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e, "请求Webhook异常: {}", finalUrl);
		}

	}
	private static final Log log = LogFactory.get();

	public static void send(String msg, String desc, String timeNow, String status, ServerApp app) {
		List<String> webhooks = ConfigUtil.getWebhooks();
		if (webhooks == null) {
			return;
		}
		for (String webhook : webhooks) {
			webhook = webhook.replaceAll("\\\\n", "\n");
			send(msg, desc, timeNow, status, app, webhook);
		}
	}


}
