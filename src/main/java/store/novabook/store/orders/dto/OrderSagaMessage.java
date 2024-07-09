package store.novabook.store.orders.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import store.novabook.store.orders.dto.request.PaymentRequest;

@Setter
@Getter
@Builder
@JsonDeserialize(builder = OrderSagaMessage.OrderSagaMessageBuilder.class)
public class OrderSagaMessage {
	long calculateTotalAmount;
	boolean noUsePoint;
	boolean noUseCoupon;
	String status;
	PaymentRequest paymentRequest;
	@JsonPOJOBuilder(withPrefix = "")
	public static class OrderSagaMessageBuilder {
	}
}
