import org.example.CalculadoraDeDescontos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculadoraDeDescontosTest {

    CalculadoraDeDescontos calculadora = new CalculadoraDeDescontos();

    @Test
    void deveLancarException_QuandoValorForNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculadora.calcular(-50.0);
        });
    }

    @Test
    void deveRetornarZero_QuandoValorAbaixoDe100() {
        assertEquals(0.0, calculadora.calcular(99.90), 0.001);
    }

    @Test
    void deveRetornar5Porcento_QuandoValorEntre100e500() {
        assertEquals(5.0, calculadora.calcular(100.0), 0.001);
        assertEquals(10.0, calculadora.calcular(200.0), 0.001);
        assertEquals(25.0, calculadora.calcular(500.0), 0.001);
    }

    @Test
    void deveRetornar10Porcento_QuandoValorAcimaDe500() {
        assertEquals(100.0, calculadora.calcular(1000.0), 0.001);
    }
}