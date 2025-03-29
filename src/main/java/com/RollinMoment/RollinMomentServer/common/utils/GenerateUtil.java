package com.RollinMoment.RollinMomentServer.common.utils;

import com.RollinMoment.RollinMomentServer.common.type.PageType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GenerateUtil {

	public static String makeMomentCode() {
		return makeCode(PageType.MOMENT);
	}

	public static String makeTraceCode() {
		return makeCode(PageType.TRACE);
	}

	public static String makeReactionCode() {
		return makeCode(PageType.REACTION);
	}

	// TODO : pk 형식 정해서 generate 현재(임시) :: MO2025031719557974290
	private static String makeCode(PageType type) {
		return type.getCodePrefix()
					   + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyDD"))
					   + System.currentTimeMillis();
	}

	public static String makeInviteCode(String momentCode) {
		return momentCode + "_invite";
	}
}
