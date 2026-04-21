# syntax=docker/dockerfile:1.6

# ==================== Build Stage ====================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Gradle wrapper & 빌드 설정 파일 먼저 복사 (레이어 캐싱)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew

# 의존성 캐싱 레이어
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사 후 빌드
COPY src src
COPY detekt.yml .

# bootJar만 실행 (테스트/detekt 스킵 - CI에서 이미 실행)
RUN ./gradlew bootJar --no-daemon -x test -x detekt -x spotlessCheck

# ==================== Runtime Stage ====================
FROM eclipse-temurin:21-jre

WORKDIR /app

# 보안: 비-루트 유저로 실행
RUN groupadd -r spring && useradd -r -g spring spring

# 빌드된 jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar
RUN chown spring:spring app.jar

USER spring

EXPOSE 8080

# JVM 메모리 옵션 (EC2 t3.micro 기준 조정 가능)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
