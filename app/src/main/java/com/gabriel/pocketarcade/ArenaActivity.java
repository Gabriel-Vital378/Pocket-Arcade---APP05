package com.gabriel.pocketarcade;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.gabriel.pocketarcade.databinding.ActivityArenaBinding;

import java.util.Random;

/**
 * Tela 4: Arena de Pedra, Papel e Tesoura, melhor de tres.
 * Placar e resultado final ficam nesta mesma Activity.
 */
public class ArenaActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade";

    private static final int PEDRA = 0;
    private static final int PAPEL = 1;
    private static final int TESOURA = 2;
    private static final int VITORIAS_PARA_VENCER = 2;
    private static final long DURACAO_ANIMACAO = 500L; // entre 300 e 700 ms

    private static final int[] ICONES = {R.drawable.ic_pedra, R.drawable.ic_papel, R.drawable.ic_tesoura};
    private static final int[] NOMES = {R.string.jogada_pedra, R.string.jogada_papel, R.string.jogada_tesoura};

    private ActivityArenaBinding binding;
    private final Random random = new Random();

    private MediaPlayer somVitoria;
    private MediaPlayer somDerrota;
    private MediaPlayer somEmpate;

    private int placarJogador;
    private int placarDispositivo;
    private boolean partidaEncerrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ArenaActivity: onCreate");

        String apelido = Extras.apelidoValido(getIntent());
        if (apelido == null) {
            finish();
            return;
        }

        binding = ActivityArenaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.txtBoasVindas.setText(getString(R.string.boas_vindas_jogador, apelido));

        // MediaPlayer: criado UMA vez em onCreate() e reutilizado (nunca dentro de listeners)
        somVitoria = MediaPlayer.create(this, R.raw.som_vitoria);
        somDerrota = MediaPlayer.create(this, R.raw.som_derrota);
        somEmpate = MediaPlayer.create(this, R.raw.som_empate);

        binding.btnPedra.setOnClickListener(v -> jogar(PEDRA));
        binding.btnPapel.setOnClickListener(v -> jogar(PAPEL));
        binding.btnTesoura.setOnClickListener(v -> jogar(TESOURA));
        binding.btnNovaPartida.setOnClickListener(v -> reiniciar());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrar.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_arena)));

        reiniciar();
    }

    private void jogar(int jogador) {
        if (partidaEncerrada) {
            return;
        }
        int dispositivo = random.nextInt(3);
        // 0 = empate, 1 = jogador vence, 2 = dispositivo vence
        int desfecho = (jogador - dispositivo + 3) % 3;

        mostrarEscolha(binding.imgJogador, jogador);
        mostrarEscolha(binding.imgDispositivo, dispositivo);

        String mensagem;
        MediaPlayer som;
        if (desfecho == 0) {
            mensagem = getString(R.string.arena_empate);
            som = somEmpate;
        } else if (desfecho == 1) {
            placarJogador++;
            mensagem = getString(R.string.arena_vitoria_rodada);
            som = somVitoria;
        } else {
            placarDispositivo++;
            mensagem = getString(R.string.arena_derrota_rodada);
            som = somDerrota;
        }
        atualizarPlacar();

        if (placarJogador == VITORIAS_PARA_VENCER) {
            partidaEncerrada = true;
            mensagem = getString(R.string.arena_vitoria_final, placarJogador, placarDispositivo);
            som = somVitoria;
        } else if (placarDispositivo == VITORIAS_PARA_VENCER) {
            partidaEncerrada = true;
            mensagem = getString(R.string.arena_derrota_final, placarJogador, placarDispositivo);
            som = somDerrota;
        }
        if (partidaEncerrada) {
            habilitarJogadas(false);
        }

        mostrarMensagem(mensagem);
        Sons.tocar(som);
    }

    private void mostrarEscolha(ImageView imagem, int escolha) {
        imagem.setImageResource(ICONES[escolha]);
        imagem.setContentDescription(getString(NOMES[escolha]));
        imagem.setScaleX(0f);
        imagem.setScaleY(0f);
        imagem.setAlpha(0f);
        imagem.setRotation(-90f);
        imagem.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .rotation(0f)
                .setDuration(DURACAO_ANIMACAO)
                .start();
    }

    private void mostrarMensagem(String mensagem) {
        binding.txtResultado.setText(mensagem);
        binding.txtResultado.setAlpha(0f);
        binding.txtResultado.setTranslationY(40f);
        binding.txtResultado.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(DURACAO_ANIMACAO)
                .start();
    }

    private void atualizarPlacar() {
        binding.txtPlacar.setText(getString(R.string.arena_placar, placarJogador, placarDispositivo));
    }

    private void habilitarJogadas(boolean habilitar) {
        float alfa = habilitar ? 1f : 0.4f;
        binding.btnPedra.setEnabled(habilitar);
        binding.btnPapel.setEnabled(habilitar);
        binding.btnTesoura.setEnabled(habilitar);
        binding.btnPedra.setAlpha(alfa);
        binding.btnPapel.setAlpha(alfa);
        binding.btnTesoura.setAlpha(alfa);
    }

    /** "Nova partida" reinicia o placar. */
    private void reiniciar() {
        placarJogador = 0;
        placarDispositivo = 0;
        partidaEncerrada = false;
        atualizarPlacar();
        habilitarJogadas(true);
        binding.imgJogador.setImageResource(R.drawable.ic_interrogacao);
        binding.imgJogador.setContentDescription(getString(R.string.cd_aguardando_jogada));
        binding.imgDispositivo.setImageResource(R.drawable.ic_interrogacao);
        binding.imgDispositivo.setContentDescription(getString(R.string.cd_aguardando_jogada));
        binding.txtResultado.setText(R.string.arena_aguardando);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ArenaActivity: onResume");
    }

    /** Ao sair da tela, pausa o audio (nunca stop()). */
    @Override
    protected void onPause() {
        super.onPause();
        Sons.pausar(somVitoria);
        Sons.pausar(somDerrota);
        Sons.pausar(somEmpate);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ArenaActivity: onStop");
    }

    /** Libera os recursos: release() leva cada player ao estado End. */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ArenaActivity: onDestroy");
        if (somVitoria != null) {
            somVitoria.release();
            somVitoria = null;
        }
        if (somDerrota != null) {
            somDerrota.release();
            somDerrota = null;
        }
        if (somEmpate != null) {
            somEmpate.release();
            somEmpate = null;
        }
    }
}
