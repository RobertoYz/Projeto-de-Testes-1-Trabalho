package org.example;

public class CalculadoraDeDescontos {

    public double calcular(double valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Valor inválido");
        }

        if (valor < 100.0) {
            return 0.0;
        }

        if (valor <= 500.0) {
            return valor * 0.05;
        }

        return valor * 0.10;
    }
}