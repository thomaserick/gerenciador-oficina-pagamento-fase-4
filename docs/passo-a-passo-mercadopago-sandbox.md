# Passo a Passo: Configurar Conta Mercado Pago para Ambiente Sandbox

## 📋 Visão Geral

Analisando seu código (`MercadoPagoGatewayImpl.java`), você está utilizando a SDK oficial do Mercado Pago para processar pagamentos. Para o ambiente de desenvolvimento (sandbox), você precisa obter as credenciais de teste.

---

## 🚀 Passo 1: Criar uma Conta no Mercado Pago Developers

1. Acesse o portal de desenvolvedores: **https://www.mercadopago.com.br/developers**
2. Clique em **"Criar conta"** ou **"Entrar"** (se já tiver uma conta Mercado Pago)
3. Complete o cadastro com seus dados pessoais ou da empresa
4. Confirme seu e-mail

---

## 🔧 Passo 2: Acessar o Painel de Desenvolvedores

1. Após fazer login, acesse: **https://www.mercadopago.com.br/developers/panel**
2. Você será direcionado ao **Dashboard do Desenvolvedor**

---

## 📱 Passo 3: Criar uma Aplicação

1. No painel, clique em **"Suas integrações"** no menu lateral
2. Clique em **"Criar aplicação"**
3. Preencha os dados:
    - **Nome da aplicação**: `gerenciador-oficina-pagamento` (ou outro nome descritivo)
    - **Modelo de integração**: Selecione **"Checkout Pro"** ou **"Checkout API"** (para sua integração via API)
    - **Finalidade**: Selecione a opção mais adequada (ex: "Desenvolvimento/Teste")
4. Aceite os termos e clique em **"Criar aplicação"**

---

## 🔑 Passo 4: Obter as Credenciais de Teste (Sandbox)

1. Na sua aplicação criada, vá para **"Credenciais de teste"** (ou "Test credentials")
2. Você encontrará:

   | Credencial | Descrição | Variável de Ambiente |
      |------------|-----------|---------------------|
   | **Public Key** | Chave pública para identificar sua aplicação | `MERCADO_PAGO_PUBLIC_KEY` |
   | **Access Token** | Token de acesso para autenticação nas APIs | `MERCADO_PAGO_ACCESS_TOKEN` |

3. **Copie ambas as credenciais** - elas começam com `TEST-` para ambiente sandbox

### Exemplo de formato das credenciais:
```
Access Token: TEST-1234567890123456-123456-abcdefghijklmnopqrstuvwxyz123456-123456789
Public Key: TEST-12345678-1234-1234-1234-123456789012
```

---

## ⚙️ Passo 5: Configurar as Variáveis de Ambiente

Configure as variáveis de ambiente no seu sistema antes de rodar a aplicação:

### Linux/macOS:
```bash
export MERCADO_PAGO_ACCESS_TOKEN="TEST-seu-access-token-aqui"
export MERCADO_PAGO_PUBLIC_KEY="TEST-sua-public-key-aqui"
```

### Windows (PowerShell):
```powershell
$env:MERCADO_PAGO_ACCESS_TOKEN="TEST-seu-access-token-aqui"
$env:MERCADO_PAGO_PUBLIC_KEY="TEST-sua-public-key-aqui"
```

### Ou adicione no arquivo `application-dev.yaml`:
```yaml
mercadopago:
  access-token: TEST-seu-access-token-aqui
  public-key: TEST-sua-public-key-aqui
```

> ⚠️ **IMPORTANTE**: Nunca commite credenciais reais no repositório!

---

## 👥 Passo 6: Criar Usuários de Teste

Para testar pagamentos no sandbox, você precisa de **usuários de teste**:

1. Acesse: **https://www.mercadopago.com.br/developers/panel/app/{APP_ID}/test-users**
2. Ou vá em **"Suas integrações"** → Selecione sua aplicação → **"Contas de teste"**
3. Clique em **"Criar conta de teste"**
4. Crie **2 usuários**:
    - **Vendedor**: Representa sua aplicação
    - **Comprador**: Para simular pagamentos

5. Anote os dados gerados (e-mail e senha)

---

## 💳 Passo 7: Cartões de Teste para Sandbox

Para testar pagamentos com cartão, use os cartões de teste do Mercado Pago:

### Cartões que APROVAM:
| Bandeira | Número | CVV | Vencimento |
|----------|--------|-----|------------|
| Mastercard | 5031 4332 1540 6351 | 123 | 11/25 |
| Visa | 4235 6477 2802 5682 | 123 | 11/25 |

### Cartões que REJEITAM:
| Bandeira | Número | CVV | Vencimento |
|----------|--------|-----|------------|
| Mastercard | 5031 4332 1540 6351 | 123 | 11/25 (use CPF: 12345678909 para rejeitar) |

### CPFs de Teste:
- **Aprovação**: Qualquer CPF válido (ex: `19119119100`)
- **Rejeição**: `12345678909`

---

## ✅ Passo 8: Testar a Integração

Seu código `MercadoPagoGatewayImpl.java` está configurado corretamente para:

1. ✅ Usar o `accessToken` via `@Value`
2. ✅ Criar pagamentos com `PaymentClient`
3. ✅ Usar chave de idempotência para evitar duplicações
4. ✅ Mapear métodos de pagamento (PIX, Boleto, Cartão)

### Para testar:

1. Inicie sua aplicação com as variáveis de ambiente configuradas
2. Envie uma mensagem para a fila `default.pagamento.processar` com um `PagamentoProcessadoEvent`
3. Verifique os logs para confirmar o processamento

---

## 📚 Referências Oficiais

- **Documentação Oficial**: https://www.mercadopago.com.br/developers/pt/docs
- **Credenciais**: https://www.mercadopago.com.br/developers/panel/app
- **Cartões de Teste**: https://www.mercadopago.com.br/developers/pt/docs/your-integrations/test/cards
- **Usuários de Teste**: https://www.mercadopago.com.br/developers/pt/docs/your-integrations/test/accounts

---

## 🔒 Boas Práticas de Segurança

1. **Nunca exponha** o `Access Token` em código-fonte público
2. Use **variáveis de ambiente** ou **secrets managers** (AWS Secrets Manager, HashiCorp Vault)
3. Para produção, obtenha credenciais de **produção** (sem o prefixo `TEST-`)
4. Rotacione as credenciais periodicamente

---

> 📅 **Última atualização**: Fevereiro 2026
