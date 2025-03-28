package spot.spot.global.klaytn.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

@Slf4j
@Component
@Getter
public class ExchangeRateByBithumbApi {

    @Value("${klaytn.bithumb.kaia.krw.api.url}")
    private String bithumbApiURL;
    private double changeRateCoin;
    private double changeRateCash;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void coinChangeRate() {
        log.info("URL: {}", bithumbApiURL);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(bithumbApiURL, String.class);

            if (response.getBody() == null) {
                throw new GlobalException(ErrorCode.EMPTY_RESPONSE);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootArray = objectMapper.readTree(response.getBody());

            if (!rootArray.isArray() || rootArray.isEmpty()) {
                log.error("API response is empty or not an array.");
                throw new GlobalException(ErrorCode.EMPTY_RESPONSE);
            }

            JsonNode firstObject = rootArray.get(0);
            JsonNode tradePriceNode = firstObject.get("trade_price");

            if (tradePriceNode == null) {
                log.error("Missing field: trade_price");
                throw new GlobalException(ErrorCode.FIELD_NOT_FOUND);
            }

            changeRateCash = tradePriceNode.asDouble();
            changeRateCoin = 1.0 / changeRateCash;

            log.info("Exchange rate updated: 1 KAIA = {} KRW, 1 KRW = {} KAIA", changeRateCash, changeRateCoin);
        } catch (Exception e) {
            log.error("Failed to fetch exchange rate. Using default values. Error: {}", e.getMessage());

            // 기본값 설정 (API가 실패해도 앱이 실행되도록)
            changeRateCash = 1.0;
            changeRateCoin = 1.0;
        }
    }

    public double exchangeToKaia(int cash) {
        if(cash <= 0) {
            throw new GlobalException(ErrorCode.LOW_AMOUNT);
        }
        log.info("exchanged kaia = {}", cash * changeRateCoin);
        return cash * changeRateCoin;
    }

    public int exchangeToCash(double kaia) {
        if(kaia <= 0) {
            throw new GlobalException(ErrorCode.LOW_AMOUNT);
        }
        log.info("exchanged Cash = {}", kaia * changeRateCash);
        return (int) (kaia * changeRateCash);
    }

}
