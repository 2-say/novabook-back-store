package store.novabook.store.user.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import store.novabook.store.user.member.entity.Member;
import store.novabook.store.user.member.entity.Users;

public interface MemberRepository extends JpaRepository<Member, Long> {
	boolean existsByLoginId(String loginId);;
	Member findByLoginIdAndLoginPassword(String loginId, String password);
}
