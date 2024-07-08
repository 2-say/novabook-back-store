package store.novabook.store.orders.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.novabook.store.orders.dto.OrderSagaMessage;
import store.novabook.store.orders.dto.PaymentType;
import store.novabook.store.orders.dto.SagaMessage;
import store.novabook.store.orders.dto.request.PaymentRequest;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrdersSagaManagerImpl {

	private final RabbitTemplate rabbitTemplate;
	private PaymentRequest paymentRequest;

	/**
	 * 주문 로직 (트랜잭션)
	 * 0. 주문서 검증 (총 결제 금액)
	 * 1. 재고 감소 시작
	 * 2. 포인트 감소
	 * 3. 쿠폰 사용 상태 변경
	 * 3.a -> 총 결제 가격 검증
	 * 4. 결제 승인
	 * <비동기 처리>
	 * 적립포인트 충전
	 * 장바구니 제거
	 * 가주문 제거
	 */
	public void orderInvoke(PaymentRequest paymentRequest) {
		this.paymentRequest = paymentRequest;
		rabbitTemplate.convertAndSend("nova.orders.saga.exchange", "orders.form.confirm"
			, OrderSagaMessage.builder()
				.status("PROCEED_CONFIRM_ORDER_FORM")
				.paymentRequest(paymentRequest)
				.build()
		);

	}

	// @RabbitListener(queues = "api1-producer-queue")
	public void handleApiResponse(SagaMessage message) {
		log.info("API  {} ", message.getStatus());
		if (message.getStatus().equals("success")) {
			rabbitTemplate.convertAndSend("saga-exchange", "api-포인트-감소-routing-key"
				, new SagaMessage("API2", message.getOrderId()));
		} else if (message.getStatus().equals("CONFIRM_FAIL")) {
			log.info("API1 execution failed");
		}
	}

	// @RabbitListener(queues = "api2-producer-queue")
	public void handleApi2Response(SagaMessage message) {
		System.out.println("API2 " + message.getStatus());
		if (message.getStatus().equals("success")) {
			rabbitTemplate.convertAndSend("saga-exchange", "api3-consumer-routing-key",
				new SagaMessage("API3", message.getOrderId()));
		} else if (message.getStatus().equals("fail")) {
			System.out.println("API2 execution failed compensation initiated");
			rabbitTemplate.convertAndSend("saga-exchange", "compensate-api1-routing-key",
				new SagaMessage("Fail", message.getOrderId()));
		}
	}

	// @RabbitListener(queues = "api3-producer-queue")
	public void handleApi3Response(SagaMessage message) {
		System.out.println("API3 " + message.getStatus());
		if (message.getStatus().equals("success")) {
			System.out.println("ALL API SUCCESSFULLY EXECUTED");
		} else if (message.getStatus().equals("fail")) {
			System.out.println("API3 execution failed compensation initiated");
			rabbitTemplate.convertAndSend("saga-exchange", "compensate-api2-routing-key",
				new SagaMessage("Fail", message.getOrderId()));
		}
	}

}
