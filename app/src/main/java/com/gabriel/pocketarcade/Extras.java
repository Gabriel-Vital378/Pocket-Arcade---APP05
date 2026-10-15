package com.gabriel.pocketarcade;

import android.content.Intent;

/**
 * Constantes das chaves de extras das Intents e validacao no destino.
 */
public final class Extras {

    public static final String APELIDO = "com.gabriel.pocketarcade.extra.APELIDO";
    public static final String ULTIMO_MODULO = "com.gabriel.pocketarcade.extra.ULTIMO_MODULO";
    public static final int MIN_APELIDO = 3;

    private Extras() {
    }

    /**
     * Valida no destino o apelido recebido pela Intent.
     *
     * @return o apelido valido ou null se estiver ausente/invalido.
     */
    public static String apelidoValido(Intent intent) {
        if (intent == null) {
            return null;
        }
        String apelido = intent.getStringExtra(APELIDO);
        if (apelido == null || apelido.trim().length() < MIN_APELIDO) {
            return null;
        }
        return apelido.trim();
    }
}
