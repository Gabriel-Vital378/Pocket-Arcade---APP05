package com.gabriel.pocketarcade;

import android.media.MediaPlayer;

/**
 * Apoio para reutilizar instancias de MediaPlayer ja criadas em onCreate().
 * Nunca cria players aqui e nunca chama stop(): ao sair da tela apenas pausa.
 */
public final class Sons {

    private Sons() {
    }

    public static void tocar(MediaPlayer player) {
        if (player == null) {
            return;
        }
        if (player.isPlaying()) {
            player.pause();
        }
        player.seekTo(0);
        player.start();
    }

    public static void pausar(MediaPlayer player) {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }
}
