package spot.spot.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
public class FireBaseConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        ClassPathResource resource = new ClassPathResource("firebase-service-key.json");

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .setProjectId("soomin-dea03")
                .build();
            FirebaseApp.initializeApp(options);
            log.info("✅ Firebase 초기화 완료!");
        } else {
            log.warn("⚠️ Firebase는 이미 초기화 되었습니다.");
        }

        FirebaseApp existingApp = FirebaseApp.getInstance();
        log.info("🔥 Firebase 프로젝트 ID: {}", existingApp.getOptions().getProjectId());

        return FirebaseMessaging.getInstance();
    }
}
