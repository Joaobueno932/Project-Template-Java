# Template de Arquitetura Java Perfeito

[![CI/CD Pipeline](https://github.com/seuusuario/my-project/workflows/CI/CD%20Pipeline/badge.svg)](https://github.com/seuusuario/my-project/actions)
[![codecov](https://codecov.io/gh/seuusuario/my-project/branch/main/graph/badge.svg)](https://codecov.io/gh/seuusuario/my-project)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=my-project&metric=alert_status)](https://sonarcloud.io/dashboard?id=my-project)
[![Java 11](https://img.shields.io/badge/java-11-blue.svg)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
[![Spring Boot 2.7](https://img.shields.io/badge/Spring%20Boot-2.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Um template completo e bem estruturado para projetos Java 11 com Spring Boot que segue as melhores práticas de desenvolvimento, arquitetura limpa, testes abrangentes, qualidade de código e deployment automatizado.

## ✨ Características

### 🏗️ Arquitetura
- **Arquitetura em Camadas** com separação clara de responsabilidades
- **Spring Boot 2.7** com configuração moderna
- **JPA/Hibernate** para persistência de dados
- **Spring Security** para autenticação e autorização
- **MapStruct** para mapeamento entre DTOs e entidades
- **Lombok** para redução de boilerplate

### 🧪 Testes
- **JUnit 5** para testes unitários
- **Mockito** para mocking
- **TestContainers** para testes de integração
- **Cobertura de código** com JaCoCo (>80%)
- **Testes de API** com Spring Boot Test

### 🔍 Qualidade de Código
- **Checkstyle** para verificação de estilo
- **PMD** para análise estática
- **SpotBugs** para detecção de bugs
- **SonarCloud** para análise de qualidade
- **OWASP Dependency Check** para segurança

### 🚀 CI/CD
- **GitHub Actions** para pipeline automatizado
- **Docker** para containerização
- **Multi-stage builds** para otimização
- **Deploy automatizado** para staging e produção
- **Notificações** via Slack

### 📊 Observabilidade
- **Spring Actuator** para métricas
- **Prometheus** para coleta de métricas
- **Grafana** para visualização
- **ELK Stack** para logs centralizados

## 🚀 Início Rápido

### Pré-requisitos

- Java 11 ou superior
- Maven 3.6+
- Docker e Docker Compose (opcional)
- Git

### Instalação

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seuusuario/my-project.git
   cd my-project
   ```

2. **Execute com Maven:**
   ```bash
   mvn spring-boot:run
   ```

3. **Ou use Docker Compose:**
   ```bash
   docker-compose up -d
   ```

### Acesso à Aplicação

- **API REST**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator
- **H2 Console** (dev): http://localhost:8080/h2-console

### Credenciais Padrão

- **Username**: admin
- **Password**: admin123

## 📁 Estrutura do Projeto

```
my-project/
├── src/
│   ├── main/
│   │   ├── java/com/example/myproject/
│   │   │   ├── MyProjectApplication.java    # Classe principal
│   │   │   ├── config/                      # Configurações
│   │   │   ├── controller/                  # Controllers REST
│   │   │   ├── service/                     # Lógica de negócio
│   │   │   ├── repository/                  # Acesso a dados
│   │   │   ├── model/                       # Entidades JPA
│   │   │   ├── dto/                         # Data Transfer Objects
│   │   │   ├── exception/                   # Exceções customizadas
│   │   │   └── util/                        # Utilitários
│   │   └── resources/
│   │       ├── application.yml              # Configurações da aplicação
│   │       └── db/migration/                # Scripts de migração
│   └── test/                                # Testes
├── docs/                                    # Documentação
├── scripts/                                 # Scripts de automação
├── .github/workflows/                       # GitHub Actions
├── docker-compose.yml                      # Orquestração de containers
├── Dockerfile                              # Imagem Docker
├── pom.xml                                 # Configuração Maven
└── README.md                               # Este arquivo
```

## 🛠️ Desenvolvimento

### Comandos Maven Úteis

```bash
# Executar aplicação
mvn spring-boot:run

# Executar testes
mvn test

# Executar testes de integração
mvn verify

# Gerar relatório de cobertura
mvn jacoco:report

# Verificar qualidade de código
mvn checkstyle:check pmd:check spotbugs:check

# Build completo
mvn clean package

# Build da imagem Docker
mvn dockerfile:build
```

### Perfis de Execução

- **dev** (padrão): Desenvolvimento com H2 em memória
- **test**: Testes com H2 em memória
- **prod**: Produção com PostgreSQL

```bash
# Executar com perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Configuração de Qualidade de Código

O projeto inclui configuração completa para:

- **Checkstyle**: Verificação de estilo baseada no Google Java Style Guide
- **PMD**: Análise estática para detectar problemas comuns
- **SpotBugs**: Detecção de bugs potenciais
- **JaCoCo**: Cobertura de código com limite mínimo de 80%

### Banco de Dados

#### Desenvolvimento (H2)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:devdb
    username: sa
    password: 
```

#### Produção (PostgreSQL)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myproject
    username: myproject
    password: password
```

## 🧪 Testes

### Estrutura de Testes

- **Testes Unitários**: `src/test/java/.../service/`
- **Testes de Integração**: `src/test/java/.../integration/`
- **Testes de Controller**: `src/test/java/.../controller/`
- **Testes de Repository**: `src/test/java/.../repository/`

### Executar Testes

```bash
# Todos os testes
mvn test

# Apenas testes unitários
mvn test -Dtest="**/*Test"

# Apenas testes de integração
mvn test -Dtest="**/*IT"

# Com cobertura
mvn test jacoco:report
```

### TestContainers

Para testes de integração com banco real:

```java
@Testcontainers
class UserServiceIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Test
    void shouldCreateUser() {
        // teste com banco real
    }
}
```

## 🐳 Docker

### Build da Imagem

```bash
# Build local
docker build -t my-project .

# Build com Maven
mvn dockerfile:build
```

### Executar com Docker

```bash
# Aplicação simples
docker run -p 8080:8080 my-project

# Com Docker Compose (completo)
docker-compose up -d

# Apenas aplicação e banco
docker-compose up -d app db

# Com monitoramento
docker-compose --profile monitoring up -d

# Com logging
docker-compose --profile logging up -d
```

### Serviços Disponíveis

- **app**: Aplicação Spring Boot (porta 8080)
- **db**: PostgreSQL (porta 5432)
- **redis**: Redis (porta 6379)
- **prometheus**: Métricas (porta 9090)
- **grafana**: Dashboards (porta 3000)
- **elasticsearch**: Logs (porta 9200)
- **kibana**: Visualização de logs (porta 5601)

## 📊 Monitoramento

### Métricas com Actuator

Endpoints disponíveis:
- `/actuator/health` - Status da aplicação
- `/actuator/metrics` - Métricas da aplicação
- `/actuator/prometheus` - Métricas para Prometheus
- `/actuator/info` - Informações da aplicação

### Prometheus + Grafana

1. Inicie os serviços de monitoramento:
   ```bash
   docker-compose --profile monitoring up -d
   ```

2. Acesse o Grafana: http://localhost:3000
   - User: admin
   - Password: admin

3. Configure o Prometheus como data source: http://prometheus:9090

### Logs com ELK Stack

1. Inicie os serviços de logging:
   ```bash
   docker-compose --profile logging up -d
   ```

2. Acesse o Kibana: http://localhost:5601

## 🔒 Segurança

### Configuração de Segurança

- **Spring Security** configurado com autenticação básica
- **CORS** configurado para desenvolvimento
- **HTTPS** suportado em produção
- **Headers de segurança** configurados

### Variáveis de Ambiente Sensíveis

```bash
# Banco de dados
DATABASE_URL=jdbc:postgresql://localhost:5432/myproject
DATABASE_USERNAME=myproject
DATABASE_PASSWORD=password

# JWT (se implementado)
JWT_SECRET=mySecretKey
JWT_EXPIRATION=86400000

# Admin user
ADMIN_PASSWORD=admin123
```

## 🚀 Deploy

### GitHub Actions

O pipeline de CI/CD inclui:

1. **Testes e Qualidade**: Execução de testes e verificações
2. **Build**: Compilação e empacotamento
3. **Segurança**: Verificação de vulnerabilidades
4. **Docker**: Build e push da imagem
5. **Deploy**: Deploy automático para staging/produção

### Configurar Secrets

No GitHub, configure os seguintes secrets:

```
DOCKERHUB_USERNAME=seu-usuario
DOCKERHUB_TOKEN=seu-token
SONAR_TOKEN=seu-sonar-token
SLACK_WEBHOOK_URL=sua-webhook-url
```

### Deploy Manual

```bash
# Build da aplicação
mvn clean package

# Deploy com Docker
docker-compose -f docker-compose.prod.yml up -d

# Deploy em Kubernetes
kubectl apply -f k8s/
```

## 📚 API Documentation

### Swagger/OpenAPI

A documentação da API está disponível em:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Endpoints Principais

#### Usuários
- `GET /api/users` - Listar usuários
- `GET /api/users/{id}` - Buscar usuário por ID
- `POST /api/users` - Criar usuário
- `PUT /api/users/{id}` - Atualizar usuário
- `DELETE /api/users/{id}` - Deletar usuário
- `PATCH /api/users/{id}/activate` - Ativar usuário
- `PATCH /api/users/{id}/deactivate` - Desativar usuário

#### Exemplos de Uso

```bash
# Criar usuário
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "password": "password123"
  }'

# Listar usuários (requer autenticação)
curl -X GET http://localhost:8080/api/users \
  -u admin:admin123
```

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Padrões de Código

- Siga o **Google Java Style Guide**
- Use **Javadoc** para documentar APIs públicas
- Mantenha **cobertura de testes > 80%**
- Execute verificações de qualidade antes do commit

## 📝 Changelog

### [1.0.0] - 2024-01-15

#### Adicionado
- Estrutura inicial do projeto com Spring Boot 2.7
- API REST para gerenciamento de usuários
- Autenticação e autorização com Spring Security
- Testes unitários e de integração
- Pipeline de CI/CD com GitHub Actions
- Containerização com Docker
- Monitoramento com Actuator e Prometheus
- Documentação com Swagger/OpenAPI

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 👥 Autores

- **Seu Nome** - *Trabalho inicial* - [SeuUsuario](https://github.com/seuusuario)

## 🙏 Agradecimentos

- Comunidade Spring Boot pelas excelentes ferramentas
- Contribuidores que ajudaram a melhorar este template
- Projetos open source que serviram de inspiração

## 📞 Suporte

- **Issues**: [GitHub Issues](https://github.com/seuusuario/my-project/issues)
- **Discussões**: [GitHub Discussions](https://github.com/seuusuario/my-project/discussions)
- **Email**: seu.email@exemplo.com

---

**Feito com ❤️ para a comunidade Java**

