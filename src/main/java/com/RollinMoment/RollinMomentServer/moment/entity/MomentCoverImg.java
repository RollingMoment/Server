package com.RollinMoment.RollinMomentServer.moment.entity;

import com.RollinMoment.RollinMomentServer.common.type.CoverImgType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "moment_cover_img")
@Getter
@NoArgsConstructor
public class MomentCoverImg {
	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "type", nullable = false,
			columnDefinition = "ENUM('ANNIVERSARY','SENDING_HEARS','FAREWELL')")
	private CoverImgType type;

	@Column(name = "url", nullable = false)
	private String url;

	@Column(name = "file_name", nullable = false)
	private String fileName;

	public MomentCoverImg(String code, CoverImgType type, String url, String fileName) {
		this.code = code;
		this.type = type;
		this.url = url;
		this.fileName = fileName;
	}

	public static MomentCoverImg basic() {
		return new MomentCoverImg("none", CoverImgType.NONE, "", "");
	}
}
