package spot.spot.domain.member;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.member.entity.Member;

@Component
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    @Transactional
    public Member updateMember(Long memberId,String phone,Double lat,Double lng){
        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(()->new EntityNotFoundException("member not found"));
        existingMember.updatePhone(phone);
        existingMember.updateAddress(lat,lng);

        return existingMember;
    }
}
