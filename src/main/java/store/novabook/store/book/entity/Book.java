package store.novabook.store.book.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.novabook.store.book.dto.CreateBookRequest;
import store.novabook.store.book.dto.UpdateBookRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "book_status_id")
	private BookStatus bookStatus;

	@NotNull
	private String isbn;

	@NotNull
	private String title;

	@NotNull
	private String description;

	@NotNull
	private String descriptionDetail;

	@NotNull
	private String author;

	@NotNull
	private String publisher;


	@NotNull
	private LocalDateTime publicationDate;

	@NotNull
	int inventory;

	@NotNull
	private long price;

	@NotNull
	private Long discountPrice;

	@NotNull
	boolean isPackaged;

	@NotNull
	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;


	@Builder
	public Book(BookStatus bookStatus,
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
				boolean isPackaged) {
		this.bookStatus = bookStatus;
		this.isbn = isbn;
		this.title = title;
		this.description = description;
		this.descriptionDetail = descriptionDetail;
		this.author = author;
		this.publisher = publisher;
		this.publicationDate = publicationDate;
		this.inventory = inventory;
		this.price = price;
		this.discountPrice = discountPrice;
		this.isPackaged = isPackaged;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = null;

	}


	public static Book of(CreateBookRequest request, BookStatus bookStatus) {
		return Book.builder()
			.bookStatus(bookStatus)
			.isbn(request.isbn())
			.title(request.title())
			.description(request.description())
			.descriptionDetail(request.descriptionDetail())
			.author(request.author())
			.publisher(request.publisher())
			.publicationDate(request.publicationDate())
			.inventory(request.inventory())
			.price(request.price())
			.discountPrice(request.discountPrice())
			.isPackaged(request.isPackaged())
			.build();
	}

	public void update(BookStatus bookStatus, UpdateBookRequest request) {
		this.bookStatus = bookStatus;
		this.inventory = request.inventory();
		this.price = request.price();
		this.discountPrice = request.discountPrice();
		this.isPackaged = request.isPackaged();
		this.updatedAt = LocalDateTime.now();
	}

	public void updateBookStatus(BookStatus bookStatus) {
		this.bookStatus = bookStatus;
	}
}
