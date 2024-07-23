package store.novabook.store.orders.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.novabook.store.common.exception.ErrorCode;
import store.novabook.store.common.exception.NotFoundException;
import store.novabook.store.orders.dto.request.CreateDeliveryFeeRequest;
import store.novabook.store.orders.dto.response.CreateResponse;
import store.novabook.store.orders.dto.response.GetDeliveryFeeResponse;
import store.novabook.store.orders.entity.DeliveryFee;
import store.novabook.store.orders.repository.DeliveryFeeRepository;
import store.novabook.store.orders.service.DeliveryFeeService;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryFeeServiceImpl implements DeliveryFeeService {

	private final DeliveryFeeRepository deliveryFeeRepository;

	@Override
	public CreateResponse createFee(CreateDeliveryFeeRequest request) {
		DeliveryFee deliveryFee = new DeliveryFee(request);
		deliveryFeeRepository.save(deliveryFee);
		return new CreateResponse(deliveryFee.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<GetDeliveryFeeResponse> findAllDeliveryFees(Pageable pageable) {
		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
		Page<DeliveryFee> deliveryFees = deliveryFeeRepository.findAll(pageable);
		return deliveryFees.map(GetDeliveryFeeResponse::from);
	}

	@Override
	@Transactional(readOnly = true)
	public List<GetDeliveryFeeResponse> findAllDeliveryFeeList() {
		List<DeliveryFee> deliveryFees = deliveryFeeRepository.findAll();
		List<GetDeliveryFeeResponse> responses = new ArrayList<>();
		for (DeliveryFee deliveryFee : deliveryFees) {
			responses.add(GetDeliveryFeeResponse.from(deliveryFee));
		}
		return responses;
	}

	@Override
	@Transactional(readOnly = true)
	public GetDeliveryFeeResponse getDeliveryFee(Long id) {
		return GetDeliveryFeeResponse.from(deliveryFeeRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCode.DELIVERY_FEE_NOT_FOUND)));
	}

	@Override
	@Transactional(readOnly = true)
	public GetDeliveryFeeResponse getRecentDeliveryFee() {
		return GetDeliveryFeeResponse.from(
			deliveryFeeRepository.findFirstByOrderByCreatedAtDesc()
				.orElseThrow(() -> new NotFoundException(ErrorCode.DELIVERY_FEE_NOT_FOUND)));
	}
}
