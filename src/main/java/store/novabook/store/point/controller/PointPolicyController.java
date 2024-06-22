package store.novabook.store.point.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.store.point.dto.CreatePointPolicyRequest;
import store.novabook.store.point.dto.GetPointPolicyResponse;
import store.novabook.store.point.service.PointPolicyService;

@Tag(name = "point-policy-controller")
@RestController()
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointPolicyController {
	private final PointPolicyService pointPolicyService;

	@Operation(summary = "포인트 정책 조회", description = "포인트 정책을 조회합니다.")
	@GetMapping("/policies")
	public ResponseEntity<Page<GetPointPolicyResponse>> getPoint(Pageable pageable) {

		Page<GetPointPolicyResponse> pointPolicyResponseList = pointPolicyService.getPointPolicyList(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(pointPolicyResponseList);
	}

	@Operation(summary = "최신 포인트 정책 조회", description = "최신 포인트 정책을 조회합니다 포인트 정책은 최신 포인트 정책으로 적용")
	@Parameter(name = "getPointPolicyResponse", description = "최신 포인트 정책 조회 정보", required = true)
	@GetMapping("/policies/latest")
	public ResponseEntity<GetPointPolicyResponse> getLatestPoint() {

		GetPointPolicyResponse getPointPolicyResponse = pointPolicyService.getLatestPointPolicy();
		return ResponseEntity.status(HttpStatus.OK).body(getPointPolicyResponse);
	}

	@Operation(summary = "포인트 정책 생성", description = "포인트 정책을 생성합니다.")
	@Parameter(name = "createPointPolicyRequest", description = "포인트 정책 생성 정보", required = true)
	@PostMapping("/policies")
	public ResponseEntity<Void> createPointPolicy(
		@Valid @RequestBody CreatePointPolicyRequest createPointPolicyRequest) {

		pointPolicyService.createPointPolicy(createPointPolicyRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
