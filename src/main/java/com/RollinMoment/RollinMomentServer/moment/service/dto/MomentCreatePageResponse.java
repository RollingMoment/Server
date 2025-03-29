package com.RollinMoment.RollinMomentServer.moment.service.dto;

import java.util.List;

public record MomentCreatePageResponse(
		List<CategoryRes> categories,
		List<CoverImgRes> coverImages
) {

	public class CategoryRes {
		private String name;
		private String title;
	}

	public class CoverImgRes {
		private String key;
		private String url;
		private String category;
	}
}

