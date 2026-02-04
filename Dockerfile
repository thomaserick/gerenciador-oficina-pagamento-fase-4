FROM eclipse-temurin:17-jdk-jammy

# Instala o New Relic Agent
RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml


# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR gerado pelo job anterior
COPY app/*.jar app.jar

EXPOSE 8083

# Comando de inicialização
ENTRYPOINT ["java","-javaagent:/usr/local/newrelic/newrelic.jar","-jar","app.jar"]

