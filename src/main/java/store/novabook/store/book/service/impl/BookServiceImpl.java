package store.novabook.store.book.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.novabook.store.book.dto.request.CreateBookRequest;
import store.novabook.store.book.dto.request.UpdateBookRequest;
import store.novabook.store.book.dto.response.CreateBookResponse;
import store.novabook.store.book.dto.response.GetBookAllResponse;
import store.novabook.store.book.dto.response.GetBookResponse;
import store.novabook.store.book.dto.response.GetBookToMainResponseMap;
import store.novabook.store.book.entity.Book;
import store.novabook.store.book.entity.BookStatus;
import store.novabook.store.book.repository.BookQueryRepository;
import store.novabook.store.book.repository.BookRepository;
import store.novabook.store.book.repository.BookStatusRepository;
import store.novabook.store.book.service.BookService;
import store.novabook.store.category.entity.BookCategory;
import store.novabook.store.category.entity.Category;
import store.novabook.store.category.repository.BookCategoryRepository;
import store.novabook.store.category.repository.CategoryRepository;
import store.novabook.store.common.exception.ErrorCode;
import store.novabook.store.common.exception.NotFoundException;
import store.novabook.store.image.service.ImageService;
import store.novabook.store.tag.entity.BookTag;
import store.novabook.store.tag.entity.Tag;
import store.novabook.store.tag.repository.BookTagRepository;
import store.novabook.store.tag.repository.TagRepository;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
	private final BookRepository bookRepository;
	private final BookStatusRepository bookStatusRepository;
	private final BookTagRepository bookTagRepository;
	private final CategoryRepository categoryRepository;
	private final TagRepository tagRepository;
	private final BookCategoryRepository bookCategoryRepository;
	private final BookQueryRepository queryRepository;
	private final ImageService imageService;



	public CreateBookResponse create(CreateBookRequest request) {
		BookStatus bookStatus = bookStatusRepository.findById(request.bookStatusId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_STATUS_NOT_FOUND));

		Book book = bookRepository.save(Book.of(request, bookStatus));

		List<Tag> tags = tagRepository.findByIdIn(request.tags());
		List<BookTag> bookTags = tags.stream().map(tag -> new BookTag(book, tag)).toList();
		bookTagRepository.saveAll(bookTags);

		List<Category> categories = categoryRepository.findByIdIn(request.categories());
		List<BookCategory> bookCategories = categories.stream()
			.map(category -> new BookCategory(book, category))
			.toList();
		bookCategoryRepository.saveAll(bookCategories);
		imageService.createBookImage(book, request.image());

		return new CreateBookResponse(book.getId());
	}

	@Transactional(readOnly = true)
	public GetBookResponse getBook(Long id) {
		return queryRepository.getBook(id);
	}

	@Transactional(readOnly = true)
	public Page<GetBookAllResponse> getBookAll(Pageable pageable) {
		Page<Book> books = bookRepository.findAll(pageable);
		return books.map(GetBookAllResponse::fromEntity);
	}

	public void update(UpdateBookRequest request) {
		BookStatus bookStatus = bookStatusRepository.findById(request.bookStatusId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_STATUS_NOT_FOUND));

		Book book = bookRepository.findById(request.id())
			.orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_NOT_FOUND));

		book.update(bookStatus, request);
	}

	public void delete(Long id) {
		BookStatus bookStatus = bookStatusRepository.findById(4L)
			.orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_STATUS_NOT_FOUND));

		Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_NOT_FOUND));
		book.updateBookStatus(bookStatus);
	}


	@Override
	@Transactional(readOnly = true)
	public GetBookToMainResponseMap getBookToMainPage() {
		return queryRepository.getBookToMainPage();
	}
}
