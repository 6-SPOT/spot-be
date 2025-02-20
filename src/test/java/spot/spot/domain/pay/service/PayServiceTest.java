//package spot.spot.domain.pay.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import spot.spot.domain.pay.entity.dto.PayReadyResponse;
//import spot.spot.domain.pay.entity.dto.PayReadyResponseDto;
//
//
//@SpringBootTest
//@Slf4j
//class PayServiceTest {
//
//    @Autowired
//    PayService payService;
//
//    @Test
//    void payReadyElements() {
//        PayReadyResponseDto payReadyResponseDto = payService.payReady("테스트유저", "전구 고쳐주세요", 10000, 0);
//        log.info("redirect_url = {}", payReadyResponseDto.redirectUrl());
//    }
//
//}