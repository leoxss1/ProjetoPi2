package projetoIntegrador.prototico.dto;
 
import jakarta.validation.constraints.NotNull;
import projetoIntegrador.prototico.model.TipoPagamento;
 
// O cliente só informa COMO quer pagar.
// Valor, status e data NÃO vêm do JSON: são definidos pelo servidor.
public record PagamentoRequest(
        @NotNull(message = "Informe o tipo de pagamento")
        TipoPagamento tipoPagamento) {
}