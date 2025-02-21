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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
public class FireBaseConfig {
    //
    @Value("${firebase.credentials}")
    private String firebaseCredentials;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(firebaseCredentials.getBytes());

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .setProjectId("soomin-dea03")
                .build();
            FirebaseApp.initializeApp(options);
            log.info("✅ Firebase 초기화 완료!");
        } else {
            log.warn("⚠️ Firebase는 이미 초기화 되었습니다.");
        }

        FirebaseApp existingApp = FirebaseApp.getInstance();
        return FirebaseMessaging.getInstance();
    }
}
