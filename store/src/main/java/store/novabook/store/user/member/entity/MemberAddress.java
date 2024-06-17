package store.novabook.store.user.member.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	private String nickname;

	@NotNull
	private String memberAddressDetail;

	@NotNull
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;


	@NotNull
	@ManyToOne
	@JoinColumn(name = "street_address_id")
	private StreetAddress streetAddress;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

}

