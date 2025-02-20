// package spot.spot.global.response.handler;
//
//
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import jakarta.annotation.PostConstruct;
// import lombok.Getter;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.web.util.UriComponentsBuilder;
//
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;
//
// @Component("globalVaultResponseKeys")
// @RequiredArgsConstructor
// @Slf4j
// public class GlobalVaultResponseKeys {
//
//     private final RestTemplate restTemplate = new RestTemplate();
//
//     @Value("${vault.connect.url}")
//     private String baseUrl;
//
//     private String resourcesPath = "/src/main/resources/";
//
//     @Value("${github.personal.pat.key}")
//     private String gitPAT;
//
//     private String access_token;
//
//     @Getter
//     private final Map<String, String> vaultKeys = new ConcurrentHashMap<>();
//
//     @Getter
//     private final Map<String, String> vaultKeyFiles = new ConcurrentHashMap<>();
//
//     @PostConstruct
//     private void responseKeyValues() {
//         //https로 바뀌면 api를 보내서 access_token에 값을 넣어주는 로직이 추가되야함.
//         responseAccessToken();
//         responseDirectoryList();
//         setVaultKeys();
//     }
//
//     public void responseAccessToken() {
//         Map<String, String> body = new HashMap<>();
//         body.put("token", gitPAT);
//
//         HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body);
//         ResponseEntity<String> response = restTemplate.exchange(
//                 baseUrl + "auth/github/login",
//                 HttpMethod.POST,
//                 requestEntity,
//                 String.class
//         );
//
//         ObjectMapper objectMapper = new ObjectMapper();
//         JsonNode root;
//         try {
//             root = objectMapper.readTree(response.getBody());
//         } catch (JsonProcessingException e) {
//             log.info("json 형태의 값이 아닙니다", e);
//             throw new RuntimeException(e);
//         }
//
//         access_token = root.path("auth").path("client_token").asText();
//         log.info("access_token = {}", access_token);
//     }
//
//     public void setVaultKeys() {
//         for (String key : vaultKeyFiles.keySet()) {
//             responseKey(key);
//         }
//
//     }
//
//     public void responseDirectoryList() {
//         String urlWithParams = UriComponentsBuilder.fromHttpUrl(baseUrl + "be/metadata/")
//                 .queryParam("list", "true")
//                 .toUriString();
//
//         HttpHeaders httpHeaders = new HttpHeaders();
//         httpHeaders.set("Authorization", "Bearer " + access_token);
//
//         HttpEntity<String> headerSet = new HttpEntity<>(httpHeaders);
//
//         ResponseEntity<String> response = restTemplate.exchange(
//                 urlWithParams,
//                 HttpMethod.GET,
//                 headerSet,
//                 String.class
//         );
//
//         ObjectMapper objectMapper = new ObjectMapper();
//         JsonNode root;
//         try {
//             root = objectMapper.readTree(response.getBody());
//         } catch (JsonProcessingException e) {
//             log.info("json 형태의 값이 아닙니다", e);
//             throw new RuntimeException(e);
//         }
//
//         JsonNode keysNode = root.path("data").path("keys");
//
//         if (keysNode.isArray()) {  // 배열인지 확인
//             for (JsonNode key : keysNode) {
//                 vaultKeyFiles.put(key.asText(), key.asText());
//             }
//         }
//     }
//
//     public void responseKey(String domainName) {
//         HttpHeaders headers = new HttpHeaders();
//         headers.set("Authorization", "Bearer " + access_token);
//
//         HttpEntity<String> httpHeaders = new HttpEntity<>(headers);
//         ResponseEntity<String> response = restTemplate.exchange(
//                 baseUrl + "be/data/" + domainName,
//                 HttpMethod.GET,
//                 httpHeaders,
//                 String.class
//         );
//
//         ObjectMapper objectMapper = new ObjectMapper();
//         JsonNode root;
//         try {
//             root = objectMapper.readTree(response.getBody());
//         } catch (JsonProcessingException e) {
//             log.info("json 형태의 값이 아닙니다", e);
//             throw new RuntimeException(e);
//         }
//
//         JsonNode dataNode = root.path("data").path("data");
//
//         Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();
//
//         while (fields.hasNext()) {
//             Map.Entry<String, JsonNode> field = fields.next();
//             vaultKeys.put(field.getKey(), field.getValue().asText());
//         }
//     }
//
// }
