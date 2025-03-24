package com.RollinMoment.RollinMomentServer.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FcmConfig {

	@PostConstruct
	public void initializeFirebaseApp() {
		try {
			// Firebase 서비스 계정 키 파일 경로
			FileInputStream serviceAccount =
					new FileInputStream("src/main/resources/config/firebase-adminsdk.json");

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
//					.setDatabaseUrl("https://your-database-url.firebaseio.com") // DB URL 초기화 (필요시)
					.build();

			if (FirebaseApp.getApps().isEmpty()) { // Firebase 앱이 초기화되지 않은 경우
				FirebaseApp.initializeApp(options);
			}
		} catch (IOException e) {
			throw new IllegalStateException("Failed to initialize Firebase app", e);
		}
	}
}

