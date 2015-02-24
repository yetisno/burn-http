package org.yetiz.performance.burn;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yeti on 15/2/24.
 */
public class Req {
	private Map<String, String> headers = new HashMap<String, String>();
	private String method = "";
	private String body = "";
	private String url = "";


	public Req addHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}


	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
