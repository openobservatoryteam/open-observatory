FROM azul/zulu-openjdk:17 AS builder
WORKDIR /app
COPY gradle/wrapper ./gradle/wrapper
COPY build.gradle gradlew settings.gradle ./
RUN ./gradlew build || return 0
COPY . ./
RUN ./install_certs.sh
RUN ./gradlew bootJar

FROM azul/zulu-openjdk-alpine:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs ./
CMD java -jar open-observatory.jar
