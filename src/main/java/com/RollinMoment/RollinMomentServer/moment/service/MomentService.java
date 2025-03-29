package com.RollinMoment.RollinMomentServer.moment.service;

import com.RollinMoment.RollinMomentServer.moment.entity.Moment;
import com.RollinMoment.RollinMomentServer.moment.entity.MomentCoverImg;
import com.RollinMoment.RollinMomentServer.moment.repository.MomentCoverImgRepository;
import com.RollinMoment.RollinMomentServer.moment.repository.MomentRepository;
import com.RollinMoment.RollinMomentServer.moment.service.dto.MomentCreatePageResponse;
import com.RollinMoment.RollinMomentServer.moment.service.dto.MomentCreateRequest;
import com.RollinMoment.RollinMomentServer.moment.service.dto.MomentDetailResponse;
import com.RollinMoment.RollinMomentServer.moment.service.dto.MomentSettingRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {

	private final MomentRepository momentRepository;
	private final MomentCoverImgRepository momentCoverImgRepository;

	@Transactional
	public Integer createNewMoment(MomentCreateRequest request) {
		MomentCoverImg coverImg = momentCoverImgRepository.findById(request.coverImg()).orElse(null);
		if(coverImg == null && !request.coverImg().isEmpty()) {
			// 이미지 처리 어떻게 할 건지...? cover img 재 정의 필요
//			momentCoverImgRepository.save(new MomentCoverImg(request.coverImg()));
		}

		coverImg = MomentCoverImg.basic();
		try {
			// 생성 성공
			Moment moment = new Moment(
					request.title(),
					coverImg,
					request.font(),
					request.expireType(),
					request.category().name(),
					request.comment(),
					request.isPublic()
			);

			momentRepository.save(moment);
			// invite code 를 보내야 합니다..
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Transactional
	public Integer changeMomentSettings(@NotNull MomentSettingRequest request) {

		try {
			Moment moment = findMomentById(request.code());

			if(true) {
				// title이 20자를 넘을 수 없습니다.
			}

			if(true) {
				// comment는 500자를 넘을 수 없습니다.
			}

			if(true) {
				// validation What?
			}

			moment.changeMomentSettings(request);
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public MomentCreatePageResponse getCategoriesAndCoverImages() {
		// 현재 활성화되어있는 카테고리 목록 조회
		List<MomentCreatePageResponse.CategoryRes> categoryResList = new ArrayList<>();

		// 현재 활성화되어있는 (저장된) 커버이미지 목록 조회
		List<MomentCreatePageResponse.CoverImgRes> coverImgResList = new ArrayList<>();

		return new MomentCreatePageResponse(categoryResList, coverImgResList);
	}

	public MomentDetailResponse getMomentDetail(String momentCode) {
		Moment moment = findMomentById(momentCode);

		// traces, reactions 도 불러와야해요..

		return new MomentDetailResponse(moment);
	}

	@Transactional
	public Integer deleteMoment(String momentCode) {
		try {
			Moment moment = findMomentById(momentCode);
			moment.deleteMoment();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}

	private Moment findMomentById(String momentCode) {
		return momentRepository.findById(momentCode)
					   .orElseThrow(() -> new IllegalArgumentException("모먼트를 찾을 수 없습니다."));
	}
}
