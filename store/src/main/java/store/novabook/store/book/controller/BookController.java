package store.novabook.store.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.novabook.store.book.dto.CreateBookRequest;
import store.novabook.store.book.dto.GetBookResponse;
import store.novabook.store.book.service.BookService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {
	private final BookService bookService;


	@GetMapping("/book/{id}")
	public ResponseEntity<GetBookResponse> getBook(@PathVariable Long id) {


		return ResponseEntity.ok().body(bookService.getBook(id));
	}

	@PostMapping("/create")
	public ResponseEntity<Void> createBook(CreateBookRequest createBookRequest) {
		bookService.create(createBookRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}


}
