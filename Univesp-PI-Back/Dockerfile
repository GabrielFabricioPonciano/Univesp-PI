# Etapa 1: Usando Maven para construir a aplicação
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiando os arquivos de configuração do Maven e dependências primeiro (para cache)
COPY pom.xml ./
COPY src ./src

# Baixando as dependências para otimizar o cache de builds
RUN mvn dependency:go-offline -B

# Construindo o pacote da aplicação sem executar os testes
RUN mvn clean package -DskipTests

# Etapa 2: Usando JDK para rodar o jar da aplicação
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Copiando o JAR gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Configurando variáveis de ambiente para otimização e ajustes do Java
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Expondo a porta que a aplicação Spring Boot usa
EXPOSE 8080

# Comando de inicialização do Spring Boot
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
