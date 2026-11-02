package com.duarte.imobiliaria.formsValidation.util;

public class DocumentoUtil {

    private DocumentoUtil() {}

    public static String apenasDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    public static boolean cpfValido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || todosIguais(cpf)) {
            return false;
        }
        int d1 = digito(cpf, 9, 10);
        int d2 = digito(cpf, 10, 11);
        return d1 == cpf.charAt(9) - '0' && d2 == cpf.charAt(10) - '0';
    }

    public static boolean cnpjValido(String cnpj) {
        if (cnpj == null || !cnpj.matches("\\d{14}") || todosIguais(cnpj)) {
            return false;
        }
        int d1 = digitoCnpj(cnpj, 12);
        int d2 = digitoCnpj(cnpj, 13);
        return d1 == cnpj.charAt(12) - '0' && d2 == cnpj.charAt(13) - '0';
    }

    private static boolean todosIguais(String s) {
        return s.chars().distinct().count() == 1;
    }

    private static int digito(String s, int tamanho, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (s.charAt(i) - '0') * (pesoInicial - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    private static int digitoCnpj(String s, int tamanho) {
        int soma = 0;
        int peso = tamanho - 7;
        for (int i = 0; i < tamanho; i++) {
            soma += (s.charAt(i) - '0') * peso;
            peso = (peso == 2) ? 9 : peso - 1;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

}
