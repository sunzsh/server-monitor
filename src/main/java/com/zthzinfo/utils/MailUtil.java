package com.zthzinfo.utils;

import cn.hutool.extra.mail.MailAccount;

import java.util.List;

public class MailUtil {
	private MailUtil() {}
	private static MailAccount account;

	public static MailAccount getAccount() {
		if (account == null) {
			synchronized (MailUtil.class) {
				if (account == null) {
					account = new MailAccount();
					account.setHost(ConfigUtil.configs.getStr("mail.host"));
					account.setPort(ConfigUtil.configs.getInt("mail.port"));
					account.setSslEnable(ConfigUtil.configs.getBool("mail.sslEnable"));
					account.setFrom(ConfigUtil.configs.getStr("mail.from"));

					Boolean auth = ConfigUtil.configs.getBool("mail.auth");
					account.setAuth(auth);
					if (auth) {
						account.setUser(ConfigUtil.configs.getStr("mail.user"));
						account.setPass(ConfigUtil.configs.getStr("mail.pass"));
					}
				}
			}
		}
		return account;
	}


	public static void sendEMail(String title, String body, List<String> mails , boolean isHtml) {
		MailAccount account = getAccount();
		cn.hutool.extra.mail.MailUtil.send(account, mails, title, body, isHtml);
	}
}
