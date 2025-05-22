# Usa Java 17 JDK como base
FROM amazoncorretto:17-alpine-jdk

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR del proyecto
COPY target/foods-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto (Render necesita esto aunque lo maneja internamente)
EXPOSE 8080

# Ejecuta la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
