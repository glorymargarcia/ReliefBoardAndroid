package com.stratpoint.reliefboard.api;

import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

class HttpDelete extends HttpEntityEnclosingRequestBase {
	
	public HttpDelete() {
		super();
	}
	
	public HttpDelete(URI uri) {
		super();
		this.setURI(uri);
	}

	@Override
	public String getMethod() {
		return "DELETE";
	}

}
