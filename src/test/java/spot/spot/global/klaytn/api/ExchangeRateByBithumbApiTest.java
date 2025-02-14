package spot.spot.global.klaytn.api;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spot.spot.global.response.format.GlobalException;

@SpringBootTest
@Slf4j
class ExchangeRateByBithumbApiTest {

    @Autowired
    ExchangeRateByBithumbApi bithumbApi;

    @Test
    public void exchangeToKaiaOrCash() {
        int amount = 1000;
        double kaia = bithumbApi.exchangeToKaia(amount);
        log.info("exchanged kaia = {}", kaia);
        int cash = bithumbApi.exchangeToCash(5.050505050505051);
        log.info("exchanged Cash = {}", cash);

        Assertions.assertThat(amount).isEqualTo(cash);
    }

    @Test
    public void inValidAmountExceptionExchangeToKaia() {
        Assertions.assertThatThrownBy(() -> bithumbApi.exchangeToKaia(0))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    public void inValidAmountExceptionExchangeToCash() {
        Assertions.assertThatThrownBy(() -> bithumbApi.exchangeToCash(0))
                .isInstanceOf(GlobalException.class);
    }
}