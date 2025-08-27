package spot.spot.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Slf4j
@Configuration
public class FireBaseConfig {

    @Value("${firebase.credentials}")
    private Resource serviceAccount;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        try (InputStream inputStream = serviceAccount.getInputStream()) {
            GoogleCredentials creds = GoogleCredentials.fromStream(inputStream);
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(creds)
                .build();

            FirebaseApp app = FirebaseApp.getApps().isEmpty()
                ? FirebaseApp.initializeApp(options, "spot-app")
                : FirebaseApp.getInstance("spot-app");

            log.info("✅ Firebase 초기화 완료!");
            return FirebaseMessaging.getInstance(app);
        }
    }
}
