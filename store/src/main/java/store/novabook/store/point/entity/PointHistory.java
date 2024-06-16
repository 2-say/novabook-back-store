package store.novabook.store.point.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PointHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long ordersId;

	@NotNull
	private Long pointPolicyId;

	@NotNull
	private String pointContent;

	private Long pointAmount;

	@NotNull
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
