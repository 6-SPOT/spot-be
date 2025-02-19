package spot.spot.global.response.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("globalVaultResponseKeys")
@RequiredArgsConstructor
@Slf4j
public class GlobalVaultResponseKeys {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${vault.connect.url}")
    private String baseUrl;

    private String resourcesPath = "/src/main/resources/";

    private String access_token;

    @Getter
    private final Map<String, String> vaultKeys = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, String> vaultKeyFiles = new ConcurrentHashMap<>();

    @PostConstruct
    private void responseAccessToken() {
        //https로 바뀌면 api를 보내서 access_token에 값을 넣어주는 로직이 추가되야함.
        access_token = "root";
        responseDirectoryList();
        setVaultKeys();
    }

    public void setVaultKeys() {
        for (String key : vaultKeyFiles.keySet()) {
            responseKey(key);
        }

    }

    public void responseDirectoryList() {
        String urlWithParams = UriComponentsBuilder.fromHttpUrl(baseUrl + "metadata/")
                .queryParam("list", "true")
                .toUriString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + access_token);

        HttpEntity<String> headerSet = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                urlWithParams,
                HttpMethod.GET,
                headerSet,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root;
        try {
            root = objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            log.info("json 형태의 값이 아닙니다", e);
            throw new RuntimeException(e);
        }

        JsonNode keysNode = root.path("data").path("keys");

        if (keysNode.isArray()) {  // 배열인지 확인
            for (JsonNode key : keysNode) {
                vaultKeyFiles.put(key.asText(), key.asText());
            }
        }
    }

    public void responseKey(String domainName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);

        HttpEntity<String> httpHeaders = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/data/" + domainName,
                HttpMethod.GET,
                httpHeaders,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root;
        try {
            root = objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            log.info("json 형태의 값이 아닙니다", e);
            throw new RuntimeException(e);
        }

        JsonNode dataNode = root.path("data").path("data");

        Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            vaultKeys.put(field.getKey(), field.getValue().asText());
        }
    }

}
