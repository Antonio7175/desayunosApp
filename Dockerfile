# Usa una imagen oficial de Java 17
FROM openjdk:17-jdk-slim

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR generado por Spring Boot
COPY target/desayunitos-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando que arranca la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
