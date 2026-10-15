package com.gabriel.pocketarcade;

import android.app.Activity;
import android.content.Intent;

/**
 * Encerrar sessao: volta a Tela 1 reaproveitando a instancia que ja esta na pilha
 * (CLEAR_TOP | SINGLE_TOP), que trata o retorno em onNewIntent().
 */
public final class Navegacao {

    private Navegacao() {
    }

    public static void encerrarSessao(Activity origem, String ultimoModulo) {
        Intent intent = new Intent(origem, ApresentacaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (ultimoModulo != null) {
            intent.putExtra(Extras.ULTIMO_MODULO, ultimoModulo);
        }
        origem.startActivity(intent);
    }
}
