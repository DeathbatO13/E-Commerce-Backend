# Etapa 1: compila el jar con Maven (las dependencias quedan en una capa cacheada aparte)
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
# Los tests corren en CI (mvn verify); acá solo se empaqueta
RUN mvn -q -B -DskipTests package

# Etapa 2: solo el JRE y el jar
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN useradd --system --uid 1001 spring
COPY --from=build /app/target/*.jar app.jar
USER spring
EXPOSE 8085
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
