package store.novabook.store.orders.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import store.novabook.store.orders.dto.OrderSagaMessage;
import store.novabook.store.orders.dto.RequestPayCancelMessage;
import store.novabook.store.orders.dto.request.PaymentRequest;

@ExtendWith(MockitoExtension.class)
class OrdersSagaManagerImplTest {

	@Mock
	private RabbitTemplate rabbitTemplate;

	@InjectMocks
	private OrdersSagaManagerImpl ordersSagaManager;

	@Captor
	private ArgumentCaptor<OrderSagaMessage> orderSagaMessageCaptor;

	private PaymentRequest paymentRequest;
	private OrderSagaMessage orderSagaMessage;
	private RequestPayCancelMessage payCancelMessage;

	@BeforeEach
	void setUp() {
		paymentRequest = PaymentRequest.builder()
			.type(null)
			.memberId(1L)
			.orderCode("orderCode")
			.paymentInfo(null)
			.build();
		orderSagaMessage = OrderSagaMessage.builder().build();
		payCancelMessage = RequestPayCancelMessage.builder()
			.orderCode("orderCode")
			.couponId(1L)
			.usePointAmount(100L)
			.memberId(1L)
			.earnPointAmount(50L)
			.paymentKey("paymentKey")
			.status("status")
			.build();
	}

	@Test
	void testOrderInvoke() {
		doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

		ordersSagaManager.orderInvoke(paymentRequest);

		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq("orders.form.verify.routing.key"),
			orderSagaMessageCaptor.capture()
		);
		OrderSagaMessage capturedMessage = orderSagaMessageCaptor.getValue();
		assertEquals("PROCEED_CONFIRM_ORDER_FORM", capturedMessage.getStatus());
		assertEquals(paymentRequest, capturedMessage.getPaymentRequest());

		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq("cart.delete.routing.key"),
			orderSagaMessageCaptor.capture()
		);
		capturedMessage = orderSagaMessageCaptor.getValue();
		assertEquals("PROCEED_DELETE_CART", capturedMessage.getStatus());
		assertEquals(paymentRequest, capturedMessage.getPaymentRequest());
	}

	@Test
	void testHandleApiResponse() {
		orderSagaMessage.setStatus("SUCCESS_CONFIRM_ORDER_FORM");

		orderSagaMessage.setNoUseCoupon(true);
		orderSagaMessage.setNoUsePoint(true);
		doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

		ordersSagaManager.handleApiResponse(orderSagaMessage);

		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq("orders.approve.payment.routing.key"),
			orderSagaMessageCaptor.capture()
		);
		OrderSagaMessage capturedMessage = orderSagaMessageCaptor.getValue();
		assertEquals("PROCEED_APPROVE_PAYMENT", capturedMessage.getStatus());
	}

	@Test
	void testHandleApi2Response() {
		orderSagaMessage.setStatus("SUCCESS_APPLY_COUPON");
		orderSagaMessage.setNoUsePoint(true); // 포인트를 사용하지 않음

		doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

		ordersSagaManager.handleApi2Response(orderSagaMessage);

		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq("orders.approve.payment.routing.key"),
			orderSagaMessageCaptor.capture()
		);
		OrderSagaMessage capturedMessage = orderSagaMessageCaptor.getValue();
		assertEquals("PROCEED_APPROVE_PAYMENT", capturedMessage.getStatus());
	}

	@Test
	void testHandleApi3Response() {
		orderSagaMessage.setStatus("SUCCESS_POINT_DECREMENT");
		doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

		ordersSagaManager.handleApi3Response(orderSagaMessage);

		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq("orders.approve.payment.routing.key"),
			orderSagaMessageCaptor.capture()
		);
		OrderSagaMessage capturedMessage = orderSagaMessageCaptor.getValue();
		assertEquals("PROCEED_APPROVE_PAYMENT", capturedMessage.getStatus());
	}

	@Test
	void testHandleApi4Response() {
		orderSagaMessage.setStatus("SUCCESS_APPROVE_PAYMENT");
		doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

		ordersSagaManager.handleApi4Response(orderSagaMessage);

		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq("orders.save.database.routing.key"),
			orderSagaMessageCaptor.capture()
		);
		OrderSagaMessage capturedMessage = orderSagaMessageCaptor.getValue();
		assertEquals("PROCEED_SAVE_ORDERS_DATABASE", capturedMessage.getStatus());
	}

	@Test
	void testHandleApi5Response() {
		orderSagaMessage.setStatus("SUCCESS_SAVE_ORDERS_DATABASE");
		doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

		ordersSagaManager.handleApi5Response(orderSagaMessage);

		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq("point.earn.routing.key"),
			orderSagaMessageCaptor.capture()
		);
		OrderSagaMessage capturedMessage = orderSagaMessageCaptor.getValue();
		assertEquals("PROCEED_EARN_POINT", capturedMessage.getStatus());
	}

	@Test
	void testHandleApi6Response() {
		// Scenario 1: FAIL_EARN_POINT status
		orderSagaMessage.setStatus("FAIL_EARN_POINT");
		doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

		ordersSagaManager.handleApi6Response(orderSagaMessage);

		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_DEAD_ROUTING_KEY),
			orderSagaMessageCaptor.capture()
		);
		OrderSagaMessage capturedMessage = orderSagaMessageCaptor.getValue();
		assertEquals("FAIL_EARN_POINT", capturedMessage.getStatus());

		// Scenario 2: SUCCESS_EARN_POINT status
		orderSagaMessage.setStatus("SUCCESS_EARN_POINT");
		ordersSagaManager.handleApi6Response(orderSagaMessage);

		assertEquals("SUCCESS_ALL_ORDER_SAGA", orderSagaMessage.getStatus());
		verifyNoMoreInteractions(rabbitTemplate);
	}
	@Test
	void testRequestPayCancel() {
		payCancelMessage.setCouponId(1L);
		payCancelMessage.setUsePointAmount(100L);

		doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

		ordersSagaManager.requestPayCancel(payCancelMessage);

		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq("coupon.request.pay.cancel.routing.key"),
			eq(payCancelMessage)
		);
		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq("point.request.pay.cancel.routing.key"),
			eq(payCancelMessage)
		);
		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq("payment.pay.cancel.routing.key"),
			eq(payCancelMessage)
		);
		verify(rabbitTemplate).convertAndSend(
			eq(OrdersSagaManagerImpl.NOVA_ORDERS_SAGA_EXCHANGE),
			eq("orders.request.pay.cancel.routing.key"),
			eq(payCancelMessage)
		);
	}
}
