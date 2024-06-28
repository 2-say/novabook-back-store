package store.novabook.store.book.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import store.novabook.store.book.entity.Book;
import store.novabook.store.category.entity.Category;
import store.novabook.store.image.entity.Image;
import store.novabook.store.tag.entity.BookTag;

@Builder
public record GetBookResponse(
	Long id,
	String isbn,
	String title,
	String description,
	String descriptionDetail,
	String author,
	String publisher,
	LocalDateTime publicationDate,
	int inventory,
	Long price,
	Long discountPrice,
	boolean isPackaged,
	List<String> tags,
	List<String> categories,
	int likes,
	int score,
	String image

) {
	public static GetBookResponse fromEntity(
		Book book,
		List<String> tags,
		Category categories,
		int likes,
		int score,
		Image image) {
		return GetBookResponse.builder()
			.id(book.getId())
			.isbn(book.getIsbn())
			.title(book.getTitle())
			.description(book.getDescription())
			.descriptionDetail(book.getDescriptionDetail())
			.author(book.getAuthor())
			.publisher(book.getPublisher())
			.publicationDate(book.getPublicationDate())
			.inventory(book.getInventory())
			.price(book.getPrice())
			.isPackaged(book.isPackaged())
			.tags(tags)
			.categories(categoryNames(categories))
			.likes(likes)
			.discountPrice(book.getDiscountPrice())
			.score(score)
			.image(image.getSource())
			.build();
	}

	public static List<String> categoryNames(Category category) {
		List<String> categoryNames = new ArrayList<>();
		if(category.hasTopCategory()){

			categoryNames.add(category.getTopCategory().getName());
			categoryNames.add(category.getName());

		}
		else {
			categoryNames.add(category.getName());
		}
		return categoryNames;
	}



}
