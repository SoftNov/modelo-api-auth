create database db_auto_car;

use db_auto_car;

-- =========================
-- TABELA: users
-- Responsável por autenticação e acesso ao sistema
-- =========================
CREATE TABLE users (
    id CHAR(36) NOT NULL COMMENT 'Identificador único do usuário (UUID)',
    username VARCHAR(50) NOT NULL COMMENT 'Nome de usuário para login',
    password_hash VARCHAR(255) NOT NULL COMMENT 'Hash da senha do usuário',
    status VARCHAR(30) NOT NULL COMMENT 'Status do usuário: ACTIVE, BLOCKED, PENDING_VERIFICATION',
    email_verified_at TIMESTAMP NULL COMMENT 'Data/hora de verificação do e-mail',
    last_login_at TIMESTAMP NULL COMMENT 'Último login realizado pelo usuário',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do usuário',
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Data da última atualização do usuário',
    PRIMARY KEY (id),
    UNIQUE (username),
    CHECK (status IN ('ACTIVE', 'BLOCKED', 'PENDING_VERIFICATION'))
) ENGINE=InnoDB COMMENT='Usuários do sistema (autenticação)';


-- =========================
-- TABELA: roles
-- Papéis de acesso do sistema
-- =========================
CREATE TABLE roles (
    id CHAR(36) NOT NULL COMMENT 'Identificador único do papel (UUID)',
    name VARCHAR(50) NOT NULL COMMENT 'Nome do papel (BUYER, SELLER, ADMIN)',
    description VARCHAR(255) COMMENT 'Descrição do papel',
    PRIMARY KEY (id),
    UNIQUE (name)
) ENGINE=InnoDB COMMENT='Papéis de acesso';


-- =========================
-- TABELA: user_roles
-- Relacionamento usuário x papel
-- =========================
CREATE TABLE user_roles (
    user_id CHAR(36) NOT NULL COMMENT 'ID do usuário',
    role_id CHAR(36) NOT NULL COMMENT 'ID do papel',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de associação do papel ao usuário',
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB COMMENT='Papéis associados aos usuários';


-- =========================
-- TABELA: person
-- Dados de pessoa física
-- =========================
CREATE TABLE person (
    id CHAR(36) NOT NULL COMMENT 'Identificador único da pessoa (UUID)',
    user_id CHAR(36) NOT NULL COMMENT 'Usuário associado à pessoa',
    full_name VARCHAR(150) NOT NULL COMMENT 'Nome completo da pessoa',
    cpf VARCHAR(14) NOT NULL COMMENT 'CPF da pessoa',
    birth_date DATE COMMENT 'Data de nascimento',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do registro',
    PRIMARY KEY (id),
    UNIQUE (user_id),
    UNIQUE (cpf),
    CONSTRAINT fk_person_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB COMMENT='Dados de pessoa física';


-- =========================
-- TABELA: company
-- Dados de empresas anunciantes
-- =========================
CREATE TABLE company (
    id CHAR(36) NOT NULL COMMENT 'Identificador único da empresa (UUID)',
    legal_name VARCHAR(200) NOT NULL COMMENT 'Razão social da empresa',
    trade_name VARCHAR(200) COMMENT 'Nome fantasia da empresa',
    cnpj VARCHAR(18) NOT NULL COMMENT 'CNPJ da empresa',
    status VARCHAR(30) NOT NULL COMMENT 'Status da empresa: ACTIVE, SUSPENDED ou PENDING_VERIFICATION',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação da empresa',
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Última atualização da empresa',
    PRIMARY KEY (id),
    UNIQUE (cnpj),
    CHECK (status IN ('ACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION'))
) ENGINE=InnoDB COMMENT='Empresas anunciantes';


-- =========================
-- TABELA: company_user
-- Usuários vinculados às empresas
-- =========================
CREATE TABLE company_user (
    company_id CHAR(36) NOT NULL COMMENT 'ID da empresa',
    user_id CHAR(36) NOT NULL COMMENT 'ID do usuário',
    role VARCHAR(30) NOT NULL COMMENT 'Papel do usuário na empresa: OWNER, ADMIN, SELLER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de vínculo do usuário com a empresa',
    PRIMARY KEY (company_id, user_id),
    CONSTRAINT fk_company_user_company FOREIGN KEY (company_id) REFERENCES company(id),
    CONSTRAINT fk_company_user_user FOREIGN KEY (user_id) REFERENCES users(id),
    CHECK (role IN ('OWNER', 'ADMIN', 'SELLER'))
) ENGINE=InnoDB COMMENT='Usuários associados às empresas';


-- =========================
-- TABELA: contact
-- Contatos de usuários ou empresas
-- =========================
CREATE TABLE contact (
    id CHAR(36) NOT NULL COMMENT 'Identificador único do contato (UUID)',
    owner_type VARCHAR(30) NOT NULL COMMENT 'Tipo do dono do contato: USER ou COMPANY',
    user_id CHAR(36) NULL COMMENT 'ID do usuário (quando owner_type = USER)',
    company_id CHAR(36) NULL COMMENT 'ID da empresa (quando owner_type = COMPANY)',
    contact_type VARCHAR(30) NOT NULL COMMENT 'Tipo de contato: EMAIL, PHONE, WHATSAPP',
    value VARCHAR(150) NOT NULL COMMENT 'Valor do contato (email, telefone, etc)',
    is_primary BOOLEAN DEFAULT FALSE COMMENT 'Indica se é o contato principal',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do contato',
    PRIMARY KEY (id),
    CONSTRAINT fk_contact_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_contact_company FOREIGN KEY (company_id) REFERENCES company(id),
    CHECK (owner_type IN ('USER', 'COMPANY')),
    CHECK (contact_type IN ('EMAIL', 'PHONE', 'WHATSAPP')),
    CHECK (
      (owner_type = 'USER' AND user_id IS NOT NULL AND company_id IS NULL) OR
      (owner_type = 'COMPANY' AND company_id IS NOT NULL AND user_id IS NULL)
    )
) ENGINE=InnoDB COMMENT='Contatos de usuários e empresas';


-- =========================
-- TABELA: address
-- Endereços de usuários ou empresas
-- =========================
CREATE TABLE address (
    id CHAR(36) NOT NULL COMMENT 'Identificador único do endereço (UUID)',
    owner_type VARCHAR(30) NOT NULL COMMENT 'Tipo do dono do endereço: USER ou COMPANY',
    user_id CHAR(36) NULL COMMENT 'ID do usuário (quando owner_type = USER)',
    company_id CHAR(36) NULL COMMENT 'ID da empresa (quando owner_type = COMPANY)',
    zip_code VARCHAR(10) NOT NULL COMMENT 'CEP do endereço',
    street VARCHAR(150) NOT NULL COMMENT 'Logradouro',
    number VARCHAR(20) COMMENT 'Número do endereço',
    complement VARCHAR(100) COMMENT 'Complemento do endereço',
    district VARCHAR(100) COMMENT 'Bairro',
    city VARCHAR(100) NOT NULL COMMENT 'Cidade',
    state VARCHAR(2) NOT NULL COMMENT 'Estado (UF)',
    country VARCHAR(50) DEFAULT 'Brasil' COMMENT 'País',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do endereço',
    PRIMARY KEY (id),
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_address_company FOREIGN KEY (company_id) REFERENCES company(id),
    CHECK (owner_type IN ('USER', 'COMPANY')),
    CHECK (
      (owner_type = 'USER' AND user_id IS NOT NULL AND company_id IS NULL) OR
      (owner_type = 'COMPANY' AND company_id IS NOT NULL AND user_id IS NULL)
    )
) ENGINE=InnoDB COMMENT='Endereços de usuários e empresas';


-- =========================
-- TABELA: seller_profile
-- Perfil de anunciante (PF ou PJ)
-- =========================
CREATE TABLE seller_profile (
    id CHAR(36) NOT NULL COMMENT 'Identificador único do perfil de vendedor (UUID)',
    seller_type VARCHAR(30) NOT NULL COMMENT 'Tipo de vendedor: PERSON ou COMPANY',
    user_id CHAR(36) NULL COMMENT 'ID do usuário (quando seller_type = PERSON)',
    company_id CHAR(36) NULL COMMENT 'ID da empresa (quando seller_type = COMPANY)',
    rating DECIMAL(3,2) DEFAULT 0.0 COMMENT 'Avaliação média do vendedor',
    total_ads INT DEFAULT 0 COMMENT 'Total de anúncios criados pelo vendedor',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do perfil',
    PRIMARY KEY (id),
    CONSTRAINT fk_seller_profile_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_seller_profile_company FOREIGN KEY (company_id) REFERENCES company(id),
    CHECK (seller_type IN ('PERSON', 'COMPANY')),
    CHECK (
      (seller_type = 'PERSON' AND user_id IS NOT NULL AND company_id IS NULL) OR
      (seller_type = 'COMPANY' AND company_id IS NOT NULL AND user_id IS NULL)
    )
) ENGINE=InnoDB COMMENT='Perfil de vendedores (anunciantes)';


-- =========================
-- TABELA: parameter
-- Parâmetros e configurações do sistema
-- =========================
CREATE TABLE parameter (
    id CHAR(36) NOT NULL COMMENT 'Identificador único do parâmetro (UUID)',
    param_key VARCHAR(100) NOT NULL COMMENT 'Chave única do parâmetro',
    param_value TEXT NOT NULL COMMENT 'Valor do parâmetro',
    param_type VARCHAR(30) NOT NULL COMMENT 'Tipo do parâmetro: STRING, INTEGER, DECIMAL, BOOLEAN, JSON',
    category VARCHAR(50) NOT NULL COMMENT 'Categoria do parâmetro: SYSTEM, BUSINESS, SECURITY, EMAIL, NOTIFICATION',
    description VARCHAR(255) COMMENT 'Descrição do parâmetro',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Indica se o parâmetro está ativo',
    is_editable BOOLEAN DEFAULT TRUE COMMENT 'Indica se o parâmetro pode ser editado via sistema',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do parâmetro',
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Data da última atualização',
    updated_by CHAR(36) NULL COMMENT 'ID do usuário que realizou a última atualização',
    PRIMARY KEY (id),
    UNIQUE (param_key),
    CONSTRAINT fk_parameter_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    CHECK (param_type IN ('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'JSON')),
    CHECK (category IN ('SYSTEM', 'BUSINESS', 'SECURITY', 'EMAIL', 'NOTIFICATION'))
) ENGINE=InnoDB COMMENT='Parâmetros e configurações do sistema';


-- =========================
-- TABELA: email_confirmation_token
-- Tokens para confirmação de e-mail
-- =========================
CREATE TABLE email_confirmation_token (
    id CHAR(36) NOT NULL COMMENT 'Identificador único do token (UUID)',
    user_id CHAR(36) NOT NULL COMMENT 'ID do usuário associado ao token',
    token CHAR(36) NOT NULL COMMENT 'Token único de confirmação (UUID)',
    expires_at TIMESTAMP NOT NULL COMMENT 'Data/hora de expiração do token (24h após criação)',
    used_at TIMESTAMP NULL COMMENT 'Data/hora em que o token foi utilizado',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do token',
    PRIMARY KEY (id),
    UNIQUE (token),
    CONSTRAINT fk_email_token_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB COMMENT='Tokens de confirmação de e-mail';



-- =========================
-- TABELA: password_reset_token
-- Tokens para reset de senha
-- =========================
CREATE TABLE password_reset_token (
    id CHAR(36) NOT NULL COMMENT 'Identificador único do token (UUID)',
    user_id CHAR(36) NOT NULL COMMENT 'ID do usuário associado ao token',
    code VARCHAR(6) NOT NULL COMMENT 'Código de 6 dígitos para reset',
    expires_at TIMESTAMP NOT NULL COMMENT 'Data/hora de expiração do código (15 minutos)',
    validated_at TIMESTAMP NULL COMMENT 'Data/hora em que o código foi validado',
    used_at TIMESTAMP NULL COMMENT 'Data/hora em que o código foi utilizado para reset',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do código',
    PRIMARY KEY (id),
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB COMMENT='Tokens de reset de senha';



-- =========================
-- TABELA: password_history
-- Histórico de senhas do usuário (para impedir reutilização)
-- =========================
CREATE TABLE password_history (
    id CHAR(36) NOT NULL COMMENT 'Identificador único do registro (UUID)',
    user_id CHAR(36) NOT NULL COMMENT 'ID do usuário',
    password_hash VARCHAR(255) NOT NULL COMMENT 'Hash da senha antiga',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data em que a senha foi registrada',
    PRIMARY KEY (id),
    CONSTRAINT fk_password_history_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB COMMENT='Histórico de senhas do usuário para impedir reutilização';



-- =========================
-- ÍNDICES DE PERFORMANCE
-- =========================
CREATE INDEX idx_contact_owner ON contact(owner_type, user_id, company_id);
CREATE INDEX idx_address_owner ON address(owner_type, user_id, company_id);
CREATE INDEX idx_seller_profile ON seller_profile(seller_type, user_id, company_id);
CREATE INDEX idx_company_user_user ON company_user(user_id);
CREATE INDEX idx_parameter_category ON parameter(category, is_active);
CREATE INDEX idx_parameter_key ON parameter(param_key);
CREATE INDEX idx_password_history_user ON password_history(user_id);
CREATE INDEX idx_password_history_created ON password_history(user_id, created_at DESC);
CREATE INDEX idx_password_reset_user ON password_reset_token(user_id);
CREATE INDEX idx_password_reset_code ON password_reset_token(user_id, code);
CREATE INDEX idx_email_token_user ON email_confirmation_token(user_id);
CREATE INDEX idx_email_token_token ON email_confirmation_token(token);
