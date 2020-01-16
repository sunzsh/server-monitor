package com.zthzinfo;

import cn.hutool.log.LogFactory;
import cn.hutool.log.dialect.console.ConsoleLogFactory;
import com.zthzinfo.beans.Group;
import com.zthzinfo.service.ServerAppService;
import com.zthzinfo.utils.GroupUtil;
import com.zthzinfo.utils.UserUtil;
import com.zthzinfo.utils.WebhookUtil;

public class Application {


	public static void main(String[] args) {
		LogFactory.setCurrentLogFactory(new ConsoleLogFactory());

		WebhookUtil.reloadWebHooks();
		UserUtil.reloadUsers();
		GroupUtil.reloadGroups();

		ServerAppService serverAppService = new ServerAppService();
		serverAppService.initApps();
		System.out.println();

		while (true) {
			serverAppService.checkApps();
			System.out.println();

			try {
				Thread.sleep(3000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
