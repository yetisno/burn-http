package org.yetiz.performance.burn;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeti on 15/1/5.
 */
public class ReqList {

	private List<Req> reqs;

	public static ArrayList<Req> getReqList(String reqFilePath) {
		ArrayList<Req> urlList = new ArrayList<Req>();
		String pathSeparator = System.getProperty("path.separator", ":");
		String workingDirs = System.getProperty("user.dir");
		File urlFile = null;
		for (String workingDir : workingDirs.split(pathSeparator)) {
			urlFile = new File(new File(workingDir), reqFilePath);
		}
		if (!urlFile.exists()) {
			urlFile = new File(reqFilePath);
			if (!urlFile.exists()) {
				Logger.getLogger(ReqList.class).error("No Url File.");
				return null;
			}
		}
		try {
			return ((ArrayList<Req>) new Gson().fromJson(new FileReader(reqFilePath), ReqList.class).reqs);
		} catch (Exception e) {
		}
		return urlList;
	}


}
