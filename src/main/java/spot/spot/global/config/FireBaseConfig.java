package spot.spot.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FireBaseConfig {

    @Value("${firebase.config}")
    private String fireBaseConfig;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        if(FirebaseApp.getApps().isEmpty()) {
            ByteArrayInputStream serviceAccount = new ByteArrayInputStream(fireBaseConfig.getBytes(
                StandardCharsets.UTF_8));
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);
        }
        return FirebaseMessaging.getInstance();
    }
}
