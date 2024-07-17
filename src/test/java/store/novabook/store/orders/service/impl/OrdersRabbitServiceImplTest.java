package store.novabook.store.orders.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import store.novabook.store.book.entity.Book;
import store.novabook.store.book.entity.BookStatus;
import store.novabook.store.book.entity.BookStatusEnum;
import store.novabook.store.book.repository.BookRepository;
import store.novabook.store.book.repository.BookStatusRepository;
import store.novabook.store.member.entity.MemberGradeHistory;
import store.novabook.store.member.entity.MemberGradePolicy;
import store.novabook.store.member.repository.MemberGradeHistoryRepository;
import store.novabook.store.member.repository.MemberRepository;
import store.novabook.store.orders.dto.OrderSagaMessage;
import store.novabook.store.orders.dto.request.BookIdAndQuantityDTO;
import store.novabook.store.orders.dto.request.CreateDeliveryFeeRequest;
import store.novabook.store.orders.dto.request.CreateWrappingPaperRequest;
import store.novabook.store.orders.dto.request.OrderAddressInfo;
import store.novabook.store.orders.dto.request.OrderReceiverInfo;
import store.novabook.store.orders.dto.request.OrderSenderInfo;
import store.novabook.store.orders.dto.request.OrderTemporaryForm;
import store.novabook.store.orders.dto.request.OrderTemporaryNonMemberForm;
import store.novabook.store.orders.dto.request.PaymentRequest;
import store.novabook.store.orders.entity.DeliveryFee;
import store.novabook.store.orders.entity.WrappingPaper;
import store.novabook.store.orders.repository.DeliveryFeeRepository;
import store.novabook.store.orders.repository.OrdersBookRepository;
import store.novabook.store.orders.repository.OrdersRepository;
import store.novabook.store.orders.repository.OrdersStatusRepository;
import store.novabook.store.orders.repository.RedisOrderNonMemberRepository;
import store.novabook.store.orders.repository.RedisOrderRepository;
import store.novabook.store.orders.repository.WrappingPaperRepository;
import store.novabook.store.payment.repository.PaymentRepository;
import store.novabook.store.point.entity.PointPolicy;
import store.novabook.store.point.repository.PointPolicyRepository;

@ExtendWith(MockitoExtension.class)
class OrdersRabbitServiceImplTest {

	@Mock
	private OrdersRepository ordersRepository;
	@Mock
	private DeliveryFeeRepository deliveryFeeRepository;
	@Mock
	private WrappingPaperRepository wrappingPaperRepository;
	@Mock
	private OrdersStatusRepository ordersStatusRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private BookStatusRepository bookStatusRepository;
	@Mock
	private OrdersBookRepository ordersBookRepository;
	@Mock
	private PointPolicyRepository pointPolicyRepository;
	@Mock
	private RedisOrderRepository redisOrderRepository;
	@Mock
	private RedisOrderNonMemberRepository redisOrderNonMemberRepository;
	@Mock
	private RabbitTemplate rabbitTemplate;
	@Mock
	private MemberGradeHistoryRepository memberGradeHistoryRepository;
	@Mock
	private PaymentRepository paymentRepository;

	@InjectMocks
	private OrdersRabbitServiceImpl ordersRabbitServiceImpl;

	private OrderSagaMessage orderSagaMessage;
	private OrderTemporaryNonMemberForm orderForm;
	private BookStatus bookStatus;

	@BeforeEach
	public void setUp() {
		setUpCommonOrderForm();
	}

	private void setUpCommonOrderForm() {
		PaymentRequest testPaymentRequest = PaymentRequest.builder()
			.orderCode("TESTCODE1")
			.build();

		orderSagaMessage = OrderSagaMessage.builder()
			.earnPointAmount(1000L)
			.paymentRequest(testPaymentRequest)
			.bookAmount(1000L)
			.calculateTotalAmount(2000L)
			.couponAmount(1000L)
			.status("PROCEED_TEST")
			.build();

		BookIdAndQuantityDTO book1 = new BookIdAndQuantityDTO(1L, 2);
		BookIdAndQuantityDTO book2 = new BookIdAndQuantityDTO(2L, 1);

		OrderSenderInfo senderInfo = OrderSenderInfo.builder()
			.name("Sender Name")
			.phone("123-456-7890")
			.build();

		OrderReceiverInfo receiverInfo = OrderReceiverInfo.builder()
			.name("Receiver Name")
			.phone("098-765-4321")
			.orderAddressInfo(OrderAddressInfo.builder()
				.streetAddress("123 Main St")
				.detailAddress("Apt 4B")
				.build())
			.build();

		orderForm = OrderTemporaryNonMemberForm.builder()
			.orderCode("ORDER12345")
			.cartUUID("CART_UUID_67890")
			.books(List.of(book1, book2))
			.wrappingPaperId(1L)
			.couponId(2L)
			.usePointAmount(100)
			.deliveryDate(LocalDate.now().plusDays(3))
			.deliveryId(3L)
			.orderSenderInfo(senderInfo)
			.orderReceiverInfo(receiverInfo)
			.build();

		bookStatus = BookStatus.of(BookStatusEnum.FOR_SALE.getKoreanValue());
	}

	@Test
	@DisplayName("가주문서 비회원 테스트 - 성공 로직")
	void confirmOrderForm() {
		mockCommonRepositories();
		when(redisOrderNonMemberRepository.findById(any())).thenReturn(Optional.of(orderForm));

		ordersRabbitServiceImpl.confirmOrderForm(orderSagaMessage);

		verify(bookRepository, times(2)).findById(anyLong());
		verify(bookRepository, times(2)).save(any(Book.class));
		verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(OrderSagaMessage.class));
		assertEquals("SUCCESS_CONFIRM_ORDER_FORM", orderSagaMessage.getStatus());
	}

	@Test
	@DisplayName("가주문서 회원 테스트 - 성공 로직")
	void confirmOrderFormForMember() {
		PaymentRequest testPaymentRequest = PaymentRequest.builder()
			.orderCode("TESTCODE1")
			.memberId(1L)
			.build();

		orderSagaMessage = OrderSagaMessage.builder()
			.earnPointAmount(1000L)
			.paymentRequest(testPaymentRequest)
			.bookAmount(1000L)
			.calculateTotalAmount(2000L)
			.couponAmount(1000L)
			.status("PROCEED_TEST")
			.build();

		BookIdAndQuantityDTO book1 = new BookIdAndQuantityDTO(1L, 2);
		BookIdAndQuantityDTO book2 = new BookIdAndQuantityDTO(2L, 1);

		OrderSenderInfo senderInfo = OrderSenderInfo.builder()
			.name("Sender Name")
			.phone("123-456-7890")
			.build();

		OrderReceiverInfo receiverInfo = OrderReceiverInfo.builder()
			.name("Receiver Name")
			.phone("098-765-4321")
			.orderAddressInfo(OrderAddressInfo.builder()
				.streetAddress("123 Main St")
				.detailAddress("Apt 4B")
				.build())
			.build();

		OrderTemporaryForm orderTemporaryForm = OrderTemporaryForm.builder()
			.memberId(1L)
			.orderCode("CODE")
			.books(List.of(book1, book2))
			.deliveryDate(LocalDate.now())
			.couponId(1L)
			.orderReceiverInfo(receiverInfo)
			.orderSenderInfo(senderInfo)
			.build();

		bookStatus = BookStatus.of(BookStatusEnum.FOR_SALE.getKoreanValue());

		when(pointPolicyRepository.findTopByOrderByCreatedAtDesc()).thenReturn(
			Optional.ofNullable(PointPolicy.builder().basicPointRate(1).reviewPoint(1).build()));

		when(memberGradeHistoryRepository.findFirstByMemberIdOrderByCreatedAtDesc(anyLong())).thenReturn(
			Optional.ofNullable(MemberGradeHistory.builder().memberGradePolicy(MemberGradePolicy.builder()
				.maxRange(1L)
				.saveRate(1L)
				.minRange(1L)
				.build()).quarter(LocalDate.now().atStartOfDay()).build()));

		mockCommonRepositories();
		when(redisOrderRepository.findById(any())).thenReturn(Optional.of(orderTemporaryForm));

		ordersRabbitServiceImpl.confirmOrderForm(orderSagaMessage);

		verify(bookRepository, times(2)).findById(anyLong());
		verify(bookRepository, times(2)).save(any(Book.class));
		verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(OrderSagaMessage.class));
		assertEquals("SUCCESS_CONFIRM_ORDER_FORM", orderSagaMessage.getStatus());
	}

	private void mockCommonRepositories() {
		when(wrappingPaperRepository.findById(any())).thenReturn(Optional.of(WrappingPaper.builder()
			.request(CreateWrappingPaperRequest.builder().price(1000L).build()).build()));

		when(deliveryFeeRepository.findById(any())).thenReturn(Optional.of(DeliveryFee.builder()
			.createDeliveryFeeRequest(CreateDeliveryFeeRequest.builder().fee(1000L).build()).build()));

		when(bookRepository.findById(any())).thenReturn(Optional.of(Book.builder()
			.inventory(10).price(5000L).discountPrice(4000L)
			.bookStatus(bookStatus).build()));
	}

	@Test
	@DisplayName("보상 트랜잭션 재고 증가 로직 테스트 - 성공")
	void compensateConfirmOrderForm() {
		when(bookRepository.findById(any())).thenReturn(Optional.of(Book.builder()
			.inventory(10).price(5000L).discountPrice(4000L)
			.bookStatus(bookStatus).build()));

		when(redisOrderNonMemberRepository.findById(any())).thenReturn(Optional.of(orderForm));

		ordersRabbitServiceImpl.compensateConfirmOrderForm(orderSagaMessage);

		verify(bookRepository, times(1)).findById(anyLong());
	}

	@Test
	void saveSagaOrder() {
		// Implement test logic here
	}

	@Test
	void orderCancel() {
		// Implement test logic here
	}
}
