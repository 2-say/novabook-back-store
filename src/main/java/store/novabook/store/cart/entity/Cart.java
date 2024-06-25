package store.novabook.store.cart.entity;

import static jakarta.persistence.GenerationType.*;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.novabook.store.user.member.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Cart {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@NotNull
	private Boolean isExposed;

	@NotNull
	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@Builder
	public Cart(Member member, Boolean isExposed) {
		this.member = member;
		this.isExposed = isExposed;
	}

	public static Cart of(Member member, Boolean isExposed) {
		return Cart.builder().member(member).isExposed(isExposed).build();
	}

}
