package store.novabook.store.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.store.category.controller.docs.CategoryControllerDocs;
import store.novabook.store.category.dto.request.CreateCategoryRequest;
import store.novabook.store.category.dto.response.CreateCategoryResponse;
import store.novabook.store.category.dto.response.DeleteResponse;
import store.novabook.store.category.dto.response.GetCategoryIdsByBookIdResponse;
import store.novabook.store.category.dto.response.GetCategoryListResponse;
import store.novabook.store.category.dto.response.GetCategoryResponse;
import store.novabook.store.category.service.CategoryService;
import store.novabook.store.common.security.aop.CheckRole;

@RestController
@RequestMapping("/api/v1/store/categories")
@RequiredArgsConstructor
public class CategoryController implements CategoryControllerDocs {
	private final CategoryService categoryService;

	@CheckRole("ROLE_ADMIN")
	@PostMapping
	public ResponseEntity<CreateCategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest category) {
		return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(category));
	}

	@GetMapping("/{id}")
	public ResponseEntity<GetCategoryResponse> getCategory(@PathVariable Long id) {
		return ResponseEntity.ok().body(categoryService.getCategory(id));
	}

	@GetMapping
	public ResponseEntity<GetCategoryListResponse> getCategoryAll() {
		return ResponseEntity.ok().body(categoryService.getAllCategories());
	}

	@GetMapping("/book/{bookId}")
	public ResponseEntity<GetCategoryIdsByBookIdResponse> getCategoryByBId(@PathVariable Long bookId) {
		return ResponseEntity.ok().body(categoryService.getCategoryIdsByBookId(bookId));
	}

	@CheckRole("ROLE_ADMIN")
	@DeleteMapping("/{id}")
	public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
		DeleteResponse response = categoryService.delete(id);
		return ResponseEntity.ok().body(response);
	}
}
