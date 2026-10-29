package com.duarte.imobiliaria.formsValidation;

public final class ValidacaoConstants {

    private ValidacaoConstants() {}

    // Limites que espelham as colunas do banco (relacional.sql)
    public static final int EMAIL_MAX_SIZE = 50;
    public static final int NOME_MAX_DB_SIZE = 50;
    public static final int PROFISSAO_MAX_DB_SIZE = 30;
    public static final int RUA_MAX_SIZE = 50;
    public static final int BAIRRO_MAX_SIZE = 30;
    public static final int CIDADE_MAX_SIZE = 30;
    public static final int COMPLEMENTO_MAX_SIZE = 10;
    public static final int NUMERO_ENDERECO_MAX = 999_999;

    // Constantes para o login
    public static final int LOGIN_MAX_SIZE = EMAIL_MAX_SIZE;
    public static final int SENHA_MAX_SIZE = 72;

    // Constantes para o cadastro
    public static final int NOME_MIN_SIZE = 3;
    public static final int NOME_MAX_SIZE = 100;
    public static final int PROFISSAO_MAX_SIZE = 50;
    public static final int TELEFONES_MAX_SIZE = 3;
    public static final int SENHA_MIN_SIZE = 8;
    public static final String TELEFONE_PATTERN = "\\d{10,11}";
    public static final String CPF_DIGITOS_PATTERN = "\\d{11}";
    public static final String CNPJ_DIGITOS_PATTERN = "\\d{14}";
    public static final String CEP_PATTERN = "\\d{8}";
    public static final String UF_PATTERN = "[A-Za-z]{2}";
    public static final String RENDA_PATTERN = "\\d{1,9}([.,]\\d{1,2})?";
    public static final String AGENCIA_PATTERN = "\\d{1,5}";
    public static final String CONTA_PATTERN = "\\d{1,12}";
    public static final String DV_PATTERN = "[0-9Xx]";
    public static final String CPF_PATTERN = "[0-9]{3}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[-]?[0-9]{2}";
    public static final String CNPJ_PATTERN = "[0-9]{2}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[\\/]?[0-9]{4}[-]?[0-9]{2}";
    public static final String SENHA_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_]).*$";
}