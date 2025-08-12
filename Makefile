# Makefile para automação de tarefas do projeto Java

# Variáveis
MAVEN := mvn
DOCKER := docker
DOCKER_COMPOSE := docker-compose
PROJECT_NAME := my-project
DOCKER_IMAGE := $(PROJECT_NAME):latest
JAVA_VERSION := 11

# Cores para output
RED := \033[0;31m
GREEN := \033[0;32m
YELLOW := \033[0;33m
BLUE := \033[0;34m
NC := \033[0m # No Color

# Targets padrão
.PHONY: help clean build test run docker-build docker-run setup quality coverage docs

# Help - mostra todos os comandos disponíveis
help: ## Mostra esta mensagem de ajuda
	@echo "$(BLUE)Comandos disponíveis:$(NC)"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(GREEN)%-20s$(NC) %s\n", $$1, $$2}'

# Setup inicial do projeto
setup: ## Configura o ambiente de desenvolvimento
	@echo "$(YELLOW)Configurando ambiente de desenvolvimento...$(NC)"
	@echo "Verificando Java $(JAVA_VERSION)..."
	@java -version
	@echo "Verificando Maven..."
	@$(MAVEN) -version
	@echo "Verificando Docker..."
	@$(DOCKER) --version
	@echo "$(GREEN)Ambiente configurado com sucesso!$(NC)"

# Limpeza
clean: ## Remove arquivos de build e cache
	@echo "$(YELLOW)Limpando arquivos de build...$(NC)"
	$(MAVEN) clean
	@echo "$(GREEN)Limpeza concluída!$(NC)"

# Build
build: clean ## Compila o projeto
	@echo "$(YELLOW)Compilando projeto...$(NC)"
	$(MAVEN) compile
	@echo "$(GREEN)Compilação concluída!$(NC)"

# Package
package: clean ## Empacota a aplicação
	@echo "$(YELLOW)Empacotando aplicação...$(NC)"
	$(MAVEN) package -DskipTests
	@echo "$(GREEN)Empacotamento concluído!$(NC)"

# Testes
test: ## Executa todos os testes
	@echo "$(YELLOW)Executando testes...$(NC)"
	$(MAVEN) test
	@echo "$(GREEN)Testes concluídos!$(NC)"

test-unit: ## Executa apenas testes unitários
	@echo "$(YELLOW)Executando testes unitários...$(NC)"
	$(MAVEN) test -Dtest="**/*Test"
	@echo "$(GREEN)Testes unitários concluídos!$(NC)"

test-integration: ## Executa testes de integração
	@echo "$(YELLOW)Executando testes de integração...$(NC)"
	$(MAVEN) verify -P integration-tests
	@echo "$(GREEN)Testes de integração concluídos!$(NC)"

# Cobertura de código
coverage: ## Gera relatório de cobertura de código
	@echo "$(YELLOW)Gerando relatório de cobertura...$(NC)"
	$(MAVEN) clean test jacoco:report
	@echo "$(GREEN)Relatório de cobertura gerado em target/site/jacoco/index.html$(NC)"

# Qualidade de código
quality: ## Executa verificações de qualidade de código
	@echo "$(YELLOW)Executando verificações de qualidade...$(NC)"
	$(MAVEN) checkstyle:check
	$(MAVEN) pmd:check
	$(MAVEN) spotbugs:check
	@echo "$(GREEN)Verificações de qualidade concluídas!$(NC)"

# Análise de segurança
security: ## Executa verificação de segurança
	@echo "$(YELLOW)Executando verificação de segurança...$(NC)"
	$(MAVEN) org.owasp:dependency-check-maven:check
	@echo "$(GREEN)Verificação de segurança concluída!$(NC)"

# Executar aplicação
run: ## Executa a aplicação
	@echo "$(YELLOW)Iniciando aplicação...$(NC)"
	$(MAVEN) spring-boot:run

run-dev: ## Executa a aplicação em modo desenvolvimento
	@echo "$(YELLOW)Iniciando aplicação em modo desenvolvimento...$(NC)"
	$(MAVEN) spring-boot:run -Dspring-boot.run.profiles=dev

run-prod: ## Executa a aplicação em modo produção
	@echo "$(YELLOW)Iniciando aplicação em modo produção...$(NC)"
	$(MAVEN) spring-boot:run -Dspring-boot.run.profiles=prod

# Docker
docker-build: package ## Constrói a imagem Docker
	@echo "$(YELLOW)Construindo imagem Docker...$(NC)"
	$(DOCKER) build -t $(DOCKER_IMAGE) .
	@echo "$(GREEN)Imagem Docker construída: $(DOCKER_IMAGE)$(NC)"

docker-run: ## Executa a aplicação via Docker
	@echo "$(YELLOW)Executando aplicação via Docker...$(NC)"
	$(DOCKER) run -p 8080:8080 $(DOCKER_IMAGE)

docker-push: docker-build ## Faz push da imagem Docker
	@echo "$(YELLOW)Fazendo push da imagem Docker...$(NC)"
	$(DOCKER) push $(DOCKER_IMAGE)
	@echo "$(GREEN)Push da imagem concluído!$(NC)"

# Docker Compose
compose-up: ## Inicia todos os serviços com Docker Compose
	@echo "$(YELLOW)Iniciando serviços com Docker Compose...$(NC)"
	$(DOCKER_COMPOSE) up -d
	@echo "$(GREEN)Serviços iniciados!$(NC)"

compose-down: ## Para todos os serviços do Docker Compose
	@echo "$(YELLOW)Parando serviços do Docker Compose...$(NC)"
	$(DOCKER_COMPOSE) down
	@echo "$(GREEN)Serviços parados!$(NC)"

compose-logs: ## Mostra logs dos serviços
	$(DOCKER_COMPOSE) logs -f

compose-monitoring: ## Inicia serviços com monitoramento
	@echo "$(YELLOW)Iniciando serviços com monitoramento...$(NC)"
	$(DOCKER_COMPOSE) --profile monitoring up -d
	@echo "$(GREEN)Serviços com monitoramento iniciados!$(NC)"

compose-logging: ## Inicia serviços com logging
	@echo "$(YELLOW)Iniciando serviços com logging...$(NC)"
	$(DOCKER_COMPOSE) --profile logging up -d
	@echo "$(GREEN)Serviços com logging iniciados!$(NC)"

# Documentação
docs: ## Gera documentação do projeto
	@echo "$(YELLOW)Gerando documentação...$(NC)"
	$(MAVEN) javadoc:javadoc
	@echo "$(GREEN)Documentação gerada em target/site/apidocs/index.html$(NC)"

docs-site: ## Gera site completo com documentação
	@echo "$(YELLOW)Gerando site de documentação...$(NC)"
	$(MAVEN) site
	@echo "$(GREEN)Site gerado em target/site/index.html$(NC)"

# Utilitários
format: ## Formata o código fonte
	@echo "$(YELLOW)Formatando código fonte...$(NC)"
	$(MAVEN) spotless:apply
	@echo "$(GREEN)Código formatado!$(NC)"

dependencies: ## Mostra árvore de dependências
	$(MAVEN) dependency:tree

dependency-updates: ## Verifica atualizações de dependências
	$(MAVEN) versions:display-dependency-updates

plugin-updates: ## Verifica atualizações de plugins
	$(MAVEN) versions:display-plugin-updates

# Linting
lint: ## Executa linting do código
	@echo "$(YELLOW)Executando linting...$(NC)"
	$(MAVEN) checkstyle:check
	@echo "$(GREEN)Linting concluído!$(NC)"

# Perfis de build
build-dev: ## Build para desenvolvimento
	@echo "$(YELLOW)Build para desenvolvimento...$(NC)"
	$(MAVEN) clean package -P dev -DskipTests
	@echo "$(GREEN)Build de desenvolvimento concluído!$(NC)"

build-prod: ## Build para produção
	@echo "$(YELLOW)Build para produção...$(NC)"
	$(MAVEN) clean package -P prod
	@echo "$(GREEN)Build de produção concluído!$(NC)"

# CI/CD local
ci: clean test quality security ## Executa pipeline de CI localmente
	@echo "$(GREEN)Pipeline de CI executado com sucesso!$(NC)"

cd: ci docker-build ## Executa pipeline de CD localmente
	@echo "$(GREEN)Pipeline de CD executado com sucesso!$(NC)"

# Banco de dados
db-migrate: ## Executa migrações do banco de dados
	@echo "$(YELLOW)Executando migrações do banco...$(NC)"
	$(MAVEN) flyway:migrate
	@echo "$(GREEN)Migrações executadas!$(NC)"

db-clean: ## Limpa o banco de dados
	@echo "$(YELLOW)Limpando banco de dados...$(NC)"
	$(MAVEN) flyway:clean
	@echo "$(GREEN)Banco de dados limpo!$(NC)"

# Monitoramento
health: ## Verifica saúde da aplicação
	@echo "$(YELLOW)Verificando saúde da aplicação...$(NC)"
	@curl -f http://localhost:8080/api/actuator/health || echo "$(RED)Aplicação não está rodando$(NC)"

metrics: ## Mostra métricas da aplicação
	@echo "$(YELLOW)Métricas da aplicação:$(NC)"
	@curl -s http://localhost:8080/api/actuator/metrics | jq '.' || echo "$(RED)Aplicação não está rodando ou jq não instalado$(NC)"

# Limpeza completa
clean-all: clean ## Limpeza completa incluindo Docker
	@echo "$(YELLOW)Limpeza completa...$(NC)"
	$(DOCKER_COMPOSE) down -v --remove-orphans
	$(DOCKER) system prune -f
	@echo "$(GREEN)Limpeza completa concluída!$(NC)"

# Instalação de dependências de desenvolvimento
install-dev-tools: ## Instala ferramentas de desenvolvimento
	@echo "$(YELLOW)Instalando ferramentas de desenvolvimento...$(NC)"
	@echo "Instalando pre-commit hooks..."
	@if command -v pre-commit >/dev/null 2>&1; then \
		pre-commit install; \
	else \
		echo "$(RED)pre-commit não encontrado. Instale com: pip install pre-commit$(NC)"; \
	fi
	@echo "$(GREEN)Ferramentas instaladas!$(NC)"

# Status do projeto
status: ## Mostra status do projeto
	@echo "$(BLUE)Status do Projeto $(PROJECT_NAME)$(NC)"
	@echo "Java Version: $(shell java -version 2>&1 | head -n 1)"
	@echo "Maven Version: $(shell $(MAVEN) -version 2>&1 | head -n 1)"
	@echo "Docker Version: $(shell $(DOCKER) --version)"
	@echo "Última build: $(shell ls -la target/*.jar 2>/dev/null | tail -n 1 || echo 'Nenhuma build encontrada')"
	@echo "Containers rodando: $(shell $(DOCKER_COMPOSE) ps --services --filter status=running 2>/dev/null | wc -l)"

# Target padrão
.DEFAULT_GOAL := help

