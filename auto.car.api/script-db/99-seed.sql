-- =========================
-- SEED: Roles padrão do sistema
-- =========================
INSERT INTO roles (id, name, description) VALUES
    (UUID(), 'ADMIN', 'Administrador - Acesso total ao sistema'),
    (UUID(), 'USER', 'Usuário padrão do sistema'),
    (UUID(), 'SELLER', 'Vendedor - Usuário que pode anunciar e vender veículos'),
    (UUID(), 'BUYER', 'Comprador - Usuário que pode comprar veículos'),
    (UUID(), 'MANAGER', 'Gerente - Permissões administrativas limitadas'),
    (UUID(), 'SUPPORT', 'Suporte - Equipe de suporte ao cliente');

