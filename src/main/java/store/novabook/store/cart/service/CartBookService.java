package store.novabook.store.cart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import store.novabook.store.cart.dto.CreateCartBookRequest;
import store.novabook.store.cart.dto.DeleteCartBookRequest;
import store.novabook.store.cart.dto.GetCartBookResponse;

public interface CartBookService {
	void createCartBook(CreateCartBookRequest createCartBookRequest);

	@Transactional(readOnly = true)
	Page<GetCartBookResponse> getCartBookListByCartId(Long cartId, Pageable pageable);

	void deleteCartBookAndBook(DeleteCartBookRequest deleteCartBookRequest);
}
