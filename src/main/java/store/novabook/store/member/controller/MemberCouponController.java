package store.novabook.store.member.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.novabook.store.common.adatper.dto.GetCouponAllResponse;
import store.novabook.store.common.adatper.dto.GetCouponHistoryAllResponse;
import store.novabook.store.common.adatper.dto.GetUsedCouponHistoryAllResponse;
import store.novabook.store.common.security.aop.CurrentUser;
import store.novabook.store.member.controller.docs.MemberCouponControllerDocs;
import store.novabook.store.member.dto.request.CreateMemberCouponRequest;
import store.novabook.store.member.dto.response.CreateMemberCouponResponse;
import store.novabook.store.member.service.MemberCouponService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/store/members/coupons")
@RestController
public class MemberCouponController implements MemberCouponControllerDocs {

	private final MemberCouponService memberCouponService;

	@PostMapping
	public ResponseEntity<CreateMemberCouponResponse> createMemberCoupon(@CurrentUser Long memberId,
		@RequestBody CreateMemberCouponRequest request) {
		CreateMemberCouponResponse saved = memberCouponService.createMemberCoupon(memberId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	@GetMapping("/history")
	public ResponseEntity<GetCouponHistoryAllResponse> getMemberCouponHistoryByMemberId(@CurrentUser Long memberId,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		GetCouponHistoryAllResponse response = memberCouponService.getMemberCouponHistory(memberId, pageable);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/history/used")
	public ResponseEntity<GetUsedCouponHistoryAllResponse> getMemberUsedCouponHistoryByMemberId(
		@CurrentUser Long memberId,
		@PageableDefault(sort = "usedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		GetUsedCouponHistoryAllResponse response = memberCouponService.getMemberUsedCouponHistory(memberId, pageable);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/is-valid")
	public ResponseEntity<GetCouponAllResponse> getMemberCouponByMemberId(@CurrentUser Long memberId) {
		GetCouponAllResponse response = memberCouponService.getValidAllByMemberId(memberId);
		return ResponseEntity.ok(response);
	}
}
