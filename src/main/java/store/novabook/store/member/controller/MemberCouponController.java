package store.novabook.store.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.novabook.store.common.security.aop.CurrentUser;
import store.novabook.store.member.controller.docs.MemberCouponControllerDocs;
import store.novabook.store.member.dto.request.CreateMemberCouponRequest;
import store.novabook.store.member.dto.response.CreateMemberCouponResponse;
import store.novabook.store.member.dto.response.GetCouponIdsResponse;
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

	@GetMapping
	public ResponseEntity<GetCouponIdsResponse> getMemberCoupon(@CurrentUser Long memberId) {
		return ResponseEntity.ok().body(memberCouponService.getMemberCoupon(memberId));
	}
}
