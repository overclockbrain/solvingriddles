# 1. ビルドする環境（MavenとJDKが入ってるやつ）
FROM maven:3-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# 2. 動かす環境（軽量なJava環境）
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]