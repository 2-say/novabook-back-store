package store.novabook.store.book.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record AuthorResponse(
	Long id,
	String name,
	String description,
	String role,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}

