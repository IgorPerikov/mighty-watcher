FROM openjdk:8u222-jdk-stretch AS BUILD_IMAGE
WORKDIR /
COPY . /
RUN ./gradlew build

FROM openjdk:8u222-jre-stretch
COPY --from=BUILD_IMAGE /build/libs/mighty-watcher.jar .
CMD ["java", "-Xmx1500m", "-Xms200m", "-XX:+UseStringDeduplication", "-jar", "mighty-watcher.jar"]
