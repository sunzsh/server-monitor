package com.zthzinfo.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class FileUtil {
	public static String getJarDir() {

		String path = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		try {
			path = java.net.URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
		int lastIndex = path.lastIndexOf(File.separator);
		path = path.substring(firstIndex, lastIndex);

		return path;
	}
}
