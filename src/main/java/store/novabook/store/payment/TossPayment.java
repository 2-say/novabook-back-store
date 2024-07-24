package store.novabook.store.payment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import lombok.extern.slf4j.Slf4j;
import store.novabook.store.common.exception.BadRequestException;
import store.novabook.store.common.exception.ErrorCode;
import store.novabook.store.orders.dto.OrderSagaMessage;
import store.novabook.store.orders.dto.RequestPayCancelMessage;
import store.novabook.store.orders.dto.request.TossPaymentCancelRequest;

@Slf4j
public class TossPayment implements Payment {
	public static final String NOVA_ORDERS_SAGA_EXCHANGE = "nova.orders.saga.exchange";
	public static final String TOSS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
	private static final String AMOUNT = "amount";
	private static final String PAYMENT_KEY = "paymentKey";
	private static final String WIDGET_SECRET_KEY = "test_sk_LkKEypNArWLkZabM1Rbz8lmeaxYG";

	@Override
	@Transactional
	public void createOrder(@Payload OrderSagaMessage orderSagaMessage, RabbitTemplate  rabbitTemplate) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject obj = new JSONObject();

			@SuppressWarnings("unchecked") HashMap<String, Object> paymentParam = (HashMap<String, Object>)orderSagaMessage.getPaymentRequest()
				.paymentInfo();

			Integer tossAmountInt = (Integer)paymentParam.get(AMOUNT);
			long tossAmount = tossAmountInt.longValue();

			if (tossAmount != orderSagaMessage.getCalculateTotalAmount()) {
				throw new BadRequestException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
			}

			obj.put("orderId", orderSagaMessage.getPaymentRequest().orderCode());
			obj.put(AMOUNT, paymentParam.get(AMOUNT));
			obj.put(PAYMENT_KEY, paymentParam.get(PAYMENT_KEY));

			// 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
			// 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
			Base64.Encoder encoder = Base64.getEncoder();
			byte[] encodedBytes = encoder.encode((WIDGET_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
			String authorizations = "Basic " + new String(encodedBytes);

			URL url = new URI(TOSS_CONFIRM_URL).toURL();
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("Authorization", authorizations);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			try (OutputStream outputStream = connection.getOutputStream()) {
				outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));
			}

			int code = connection.getResponseCode();
			boolean isSuccess = code == 200;

			try (InputStream responseStream = isSuccess ? connection.getInputStream() :
				connection.getErrorStream(); Reader reader = new InputStreamReader(responseStream,
				StandardCharsets.UTF_8)) {

				JSONObject jsonObject = (JSONObject)parser.parse(reader);

				if (isSuccess) {
					orderSagaMessage.setStatus("SUCCESS_APPROVE_PAYMENT");
				} else {
					orderSagaMessage.setStatus("FAIL_APPROVE_PAYMENT");
				}
				log.info("결제 응답 내용 : {}", jsonObject.toString());
			}

		} catch (Exception e) {
			orderSagaMessage.setStatus("FAIL_APPROVE_PAYMENT");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			rabbitTemplate.convertAndSend(NOVA_ORDERS_SAGA_EXCHANGE, "nova.api4-producer-routing-key",
				orderSagaMessage);
		}
	}


	@Override
	@Transactional
	public void compensateCancelOrder(@Payload OrderSagaMessage orderSagaMessage, RabbitTemplate rabbitTemplate) {
		@SuppressWarnings("unchecked") HashMap<String, String> paymentParam = (HashMap<String, String>)orderSagaMessage.getPaymentRequest()
			.paymentInfo();
		String paymentKey = paymentParam.get(PAYMENT_KEY);

		try {
			TossPaymentCancelRequest tossPaymentCancel = TossPaymentCancelRequest.builder()
				.cancelReason("서버오류 결제 보상 트랜잭션")
				.paymentKey(paymentKey)
				.build();

			sendTossCancelRequest(tossPaymentCancel);
			orderSagaMessage.setStatus("SUCCESS_REFUND_PAYMENT");
		} catch (IOException | URISyntaxException | ParseException e) {
			orderSagaMessage.setStatus("FAIL_REFUND_TOSS_PAYMENT");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			rabbitTemplate.convertAndSend(NOVA_ORDERS_SAGA_EXCHANGE, "nova.orders.saga.dead.routing.key",
				orderSagaMessage);
		}
	}

	public void sendTossCancelRequest(TossPaymentCancelRequest tossPaymentCancelRequest) throws
		IOException,
		ParseException,
		URISyntaxException {
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		obj.put("cancelReason", tossPaymentCancelRequest.cancelReason());

		Base64.Encoder encoder = Base64.getEncoder();
		byte[] encodedBytes = encoder.encode((WIDGET_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
		String authorizations = "Basic " + new String(encodedBytes);

		URL url = new URI(
			"https://api.tosspayments.com/v1/payments/" + tossPaymentCancelRequest.paymentKey() + "/cancel").toURL();
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestProperty("Authorization", authorizations);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);

		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));

		int code = connection.getResponseCode();
		boolean isSuccess = code == 200;

		InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
		Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
		JSONObject jsonObject = (JSONObject)parser.parse(reader);
		responseStream.close();

		if (!isSuccess) {
			log.error("토스 환불 실패 {}", jsonObject.toString());
		}

		log.info("jsonObject");
	}


	@Override
	public void cancelOrder(@Payload RequestPayCancelMessage message, RabbitTemplate rabbitTemplate) {
		try {
			sendTossCancelRequest(TossPaymentCancelRequest.builder()
				.paymentKey(message.getPaymentKey())
				.cancelReason("결제 취소 요청")
				.build());
		} catch (IOException | ParseException | URISyntaxException e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			message.setStatus("FAIL_REQUEST_CANCEL_TOSS_PAYMENT");
			rabbitTemplate.convertAndSend(NOVA_ORDERS_SAGA_EXCHANGE, "nova.orders.saga.dead.routing.key", message);
		}
	}

}
