package store.novabook.store.search.dto;

import lombok.Builder;
import store.novabook.store.book.entity.Book;
import store.novabook.store.search.document.BookDocument;

@Builder
public record GetBookSearchResponse(
	Long id,
	String title,
	String author,
	String publisher,
	// LocalDateTime publication_date,
	String image,
	Long price,
	Long discountPrice,
	Double score,
	Boolean isPackaged
) {
	public static GetBookSearchResponse of(BookDocument bookDocument, Book book, Integer score) {
		return GetBookSearchResponse.builder()
			.id(bookDocument.getId())
			.title(bookDocument.getTitle())
			.author(bookDocument.getAuthor())
			.publisher(bookDocument.getPublisher())
			.image(bookDocument.getImage())
			.price(book.getPrice())
			.discountPrice(book.getDiscountPrice())
			.score(Double.valueOf(score))
			.isPackaged(book.isPackaged())
			.build();
	}

	public static GetBookSearchResponse of(BookDocument bookDocument) {
		return GetBookSearchResponse.builder()
			.id(bookDocument.getId())
			.title(bookDocument.getTitle())
			.author(bookDocument.getAuthor())
			.publisher(bookDocument.getPublisher())
			.price(bookDocument.getPrice())
			.discountPrice(bookDocument.getDiscountPrice())
			.image(bookDocument.getImage())
			.score(Double.valueOf(bookDocument.getScore()))
			.isPackaged(bookDocument.getIsPackaged())
			.build();
	}
}
