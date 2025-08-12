#!/bin/bash

# Script de setup para ambiente de desenvolvimento
# Este script configura o ambiente local para desenvolvimento

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para logging
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Função para verificar se comando existe
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Banner
echo -e "${BLUE}"
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║                    Setup de Desenvolvimento                  ║"
echo "║                     My Project Template                      ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Verificar pré-requisitos
log_info "Verificando pré-requisitos..."

# Java
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    log_success "Java encontrado: $JAVA_VERSION"
    
    # Verificar se é Java 11+
    JAVA_MAJOR=$(echo $JAVA_VERSION | cut -d'.' -f1)
    if [ "$JAVA_MAJOR" -lt 11 ]; then
        log_error "Java 11 ou superior é necessário. Versão atual: $JAVA_VERSION"
        exit 1
    fi
else
    log_error "Java não encontrado. Por favor, instale Java 11 ou superior."
    exit 1
fi

# Maven
if command_exists mvn; then
    MAVEN_VERSION=$(mvn -version 2>&1 | head -n 1 | cut -d' ' -f3)
    log_success "Maven encontrado: $MAVEN_VERSION"
else
    log_error "Maven não encontrado. Por favor, instale Maven 3.6 ou superior."
    exit 1
fi

# Git
if command_exists git; then
    GIT_VERSION=$(git --version | cut -d' ' -f3)
    log_success "Git encontrado: $GIT_VERSION"
else
    log_error "Git não encontrado. Por favor, instale Git."
    exit 1
fi

# Docker (opcional)
if command_exists docker; then
    DOCKER_VERSION=$(docker --version | cut -d' ' -f3 | sed 's/,//')
    log_success "Docker encontrado: $DOCKER_VERSION"
    DOCKER_AVAILABLE=true
else
    log_warning "Docker não encontrado. Funcionalidades Docker não estarão disponíveis."
    DOCKER_AVAILABLE=false
fi

# Docker Compose (opcional)
if command_exists docker-compose; then
    DOCKER_COMPOSE_VERSION=$(docker-compose --version | cut -d' ' -f3 | sed 's/,//')
    log_success "Docker Compose encontrado: $DOCKER_COMPOSE_VERSION"
    DOCKER_COMPOSE_AVAILABLE=true
else
    log_warning "Docker Compose não encontrado. Funcionalidades Docker Compose não estarão disponíveis."
    DOCKER_COMPOSE_AVAILABLE=false
fi

# Configurar Git hooks (se pre-commit estiver disponível)
log_info "Configurando Git hooks..."
if command_exists pre-commit; then
    pre-commit install
    log_success "Pre-commit hooks instalados"
else
    log_warning "pre-commit não encontrado. Para instalar: pip install pre-commit"
fi

# Baixar dependências Maven
log_info "Baixando dependências Maven..."
mvn dependency:resolve dependency:resolve-sources
log_success "Dependências Maven baixadas"

# Compilar projeto
log_info "Compilando projeto..."
mvn clean compile
log_success "Projeto compilado com sucesso"

# Executar testes
log_info "Executando testes..."
mvn test
log_success "Testes executados com sucesso"

# Configurar IDE (se IntelliJ IDEA estiver disponível)
if [ -d ".idea" ]; then
    log_info "Configuração do IntelliJ IDEA detectada"
    log_info "Certifique-se de configurar:"
    echo "  - Java SDK: Java 11+"
    echo "  - Maven: Usar Maven wrapper ou instalação local"
    echo "  - Code Style: Google Java Style Guide"
    echo "  - Plugins recomendados: Lombok, MapStruct, SonarLint"
fi

# Criar arquivos de configuração local se não existirem
log_info "Criando arquivos de configuração local..."

# application-local.yml
if [ ! -f "src/main/resources/application-local.yml" ]; then
    cat > src/main/resources/application-local.yml << EOF
# Configuração local para desenvolvimento
# Este arquivo é ignorado pelo Git

spring:
  profiles:
    active: dev
  
  # Configurações específicas do desenvolvedor
  datasource:
    url: jdbc:h2:mem:localdb
    username: sa
    password: 
  
  # Logs mais verbosos para desenvolvimento
  logging:
    level:
      com.example.myproject: DEBUG
      org.springframework.web: DEBUG

# Configurações personalizadas
app:
  debug: true
EOF
    log_success "Arquivo application-local.yml criado"
fi

# .env.local
if [ ! -f ".env.local" ]; then
    cat > .env.local << EOF
# Variáveis de ambiente locais
# Este arquivo é ignorado pelo Git

# Banco de dados local
DATABASE_URL=jdbc:h2:mem:localdb
DATABASE_USERNAME=sa
DATABASE_PASSWORD=

# Configurações de desenvolvimento
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=DEBUG

# Configurações opcionais
ADMIN_PASSWORD=admin123
JWT_SECRET=localSecretKey
EOF
    log_success "Arquivo .env.local criado"
fi

# Configurar Docker (se disponível)
if [ "$DOCKER_AVAILABLE" = true ] && [ "$DOCKER_COMPOSE_AVAILABLE" = true ]; then
    log_info "Configurando ambiente Docker..."
    
    # Verificar se Docker está rodando
    if docker info >/dev/null 2>&1; then
        log_info "Baixando imagens Docker necessárias..."
        docker-compose pull
        log_success "Imagens Docker baixadas"
    else
        log_warning "Docker não está rodando. Inicie o Docker para usar funcionalidades de container."
    fi
fi

# Criar diretórios necessários
log_info "Criando diretórios necessários..."
mkdir -p logs data docs/images scripts/sql
log_success "Diretórios criados"

# Configurar permissões dos scripts
log_info "Configurando permissões dos scripts..."
chmod +x scripts/*.sh
log_success "Permissões configuradas"

# Verificar configuração final
log_info "Verificando configuração final..."

# Testar build completo
log_info "Testando build completo..."
mvn clean package -DskipTests
log_success "Build completo executado com sucesso"

# Resumo final
echo -e "${GREEN}"
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║                    Setup Concluído!                         ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

log_success "Ambiente de desenvolvimento configurado com sucesso!"
echo ""
echo -e "${BLUE}Próximos passos:${NC}"
echo "1. Execute 'mvn spring-boot:run' para iniciar a aplicação"
echo "2. Acesse http://localhost:8080/swagger-ui.html para ver a API"
echo "3. Use 'make help' para ver comandos disponíveis"
echo "4. Configure sua IDE com as configurações recomendadas"
echo ""

if [ "$DOCKER_AVAILABLE" = true ]; then
    echo -e "${BLUE}Comandos Docker úteis:${NC}"
    echo "- docker-compose up -d          # Iniciar todos os serviços"
    echo "- docker-compose logs -f        # Ver logs dos serviços"
    echo "- docker-compose down           # Parar todos os serviços"
    echo ""
fi

echo -e "${BLUE}Comandos Make úteis:${NC}"
echo "- make run                       # Executar aplicação"
echo "- make test                      # Executar testes"
echo "- make quality                   # Verificar qualidade"
echo "- make docker-build              # Build Docker"
echo ""

log_info "Para mais informações, consulte o README.md"
echo ""

