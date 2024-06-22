package store.novabook.store.orders.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.store.orders.dto.CreateOrdersRequest;
import store.novabook.store.orders.dto.CreateResponse;
import store.novabook.store.orders.dto.GetOrdersResponse;
import store.novabook.store.orders.dto.UpdateOrdersRequest;
import store.novabook.store.orders.service.OrdersService;

@Tag(name = "Orders API", description = "Orders 을 생성, 조회, 삭제 합니다.")
@RestController
@RequestMapping("/api/v1/store/orders")
@RequiredArgsConstructor
public class OrdersController {
	private final OrdersService ordersService;

	//생성
	@Operation(summary = "생성", description = "생성합니다 ")
	@PostMapping
	public ResponseEntity<CreateResponse> createOrders(@Valid @RequestBody CreateOrdersRequest request) {
		CreateResponse response = ordersService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	//전체 조회
	@GetMapping
	@Operation(summary = "전체 조회", description = "전체 조회합니다.")
	public ResponseEntity<Page<GetOrdersResponse>> getOrdersAll() {
		Page<GetOrdersResponse> responses = ordersService.getOrdersResponsesAll();
		return ResponseEntity.ok(responses);
	}

	//단건조회
	@Operation(summary = "조회", description = "조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<GetOrdersResponse> getOrders(@PathVariable Long id) {
		GetOrdersResponse response = ordersService.getOrdersById(id);
		return ResponseEntity.ok(response);
	}

	//수정
	@Operation(summary = "수정", description = "수정합니다.")
	@PutMapping("/{id}")
	public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateOrdersRequest request) {
		ordersService.update(id, request);
		return ResponseEntity.noContent().build();
	}

}
