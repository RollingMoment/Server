package com.RollinMoment.RollinMomentServer.common;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AppResponseEntity<T> {
	private MetaData meta;
	private T body;

	public AppResponseEntity(MetaData meta) {
		this.meta = meta;
		this.body = null;
	}

	@AllArgsConstructor
	public class MetaData {
		private Integer code;
		private String message;
	}
}
