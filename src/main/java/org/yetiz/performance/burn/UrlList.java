package org.yetiz.performance.burn;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by yeti on 15/1/5.
 */
public class UrlList {

	public static ArrayList<String> getUrlList(String urlFilePath) {
		ArrayList<String> urlList = new ArrayList<String>();
		String pathSeparator = System.getProperty("path.separator", ":");
		String workingDirs = System.getProperty("user.dir");
		File urlFile = null;
		for (String workingDir : workingDirs.split(pathSeparator)) {
			urlFile = new File(new File(workingDir), urlFilePath);
		}
		if (!urlFile.exists()) {
			urlFile = new File(urlFilePath);
			if (!urlFile.exists()) {
				Logger.getLogger(UrlList.class).error("No Url File.");
				return null;
			}
		}
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(urlFile));
			while (true) {
				String read = bufferedReader.readLine();
				if (read == null) {
					Logger.getLogger(UrlList.class).info("read line end.");
					break;
				}
				urlList.add(read);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urlList;
	}
}
