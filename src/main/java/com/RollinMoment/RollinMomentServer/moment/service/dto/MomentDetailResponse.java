package com.RollinMoment.RollinMomentServer.moment.service.dto;

import com.RollinMoment.RollinMomentServer.moment.entity.Moment;
import com.RollinMoment.RollinMomentServer.reaction.entity.ReactionType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public record MomentDetailResponse (
		String inviteCode, //inviteCode
		Boolean isExpired, //isExpired
		Integer deadline, //데드라인…..
		String category, //category
		String title, //title
		String comment, //comment
		String period, //period
		Boolean isPublic, //isPublic
		Integer traceCnt, //traceCnt
		List<TraceDetailRes> traces //traces
) {

	public MomentDetailResponse(Moment moment) {
		this(
				moment.getInviteCode(),
				LocalDateTime.now().isAfter(moment.getExpireAt()),
				0, // moment.deadline(),
				moment.getCategoryEnName(),
				moment.getTitle(),
				moment.getComment(),
				moment.getCreatedAt() + " ~ " + moment.getExpireAt(),
				moment.getIsPublic(),
				0,	// tracesCnt
				new ArrayList<>() // moment.traces
		);
	}

	public class TraceDetailRes {
		private Boolean hasReacted;
		private ReactionRes myReaction; //traces.myReaction
		private String nickname; //traces.nickname
		private String content; //traces.content
		private String date; //traces.date
		private Integer reactionCnt; //traces.reactionCnt
		private List<ReactionRes> reactions;
	}

	public class ReactionRes {
		private String type;
		private Integer cnt;
	}
}
