package com.RollinMoment.RollinMomentServer.moment;

import com.RollinMoment.RollinMomentServer.common.AppResponse;
import com.RollinMoment.RollinMomentServer.moment.service.MomentService;
import com.RollinMoment.RollinMomentServer.moment.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/moment")
@RequiredArgsConstructor
public class MomentController {

	private final MomentService momentService;

	@GetMapping("")
	public AppResponse<MomentDetailResponse> getMomentDetail(@RequestBody MomentBasicRequest request) {
		return AppResponse.ok(momentService.getMomentDetail(request.code()));
	}

	@GetMapping("/create")
	public AppResponse<MomentCreatePageResponse> getCategoriesAndCoverImgs() {
		return AppResponse.ok(momentService.getCategoriesAndCoverImages());
	}

	@PostMapping("")
	public AppResponse<MomentCreateResponse> createNewMoment(@RequestBody MomentCreateRequest request) {
		return AppResponse.ok().build();
	}

	@PatchMapping("")
	public AppResponse<> changeMomentSettings(@RequestBody MomentSettingRequest request) {
		return AppResponse.ok().build();
	}

	@PatchMapping("/delete")
	public AppResponse<> deleteMoment(@RequestBody MomentBasicRequest request) {
		return AppResponse.ok().build();
	}
}
