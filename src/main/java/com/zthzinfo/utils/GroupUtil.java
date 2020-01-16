package com.zthzinfo.utils;

import com.zthzinfo.beans.Group;
import com.zthzinfo.beans.User;

import java.util.*;
import java.util.stream.Collectors;

public class GroupUtil {
	public static Map<String, Group> groups = new HashMap<>();
	public static void reloadGroups() {
		groups = new HashMap<>();
		List<String> keys = ConfigUtil.configs.keySet().stream().filter(k -> k.matches("^groups\\.[^.]*$")).sorted().collect(Collectors.toList());
		_outter:for (String key : keys) {
			String[] keyArr = key.split("\\.");
			String value = ConfigUtil.configs.get(key);
			if (value == null || value.length() == 0) {
				continue;
			}

			String[] users = value.split("\\s*,\\s*");
			if (users == null) {
				continue;
			}

			Group group = new Group();
			group.setName(keyArr[1]);

			for (String userName : users) {
				if (group.getUsers().stream().map(User::getName).collect(Collectors.toList()).contains(userName)) {
					continue;
				}
				User user = UserUtil.getUserByName(userName);
				if (user == null) {
					continue;
				}
				group.getUsers().add(user);
			}

			groups.put(group.getName(), group);

		}
	}


	public static LinkedHashSet<User> getUsersByGroups(String...groupNames) {
		LinkedHashSet<User> result = new LinkedHashSet<>();
		if (groupNames == null || groupNames.length == 0) {
			return result;
		}
		for (String groupName : groupNames) {
			Group group = groups.get(groupName);
			if (group == null) {
				continue;
			}
			List<User> users = group.getUsers();
			if (users == null) {
				continue;
			}
			result.addAll(users);
		}

		return result;

	}

}
