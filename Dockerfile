FROM eclipse-temurin:17-jdk AS runtime
WORKDIR /app

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# JAR 파일 복사 (GitHub Actions의 빌드 결과물을 사용)
COPY build/libs/spot-0.0.1-SNAPSHOT.jar /app/spot-be.jar

# 실행 명령어
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/app/spot-be.jar"]