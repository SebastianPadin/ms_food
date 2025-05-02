FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiar y construir el código fuente dentro del contenedor
COPY . .
RUN ./mvnw clean package

# Copiar el archivo JAR generado
COPY target/FoodCost-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Configurar el punto de entrada
ENTRYPOINT ["java", "-jar", "app.jar"]
