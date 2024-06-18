package store.novabook.store.point.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import store.novabook.store.exception.EntityNotFoundException;
import store.novabook.store.order.entity.Orders;
import store.novabook.store.point.dto.CreatePointHistoryRequest;
import store.novabook.store.point.dto.GetPointHistoryResponse;
import store.novabook.store.point.entity.PointHistory;
import store.novabook.store.point.entity.PointPolicy;
import store.novabook.store.point.repository.PointHistoryRepository;
import store.novabook.store.point.repository.PointPolicyRepository;
import store.novabook.store.user.member.entity.Member;

public class PointHistoryServiceTests {

	@InjectMocks
	private PointHistoryService pointHistoryService;

	@Mock
	private PointPolicyRepository pointPolicyRepository;

	@Mock
	private PointHistoryRepository pointHistoryRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() {
		try {
			MockitoAnnotations.openMocks(this).close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void createPointHistoryTest() {
		Orders mockOrders = mock(Orders.class);
		Member mockMember = mock(Member.class);

		PointPolicy pointPolicy = PointPolicy.builder()
			.reviewPointRate(1000)
			.basicPoint(1000)
			.registerPoint(3000)
			.build();
		PointPolicy savedPointPolicy = pointPolicyRepository.save(pointPolicy);

		PointHistory pointHistory = PointHistory.builder()
			.id(null)
			.orders(mockOrders)
			.pointPolicy(savedPointPolicy)
			.member(mockMember)
			.pointContent("pointContent")
			.pointAmount(1000)
			.build();

		when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);

		pointHistoryService.createPointHistory(CreatePointHistoryRequest.builder()
			.orders(mockOrders)
			.pointPolicy(savedPointPolicy)
			.member(mockMember)
			.pointContent("pointContent")
			.pointAmount(1000)
			.build());

		verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
	}

	@Test
	void getPointHistoryList() {

		PointHistory pointHistory = PointHistory.builder()
			.orders(mock(Orders.class))
			.pointPolicy(mock(PointPolicy.class))
			.member(mock(Member.class))
			.pointContent("pointContent")
			.pointAmount(1000)
			.build();
		List<PointHistory> pointHistoryList = Collections.singletonList(pointHistory);
		Page<PointHistory> page = new PageImpl<>(pointHistoryList, PageRequest.of(0, 10), pointHistoryList.size());

		when(pointHistoryRepository.findAll(any(Pageable.class))).thenReturn(page);

		Page<GetPointHistoryResponse> result = pointHistoryService.getPointHistoryList(PageRequest.of(0, 10));

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("pointContent", result.getContent().get(0).pointContent());
	}

	@Test
	void getPointHistoryListTest_EntityNotFoundException() {
		when(pointHistoryRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

		assertThrows(EntityNotFoundException.class, () -> {
			pointHistoryService.getPointHistoryList(PageRequest.of(0, 10));
		});
	}
}