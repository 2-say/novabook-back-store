package store.novabook.store.order.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import store.novabook.store.order.entity.DeliveryFee;

@Builder
public record GetDeliveryFeeResponse(
	Long id,
	long fee,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static GetDeliveryFeeResponse from(DeliveryFee deliveryFee) {
		return GetDeliveryFeeResponse.builder()
			.id(deliveryFee.getId())
			.fee(deliveryFee.getFee())
			.createdAt(deliveryFee.getCreatedAt())
			.updatedAt(deliveryFee.getUpdatedAt())
			.build();
	}
}
