# Multi-stage Dockerfile para aplicação Spring Boot

# Estágio 1: Build da aplicação
FROM maven:3.8.6-openjdk-11-slim AS builder

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY checkstyle.xml .

# Baixar dependências (cache layer)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests -B

# Estágio 2: Runtime da aplicação
FROM openjdk:11-jre-slim

# Metadados da imagem
LABEL maintainer="seu.email@exemplo.com"
LABEL version="1.0.0"
LABEL description="Template de arquitetura Java com Spring Boot"

# Instalar dependências do sistema
RUN apt-get update && apt-get install -y \
    curl \
    dumb-init \
    && rm -rf /var/lib/apt/lists/*

# Criar usuário não-root para segurança
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Definir diretório de trabalho
WORKDIR /app

# Copiar JAR da aplicação do estágio de build
ARG JAR_FILE=target/my-project.jar
COPY --from=builder /app/${JAR_FILE} app.jar

# Criar diretórios necessários
RUN mkdir -p /app/logs /app/data && \
    chown -R appuser:appuser /app

# Configurar variáveis de ambiente
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080

# Expor porta da aplicação
EXPOSE ${SERVER_PORT}

# Configurar health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/api/actuator/health || exit 1

# Mudar para usuário não-root
USER appuser

# Comando de inicialização usando dumb-init para proper signal handling
ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

