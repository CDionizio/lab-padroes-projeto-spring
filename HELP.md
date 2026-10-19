# Laboratório de padrões de projeto com Spring

Aplicação REST de cadastro de clientes que reutiliza endereços consultados pela API ViaCEP.

## Requisitos

- Java 25

## Executar

No Windows, na pasta do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

Executar os testes:

```powershell
.\mvnw.cmd test
```

A aplicação inicia na porta 8080. O banco H2 em memória é criado automaticamente e pode ser acessado em `http://localhost:8080/h2-console` com JDBC URL `jdbc:h2:mem:padroesdb`, usuário `sa` e senha vazia.

## API de clientes

| Método | Rota | Resultado |
| --- | --- | --- |
| GET | `/clientes` | Lista clientes |
| GET | `/clientes/{id}` | Busca cliente por ID |
| POST | `/clientes` | Cria cliente e consulta o CEP quando necessário |
| PUT | `/clientes/{id}` | Atualiza cliente |
| DELETE | `/clientes/{id}` | Exclui cliente |

Exemplo de corpo para criação ou atualização:

```json
{
  "nome": "Ana",
  "endereco": {
    "cep": "01001-000"
  }
}
```

O CEP pode ser enviado com ou sem hífen. O endereço é reutilizado quando já existe no banco; caso contrário, é consultado no ViaCEP.
