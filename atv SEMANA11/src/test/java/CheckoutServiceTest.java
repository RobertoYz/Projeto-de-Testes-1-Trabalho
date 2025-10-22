import org.example.checkout.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;


public class CheckoutServiceTest {

    private final CouponService couponSvc = new CouponService();
    private final ShippingService shipSvc = new ShippingService();
    private final CheckoutService service = new CheckoutService(couponSvc, shipSvc);
    private final LocalDate today = LocalDate.of(2025, 10, 22);

    // --- Teste Original ---

    @Test
    public void deveCalcularBasicoSemDescontosEImpostoApenasNaoBook() {
        var itens = List.of(
                new Item("BOOK", 100.00, 1),
                new Item("ELETRONICO", 50.00, 2) // tributável
        );

        var res = service.checkout(
                itens,
                CustomerTier.BASIC,
                false,
                "SUL",
                3.0,
                null,
                today,
                null
        );

        assertEquals(200.00, res.subtotal);          // 100 + (50*2)
        assertEquals(0.00, res.discountValue);
        // imposto 12% sobre parte tributável (100)
        assertEquals(12.00, res.tax);
        // frete SUL com peso 3 → 35
        assertEquals(35.00, res.shipping);
        assertEquals(247.00, res.total);
    }

    // --- Testes Adicionais para Cobertura de 100% ---

    // --- Testes de Descontos ---

    @Test
    public void deveAplicarDescontosCombinadosSilverPrimeiraCompraECupom() {
        var itens = List.of(new Item("ELETRONICO", 200.00, 1));

        var res = service.checkout(
                itens,
                CustomerTier.SILVER, // 5%
                true,          // 5%
                "SUDESTE",
                1.0,
                "DESC10",        // 10%
                today,
                null
        );

        // Desconto total = 5% (SILVER) + 5% (1a compra) + 10% (cupom) = 20%
        assertEquals(200.00, res.subtotal);
        assertEquals(40.00, res.discountValue); // 20% de 200
        double baseAposDesconto = 160.00;
        assertEquals(19.20, res.tax, 0.001);   // 12% sobre 160
        assertEquals(20.00, res.shipping); // frete SUDESTE com peso 1
        assertEquals(199.20, res.total, 0.001); // 160 + 19.20 + 20
    }

    @Test
    public void deveLimitarDescontoTotalEm30Porcento() {
        var itens = List.of(new Item("ELETRONICO", 500.00, 1));

        var res = service.checkout(
                itens,
                CustomerTier.GOLD,   // 10%
                true,          // 5%
                "NORTE",
                4.0,
                "DESC20",        // 20%
                today,
                null
        );

        // Desconto total = 10% + 5% + 20% = 35%, mas deve ser limitado a 30%
        assertEquals(500.00, res.subtotal);
        assertEquals(150.00, res.discountValue); // 30% de 500
        double baseAposDesconto = 350.00;
        assertEquals(42.00, res.tax, 0.001);   // 12% sobre 350
        assertEquals(55.00, res.shipping); // frete NORTE com peso 4
        assertEquals(447.00, res.total, 0.001); // 350 + 42 + 55
    }

    @Test
    public void naoDeveAplicarDescontoPrimeiraCompraSeSubtotalMenorQue50() {
        var itens = List.of(new Item("ELETRONICO", 49.99, 1));
        var res = service.checkout(itens, CustomerTier.BASIC, true, "SUL", 1, null, today, null);
        assertEquals(0.0, res.discountValue);
    }

    // --- Testes de Cupons ---

    @Test
    public void naoDeveAplicarCupomDesc20SeExpirado() {
        var itens = List.of(new Item("ELETRONICO", 100.00, 1));
        var res = service.checkout(
                itens, CustomerTier.BASIC, false, "SUL", 1.0, "DESC20",
                today,
                today.minusDays(1) // Expirado ontem
        );
        assertEquals(0.00, res.discountValue, "Desconto não deveria ser aplicado para cupom expirado.");
    }

    @Test
    public void naoDeveAplicarCupomDesc20SeSubtotalInsuficiente() {
        var itens = List.of(new Item("ELETRONICO", 99.99, 1));
        var res = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1.0, "DESC20", today, null);
        assertEquals(0.00, res.discountValue, "Desconto não deveria ser aplicado para subtotal insuficiente.");
    }

    @Test
    public void deveIgnorarCupomInvalidoOuVazio() {
        var itens = List.of(new Item("ELETRONICO", 100.00, 1));
        // Cupom inexistente
        var res1 = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1, "CUPOM_FALSO", today, null);
        assertEquals(0.0, res1.discountValue);
        // Cupom nulo
        var res2 = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1, null, today, null);
        assertEquals(0.0, res2.discountValue);
        // Cupom em branco
        var res3 = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1, "  ", today, null);
        assertEquals(0.0, res3.discountValue);
    }

    // --- Testes de Imposto ---

    @Test
    public void naoDeveCalcularImpostoParaItensIsentos() {
        var itens = List.of(new Item("BOOK", 250.00, 1)); // Apenas itens isentos
        var res = service.checkout(itens, CustomerTier.GOLD, false, "SUL", 2, null, today, null);

        assertEquals(250.00, res.subtotal);
        assertEquals(25.00, res.discountValue); // 10% GOLD
        assertEquals(0.00, res.tax); // Imposto deve ser zero
        assertEquals(35.00, res.shipping); // SUL, peso 2
        assertEquals(260.00, res.total); // 225 + 0 + 35
    }

    // --- Testes de Frete ---

    @Test
    public void deveDarFreteGratisParaSubtotalAcimaDe300() {
        var itens = List.of(new Item("ELETRONICO", 300.00, 1));
        var res = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 10, null, today, null);
        assertEquals(0.00, res.shipping, "Frete deveria ser grátis para subtotal >= 300.");
    }

    @Test
    public void deveDarFreteGratisComCupomEPesoValido() {
        var itens = List.of(new Item("ELETRONICO", 100.00, 1));
        var res = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 5.0, "FRETEGRATIS", today, null);
        assertEquals(0.00, res.shipping, "Frete deveria ser grátis com cupom e peso válido.");
    }

    @Test
    public void naoDeveDarFreteGratisComCupomSePesoExcedido() {
        var itens = List.of(new Item("ELETRONICO", 100.00, 1));
        var res = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 5.01, "FRETEGRATIS", today, null);
        assertNotEquals(0.00, res.shipping, "Frete não deveria ser grátis com cupom se peso exceder o limite.");
        assertEquals(50.0, res.shipping); // Deve aplicar a regra normal de frete (SUL, peso > 5)
    }

    @ParameterizedTest
    @CsvSource({
            // region,      weight, expectedShipping
            "SUL,         2.0,    20.0",
            "SUDESTE,     2.0,    20.0",
            "SUL,         5.0,    35.0",
            "SUDESTE,     5.0,    35.0",
            "SUL,         5.1,    50.0",
            "SUDESTE,     5.1,    50.0",
            "NORTE,       2.0,    30.0",
            "NORTE,       5.0,    55.0",
            "NORTE,       5.1,    80.0",
            "CENTRO-OESTE,3.0,    40.0", // Outra região
            " ,           3.0,    40.0"  // Região nula/vazia
    })
    public void deveCalcularFreteCorretamenteParaDiferentesRegioesEPesos(String region, double weight, double expectedShipping) {
        var itens = List.of(new Item("ELETRONICO", 100.00, 1));
        var res = service.checkout(itens, CustomerTier.BASIC, false, region, weight, null, today, null);
        assertEquals(expectedShipping, res.shipping);
    }

    // --- Testes de Casos Especiais (Edge Cases) ---

    @Test
    public void deveRetornarTudoZeroParaCheckoutDeListaVaziaComCupomFreteGratis() {
        var res = service.checkout(Collections.emptyList(), CustomerTier.BASIC, false, "SUL", 0.0, "FRETEGRATIS", today, null);
        assertEquals(0.0, res.subtotal);
        assertEquals(0.0, res.discountValue);
        assertEquals(0.0, res.tax);
        assertEquals(0.0, res.shipping);
        assertEquals(0.0, res.total);
    }

    // --- Testes de Exceções (Inputs Inválidos) ---

    @Test
    public void deveLancarExcecaoParaArgumentosNulosNoCheckout() {
        var itens = List.of(new Item("ELETRONICO", 100, 1));
        assertThrows(NullPointerException.class, () ->
                        service.checkout(null, CustomerTier.BASIC, false, "SUL", 1, null, today, null),
                "Itens nulos devem lançar NullPointerException");

        assertThrows(NullPointerException.class, () ->
                        service.checkout(itens, null, false, "SUL", 1, null, today, null),
                "Tier nulo deve lançar NullPointerException");

        assertThrows(NullPointerException.class, () ->
                        service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1, null, null, null),
                "Today nulo deve lançar NullPointerException");
    }

    @Test
    public void deveLancarExcecaoParaItemInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Item("CAT", -1.0, 1),
                "Preço negativo deve lançar IllegalArgumentException");

        assertThrows(IllegalArgumentException.class, () -> new Item("CAT", 10.0, 0),
                "Quantidade zero deve lançar IllegalArgumentException");

        assertThrows(IllegalArgumentException.class, () -> new Item("CAT", 10.0, -1),
                "Quantidade negativa deve lançar IllegalArgumentException");

        assertThrows(NullPointerException.class, () -> new Item(null, 10.0, 1),
                "Categoria nula deve lançar NullPointerException");
    }

    @Test
    public void deveLancarExcecaoParaPesoDeFreteNegativo() {
        var itens = List.of(new Item("ELETRONICO", 100, 1));
        assertThrows(IllegalArgumentException.class, () ->
                        service.checkout(itens, CustomerTier.BASIC, false, "SUL", -1.0, null, today, null),
                "Peso negativo deve lançar IllegalArgumentException");
    }
}