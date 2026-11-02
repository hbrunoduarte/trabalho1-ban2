package com.duarte.imobiliaria.model;

import java.util.Arrays;

public enum CodigoBancos {
    BancoBrasil(1),
    Santander(33),
    Inter(77),
    Caixa(104),
    Bradesco(237),
    Nubank(260),
    Itau(341),
    C6(336),
    Sicoob(756),
    Sicredi(748);

    private final int codigo;

    CodigoBancos(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static int getCodigoByOrdinal(int i) {
        return values()[i].getCodigo();
    }

    public static int getOrdinalByCodigo(int cod) {
        for (CodigoBancos v : values()) {
            if (v.getCodigo() == cod)
                return v.ordinal();
        }
        return -1;
    }
}
