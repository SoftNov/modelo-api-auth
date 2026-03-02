-- =========================
-- SEED: Parâmetros de E-mail
-- Template HTML para confirmação de conta
-- =========================

-- Template HTML do e-mail de confirmação de conta
INSERT INTO parameter (id, param_key, param_value, param_type, category, description, is_active, is_editable)
VALUES (
    UUID(),
    'email-create-account-html',
    '<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirmação de Conta</title>
</head>
<body style="margin: 0; padding: 0; font-family: Arial, Helvetica, sans-serif; background-color: #f4f4f4;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background-color: #f4f4f4; padding: 20px 0;">
        <tr>
            <td align="center">
                <table role="presentation" width="600" cellspacing="0" cellpadding="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="background-color: #1a73e8; padding: 30px; text-align: center; border-radius: 8px 8px 0 0;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 28px;">🚗 Auto Car</h1>
                            <p style="color: #e8f0fe; margin: 10px 0 0 0; font-size: 14px;">Marketplace de Veículos</p>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px 30px;">
                            <h2 style="color: #333333; margin: 0 0 20px 0; font-size: 24px;">Bem-vindo(a), {{NOME_USUARIO}}!</h2>
                            <p style="color: #666666; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">
                                Obrigado por se cadastrar no <strong>Auto Car</strong>! Estamos muito felizes em tê-lo(a) conosco.
                            </p>
                            <p style="color: #666666; font-size: 16px; line-height: 1.6; margin: 0 0 30px 0;">
                                Para completar seu cadastro e ativar sua conta, clique no botão abaixo:
                            </p>
                            <!-- Button -->
                            <table role="presentation" width="100%" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td align="center">
                                        <a href="{{LINK_CONFIRMACAO}}" style="display: inline-block; background-color: #1a73e8; color: #ffffff; text-decoration: none; padding: 15px 40px; border-radius: 5px; font-size: 16px; font-weight: bold;">
                                            Confirmar Minha Conta
                                        </a>
                                    </td>
                                </tr>
                            </table>
                            <p style="color: #999999; font-size: 14px; line-height: 1.6; margin: 30px 0 0 0;">
                                Se o botão não funcionar, copie e cole o link abaixo no seu navegador:
                            </p>
                            <p style="color: #1a73e8; font-size: 14px; word-break: break-all; margin: 10px 0 0 0;">
                                {{LINK_CONFIRMACAO}}
                            </p>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #f8f9fa; padding: 20px 30px; border-radius: 0 0 8px 8px; border-top: 1px solid #e0e0e0;">
                            <p style="color: #999999; font-size: 12px; margin: 0; text-align: center;">
                                Este e-mail foi enviado automaticamente. Por favor, não responda.
                            </p>
                            <p style="color: #999999; font-size: 12px; margin: 10px 0 0 0; text-align: center;">
                                © 2024 Auto Car - Todos os direitos reservados.
                            </p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    'STRING',
    'EMAIL',
    'Template HTML do e-mail de confirmação de conta. Variáveis: {{NOME_USUARIO}}, {{LINK_CONFIRMACAO}}',
    TRUE,
    TRUE
);

-- Assunto do e-mail de confirmação de conta
INSERT INTO parameter (id, param_key, param_value, param_type, category, description, is_active, is_editable)
VALUES (
    UUID(),
    'email-create-account-subject',
    'Confirme sua conta - Auto Car',
    'STRING',
    'EMAIL',
    'Assunto do e-mail de confirmação de conta',
    TRUE,
    TRUE
);

-- Template HTML do e-mail de reset de senha
INSERT INTO parameter (id, param_key, param_value, param_type, category, description, is_active, is_editable)
VALUES (
    UUID(),
    'email-password-reset-html',
    '<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recuperação de Senha</title>
</head>
<body style="margin: 0; padding: 0; font-family: Arial, Helvetica, sans-serif; background-color: #f4f4f4;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background-color: #f4f4f4; padding: 20px 0;">
        <tr>
            <td align="center">
                <table role="presentation" width="600" cellspacing="0" cellpadding="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="background-color: #e65100; padding: 30px; text-align: center; border-radius: 8px 8px 0 0;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 28px;">🔐 Recuperação de Senha</h1>
                            <p style="color: #ffe0b2; margin: 10px 0 0 0; font-size: 14px;">Auto Car - Marketplace de Veículos</p>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px 30px;">
                            <h2 style="color: #333333; margin: 0 0 20px 0; font-size: 24px;">Olá, {{NOME_USUARIO}}!</h2>
                            <p style="color: #666666; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">
                                Recebemos uma solicitação para redefinir a senha da sua conta no <strong>Auto Car</strong>.
                            </p>
                            <p style="color: #666666; font-size: 16px; line-height: 1.6; margin: 0 0 30px 0;">
                                Use o código abaixo para redefinir sua senha:
                            </p>
                            <!-- Code Box -->
                            <table role="presentation" width="100%" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td align="center">
                                        <div style="background-color: #fff3e0; border: 2px dashed #e65100; border-radius: 8px; padding: 20px; display: inline-block;">
                                            <span style="font-size: 36px; font-weight: bold; letter-spacing: 10px; color: #e65100; font-family: monospace;">{{CODIGO_RESET}}</span>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                            <p style="color: #d32f2f; font-size: 14px; line-height: 1.6; margin: 30px 0 0 0; text-align: center;">
                                <strong>⚠️ Este código expira em {{MINUTOS_EXPIRACAO}} minutos.</strong>
                            </p>
                            <p style="color: #999999; font-size: 14px; line-height: 1.6; margin: 20px 0 0 0;">
                                Se você não solicitou a redefinição de senha, ignore este e-mail. Sua senha permanecerá a mesma.
                            </p>
                        </td>
                    </tr>
                    <!-- Security Notice -->
                    <tr>
                        <td style="padding: 0 30px 30px 30px;">
                            <div style="background-color: #e3f2fd; border-left: 4px solid #1976d2; padding: 15px; border-radius: 4px;">
                                <p style="color: #1976d2; font-size: 14px; margin: 0;">
                                    <strong>🛡️ Dica de segurança:</strong> Nunca compartilhe este código com ninguém. Nossa equipe nunca solicitará seu código por telefone ou e-mail.
                                </p>
                            </div>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #f8f9fa; padding: 20px 30px; border-radius: 0 0 8px 8px; border-top: 1px solid #e0e0e0;">
                            <p style="color: #999999; font-size: 12px; margin: 0; text-align: center;">
                                Este e-mail foi enviado automaticamente. Por favor, não responda.
                            </p>
                            <p style="color: #999999; font-size: 12px; margin: 10px 0 0 0; text-align: center;">
                                © 2024 Auto Car - Todos os direitos reservados.
                            </p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    'STRING',
    'EMAIL',
    'Template HTML do e-mail de reset de senha. Variáveis: {{NOME_USUARIO}}, {{CODIGO_RESET}}, {{MINUTOS_EXPIRACAO}}',
    TRUE,
    TRUE
);

-- Template HTML do e-mail de confirmação de alteração de senha
INSERT INTO parameter (id, param_key, param_value, param_type, category, description, is_active, is_editable)
VALUES (
    UUID(),
    'email-password-changed-html',
    '<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Senha Alterada</title>
</head>
<body style="margin: 0; padding: 0; font-family: Arial, Helvetica, sans-serif; background-color: #f4f4f4;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background-color: #f4f4f4; padding: 20px 0;">
        <tr>
            <td align="center">
                <table role="presentation" width="600" cellspacing="0" cellpadding="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="background-color: #28a745; padding: 30px; text-align: center; border-radius: 8px 8px 0 0;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 28px;">✅ Senha Alterada</h1>
                            <p style="color: #d4edda; margin: 10px 0 0 0; font-size: 14px;">Auto Car - Marketplace de Veículos</p>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px 30px;">
                            <h2 style="color: #333333; margin: 0 0 20px 0; font-size: 24px;">Olá, {{NOME_USUARIO}}!</h2>
                            <p style="color: #666666; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;">
                                Sua senha foi alterada com sucesso em <strong>{{DATA_HORA_ALTERACAO}}</strong>.
                            </p>
                            <!-- Warning Box -->
                            <div style="background-color: #fff3cd; border: 1px solid #ffc107; border-radius: 8px; padding: 20px; margin: 20px 0;">
                                <p style="margin: 0; color: #856404; font-size: 14px;">
                                    <strong>⚠️ Não foi você?</strong><br><br>
                                    Se você não realizou esta alteração, sua conta pode estar comprometida.
                                    Entre em contato conosco imediatamente ou altere sua senha novamente através do nosso site.
                                </p>
                            </div>
                            <p style="color: #666666; font-size: 16px; line-height: 1.6; margin: 20px 0 0 0;">
                                Se você realizou esta alteração, pode ignorar este e-mail.
                            </p>
                        </td>
                    </tr>
                    <!-- Security Tips -->
                    <tr>
                        <td style="padding: 0 30px 30px 30px;">
                            <div style="background-color: #e3f2fd; border-left: 4px solid #1976d2; padding: 15px; border-radius: 4px;">
                                <p style="color: #1976d2; font-size: 14px; margin: 0;">
                                    <strong>🛡️ Dicas de segurança:</strong><br>
                                    • Nunca compartilhe sua senha com ninguém<br>
                                    • Use senhas únicas para cada serviço<br>
                                    • Ative a verificação em duas etapas quando disponível
                                </p>
                            </div>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #f8f9fa; padding: 20px 30px; border-radius: 0 0 8px 8px; border-top: 1px solid #e0e0e0;">
                            <p style="color: #999999; font-size: 12px; margin: 0; text-align: center;">
                                Este e-mail foi enviado automaticamente. Por favor, não responda.
                            </p>
                            <p style="color: #999999; font-size: 12px; margin: 10px 0 0 0; text-align: center;">
                                © 2024 Auto Car - Todos os direitos reservados.
                            </p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    'STRING',
    'EMAIL',
    'Template HTML do e-mail de confirmação de alteração de senha. Variáveis: {{NOME_USUARIO}}, {{DATA_HORA_ALTERACAO}}',
    TRUE,
    TRUE
);
