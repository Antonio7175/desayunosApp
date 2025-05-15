# Usa una imagen oficial de Java 17 optimizada
FROM eclipse-temurin:17-jdk-alpine

# Crea un directorio temporal
VOLUME /tmp

# Define la ruta del JAR generado por Maven
ARG JAR_FILE=target/*.jar

# Copia el JAR al contenedor y lo renombra como app.jar
COPY ${JAR_FILE} app.jar

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "/app.jar"]
