package store.novabook.store.common.hanlder;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import store.novabook.store.common.exception.ErrorCode;
import store.novabook.store.common.exception.FeignClientException;
import store.novabook.store.common.exception.ForbiddenException;
import store.novabook.store.common.exception.NotFoundException;
import store.novabook.store.common.exception.NovaException;
import store.novabook.store.common.response.ErrorResponse;
import store.novabook.store.common.response.ValidErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		log.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ValidErrorResponse.from(exception));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception,
		WebRequest request) {
		log.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.from(ErrorCode.DUPLICATED_VALUE));
	}

	@ExceptionHandler(PersistenceException.class)
	public ResponseEntity<ErrorResponse> handlePersistenceException(PersistenceException exception, WebRequest request) {
		log.error(exception.getMessage(), exception);
		Throwable cause = exception.getCause();
		if (cause instanceof ConstraintViolationException constraintViolationException) {
			return handleConstraintViolationException(constraintViolationException, request);
		}
		return ResponseEntity.internalServerError().body(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
	}

	/**
	 * {@code NotFoundException}을 처리하여 {@link ErrorResponse}를 반환합니다.
	 *
	 * @param exception {@code NotFoundException}
	 * @param request   HTTP 요청
	 * @return {@link ErrorResponse}를 포함하는 {@link ResponseEntity} 객체
	 */
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> handle(NotFoundException exception, HttpServletRequest request) {
		log.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.from(exception));
	}

	/**
	 * {@code ForbiddenException}을 처리하여 {@link ErrorResponse}를 반환합니다.
	 *
	 * @param exception {@code ForbiddenException}
	 * @param request   HTTP 요청
	 * @return {@link ErrorResponse}를 포함하는 {@link ResponseEntity} 객체
	 */
	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ErrorResponse> handle(ForbiddenException exception, HttpServletRequest request) {
		log.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.from(exception));
	}

	/**
	 * {@code NovaException}을 처리하여 {@link ErrorResponse}를 반환합니다.
	 *
	 * @param exception      {@code NovaException}
	 * @param request 웹 요청
	 * @return {@link ErrorResponse}를 포함하는 {@link ResponseEntity} 객체
	 */
	@ExceptionHandler(NovaException.class)
	protected ResponseEntity<Object> handleNovaException(NovaException exception, WebRequest request) {
		log.error(exception.getMessage(), exception);
		ErrorResponse errorResponse = ErrorResponse.from(exception);
		return handleExceptionInternal(exception, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(FeignClientException.class)
	public ResponseEntity<ErrorResponse> handleFeignClientException(FeignClientException exception) {
		log.error(exception.getMessage(), exception);
		return ResponseEntity.status(exception.getStatus()).body(ErrorResponse.from(exception));
	}

	// /**
	//  * 일반 {@code Exception}을 처리하여 {@link ErrorResponse}를 반환합니다.
	//  *
	//  * @param exception 일반 {@code Exception}
	//  * @param request   HTTP 요청
	//  * @return {@link ErrorResponse}를 포함하는 {@link ResponseEntity} 객체
	//  */
	// @ExceptionHandler(Exception.class)
	// public ResponseEntity<ErrorResponse> handleException(Exception exception, HttpServletRequest request) {
	// 	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	// 		.body(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
	// }

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
		log.error(exception.getMessage(), exception);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.from(ErrorCode.ORDER_BOOK_ALREADY_EXISTS));
	}

	/**
	 * {@code MethodArgumentTypeMismatchException}을 처리하여 {@link ErrorResponse}를 반환합니다.
	 *
	 * @param exception {@code MethodArgumentTypeMismatchException}
	 * @return {@link ErrorResponse}를 포함하는 {@link ResponseEntity} 객체
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		log.error(exception.getMessage(), exception);
		return ResponseEntity.badRequest().body(ErrorResponse.from(ErrorCode.INVALID_ARGUMENT_TYPE));
	}

}
