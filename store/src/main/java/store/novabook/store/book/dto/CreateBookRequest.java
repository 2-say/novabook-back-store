package store.novabook.store.book.dto;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;


public record CreateBookRequest(
	@NotNull(message = "책 상태는 필수 입력값입니다.")
	Long bookStatusId,
	@NotNull(message = "ISBN는 필수 입력값입니다.")
	String isbn,
	@NotNull(message = "책 제목는 필수 입력값입니다.")
	String title,
	@NotNull(message = "책 목차는 필수 입력값입니다.")
	String bookIndex,
	@NotNull(message = "책 설명는 필수 입력값입니다.")
	String description,
	@NotNull(message = "책 상세보기 설명은 필수 입력값입니다.")
	String descriptionDetail,
	@NotNull(message = "책 작가는 필수 입력값입니다.")
	String author,
	@NotNull(message = "책 출판사는 필수 입력값입니다.")
	String publisher,
	@NotNull(message = "책 발행일는 필수 입력값입니다.")
	LocalDateTime publicationDate,
	@NotNull(message = "책 재고는 필수 입력값입니다.")
	int inventory,
	@NotNull(message = "책 가격은 필수 입력값입니다.")
	Long price,
	@NotNull(message = "책 할인가격은 필수 입력값입니다.")
	Long discountPrice,
	@NotNull(message = "책 상태는 필수 입력값입니다.")
	boolean isPackaged,
	String image
) {}
