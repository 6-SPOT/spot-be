package spot.spot.global.klaytn.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

@Slf4j
@Component
public class ExchangeRateByBithumbApi {

    @Value("${klaytn.bithumb.kaia.krw.api.url}")
    private String bithumbApiURL;
    private double changeRateCoin;
    private double changeRateCash;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void coinChangeRate() {
        ResponseEntity<String> response = restTemplate.getForEntity(bithumbApiURL, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootArray = objectMapper.readTree(response.getBody());
            if (rootArray.isArray() && !rootArray.isEmpty()) {
                JsonNode firstObject = rootArray.get(0);
                JsonNode tradePriceNode = firstObject.get("trade_price");
                if (tradePriceNode != null) {
                    changeRateCash = tradePriceNode.asDouble();
                    double tradePrice = tradePriceNode.asDouble();
                    changeRateCoin = 1.0 / tradePrice;
                } else {
                    throw new GlobalException(ErrorCode.FIELD_NOT_FOUND);
                }
            } else {
                throw new GlobalException(ErrorCode.EMPTY_RESPONSE);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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
