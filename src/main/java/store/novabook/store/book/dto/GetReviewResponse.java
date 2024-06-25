package store.novabook.store.book.dto;

import lombok.Builder;
import store.novabook.store.book.entity.Review;

@Builder
public record GetReviewResponse(
	Long id,
	Long orderId,
	Long bookId,
	String content,
	int score
) {
	public static GetReviewResponse from(Review review) {
		return GetReviewResponse.builder()
			.id(review.getId())
			.bookId(review.getBook().getId())
			.orderId(review.getOrders().getId())
			.content(review.getContent())
			.score(review.getScore())
			.build();
	}
}
