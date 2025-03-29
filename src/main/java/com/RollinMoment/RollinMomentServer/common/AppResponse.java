package com.RollinMoment.RollinMomentServer.common;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class AppResponse<T> extends ResponseEntity<T> {

	@Override
	public static AppResponse ok(T body) {
		return null;
	}

	public AppResponse(HttpStatusCode status) {
		super(status);
	}

	public AppResponse(T body, HttpStatusCode status) {
		super(body, status);
	}

	public AppResponse(MultiValueMap<String, String> headers, HttpStatusCode status) {
		super(headers, status);
	}

	public AppResponse(T body, MultiValueMap<String, String> headers, int rawStatus) {
		super(body, headers, rawStatus);
	}

	public AppResponse(T body, MultiValueMap<String, String> headers, HttpStatusCode statusCode) {
		super(body, headers, statusCode);
	}
}
