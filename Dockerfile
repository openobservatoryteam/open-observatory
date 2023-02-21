FROM azul/zulu-openjdk:17 AS builder
WORKDIR /app
COPY gradle/ build.gradle gradlew settings.gradle ./
RUN ./gradlew build || return 0
COPY . ./
RUN ./gradlew bootJar

FROM azul/zulu-openjdk:17.0.4.1-jre
WORKDIR /app
COPY --from=builder /app/build/libs ./
CMD java -jar *.jar
