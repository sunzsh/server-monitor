package com.zthzinfo.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
public class Webhook {

	private String name;
	private String url;
	private String multiUserSeparator;
	private EHookType eHookType;
	private Matcher matcher;

	public Webhook(String name, String url) {
		this.setName(name);
		this.setUrl(url);
	}


	public void setUrl(String url) {
		this.url = url;
		if (this.url == null) {
			this.eHookType = EHookType.NO_USER;
			return;
		}
		Pattern pattern = Pattern.compile("\\{\\{\\s*(user((.?)))\\}\\}");
		Matcher matcher = pattern.matcher(this.url);
		if (matcher.find()) {
			if (matcher.groupCount() >= 3) {
				this.eHookType = EHookType.MULTI_USER;
				this.multiUserSeparator = matcher.group(2);
			} else {
				this.multiUserSeparator = null;
				this.eHookType = EHookType.SINGLE_USER;
			}
			this.matcher = matcher;
		} else {
			this.eHookType = EHookType.NO_USER;
		}
	}

	public String getUrlWithUsers(List<User> users) {
		if (users == null) {
			return getUrlWithUserIds(null);
		}
		return getUrlWithUserIds(users.stream().map(User::getWebhookUserid).collect(Collectors.toList()));
	}

	public String getUrlWithUserIds(List<String> userIds) {
		if (this.eHookType == EHookType.NO_USER || matcher == null) {
			return this.getUrl();
		}
		if (userIds == null && userIds.size() == 0) {
			return matcher.replaceAll("");
		}
		String ids = userIds.stream().distinct().collect(Collectors.joining(this.multiUserSeparator));
		try {
			return matcher.replaceAll(URLEncoder.encode(ids, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return matcher.replaceAll("");
		}
	}
}
