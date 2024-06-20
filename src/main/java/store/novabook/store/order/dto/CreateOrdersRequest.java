package store.novabook.store.order.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrdersRequest (
	@NotNull(message = "userId 값은 필수 입니다")
	Long userId,
	@NotNull(message = "deliveryFeeId 값은 필수 입니다")
	Long deliveryFeeId,
	@NotNull(message = "wrappingPaperId 값은 필수 입니다")
	Long wrappingPaperId,
	@NotNull(message = "ordersStatusId 값은 필수 입니다")
	Long ordersStatusId,
	@NotNull(message = "returnPolicyId 값은 필수 입니다")
	Long returnPolicyId,
	@NotNull(message = "ordersDate 값은 필수 입니다")
	LocalDateTime ordersDate,
	@NotNull(message = "totalAmount 값은 필수 입니다")
	Long totalAmount,
	@NotNull(message = "deliveryDate 값은 필수 입니다")
	LocalDateTime deliveryDate,
	@NotNull(message = "bookPurchaseAmount 값은 필수 입니다")
	@Min(value = 0,message = "0보다 커야 합니다 ")
	long bookPurchaseAmount,
	@NotNull(message = "deliveryAddress 값은 필수 입니다")
	@NotBlank(message = "deliveryAddress 값은 필수 입니다")
	String deliveryAddress,
	@NotNull(message = "recieverName 값은 필수 입니다")
	@NotBlank(message = "recieverName 값은 필수 입니다")
	String recieverName,
	@NotNull(message = "recieverNumber 값은 필수 입니다")
	@NotBlank(message = "recieverNumber 값은 필수 입니다")
	String recieverNumber
){
}
