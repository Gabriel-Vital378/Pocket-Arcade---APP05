package com.gabriel.pocketarcade;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gabriel.pocketarcade.databinding.ActivityCodigoSecretoBinding;
import com.gabriel.pocketarcade.databinding.ItemTentativaBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tela 5: Cofre do Codigo Secreto (quatro digitos diferentes, no maximo oito tentativas).
 */
public class CodigoSecretoActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade";
    private static final int TAMANHO = 4;
    private static final int MAX_TENTATIVAS = 8;

    private ActivityCodigoSecretoBinding binding;
    private MediaPlayer somVitoria;
    private MediaPlayer somDerrota;

    private final int[] segredo = new int[TAMANHO];
    private int tentativas;
    private boolean partidaEncerrada;
    private long inicioMs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "CodigoSecretoActivity: onCreate");

        String apelido = Extras.apelidoValido(getIntent());
        if (apelido == null) {
            finish();
            return;
        }

        binding = ActivityCodigoSecretoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.txtBoasVindas.setText(getString(R.string.boas_vindas_jogador, apelido));

        somVitoria = MediaPlayer.create(this, R.raw.som_vitoria);
        somDerrota = MediaPlayer.create(this, R.raw.som_derrota);

        binding.btnTentar.setOnClickListener(v -> tentar());
        binding.btnNovaPartida.setOnClickListener(v -> novaPartida());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrar.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_cofre)));

        novaPartida();
    }

    private void novaPartida() {
        // Codigo aleatorio de quatro digitos sem repeticao
        List<Integer> digitos = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            digitos.add(i);
        }
        Collections.shuffle(digitos);
        for (int i = 0; i < TAMANHO; i++) {
            segredo[i] = digitos.get(i);
        }

        tentativas = 0;
        partidaEncerrada = false;
        // Tempo SEM atualizacao continua: marca o inicio (a diferenca e calculada ao final)
        inicioMs = System.currentTimeMillis();

        binding.containerHistorico.removeAllViews();
        binding.painelResultado.setVisibility(View.GONE);
        binding.txtErro.setVisibility(View.GONE);
        binding.editPalpite.setText("");
        binding.editPalpite.setEnabled(true);
        binding.btnTentar.setEnabled(true);
        atualizarContador();
    }

    private void tentar() {
        if (partidaEncerrada) {
            return;
        }
        String palpite = binding.editPalpite.getText().toString().trim();

        // Validacoes: campo nao vazio, exatamente quatro numeros, sem digitos repetidos
        if (palpite.isEmpty()) {
            mostrarErro(getString(R.string.erro_codigo_vazio));
            return;
        }
        if (!palpite.matches("\\d{" + TAMANHO + "}")) {
            mostrarErro(getString(R.string.erro_codigo_formato));
            return;
        }
        if (temDigitoRepetido(palpite)) {
            mostrarErro(getString(R.string.erro_codigo_repetido));
            return;
        }
        binding.txtErro.setVisibility(View.GONE);
        tentativas++;

        int corVerde = ContextCompat.getColor(this, R.color.verde);
        int corAmarela = ContextCompat.getColor(this, R.color.amarelo);
        int corCinza = ContextCompat.getColor(this, R.color.cinza);
        int corTexto = ContextCompat.getColor(this, R.color.escuro);

        int certos = 0;
        int deslocados = 0;
        SpannableStringBuilder linha = new SpannableStringBuilder();
        linha.append(getString(R.string.historico_prefixo, tentativas)).append("  ");

        for (int i = 0; i < TAMANHO; i++) {
            int digito = palpite.charAt(i) - '0';
            int cor;
            if (digito == segredo[i]) {
                cor = corVerde;
                certos++;
            } else if (existeNoSegredo(digito)) {
                cor = corAmarela;
                deslocados++;
            } else {
                cor = corCinza;
            }
            int inicio = linha.length();
            linha.append(' ').append(String.valueOf(digito)).append(' ');
            linha.setSpan(new BackgroundColorSpan(cor), inicio, linha.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            linha.setSpan(new ForegroundColorSpan(corTexto), inicio, linha.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            linha.append(' ');
        }
        linha.append("\n").append(getString(R.string.historico_resumo, certos, deslocados));

        // Historico textual na propria tela (tentativa mais recente no topo)
        ItemTentativaBinding item = ItemTentativaBinding.inflate(
                getLayoutInflater(), binding.containerHistorico, false);
        item.txtTentativa.setText(linha);
        binding.containerHistorico.addView(item.getRoot(), 0);

        binding.editPalpite.setText("");
        atualizarContador();

        if (certos == TAMANHO) {
            finalizar(true);
        } else if (tentativas >= MAX_TENTATIVAS) {
            finalizar(false);
        }
    }

    private void finalizar(boolean vitoria) {
        partidaEncerrada = true;
        // Fim da contagem: diferenca entre dois instantes (sem atualizacao continua)
        long fimMs = System.currentTimeMillis();
        long decorridoMs = fimMs - inicioMs;
        long segundos = decorridoMs / 1000L;
        String tempo = getString(R.string.tempo_formato, segundos / 60L, segundos % 60L);

        binding.editPalpite.setEnabled(false);
        binding.btnTentar.setEnabled(false);

        String detalhes = getString(R.string.codigo_detalhes, tentativas, tempo);
        if (vitoria) {
            binding.txtResultadoTitulo.setText(R.string.codigo_vitoria);
            binding.txtResultadoTitulo.setTextColor(ContextCompat.getColor(this, R.color.verde));
        } else {
            binding.txtResultadoTitulo.setText(R.string.codigo_derrota);
            binding.txtResultadoTitulo.setTextColor(ContextCompat.getColor(this, R.color.vermelho));
            detalhes = getString(R.string.codigo_revelado, codigoComoTexto()) + "\n" + detalhes;
        }
        binding.txtResultadoDetalhes.setText(detalhes);

        binding.painelResultado.setVisibility(View.VISIBLE);
        binding.painelResultado.startAnimation(AnimationUtils.loadAnimation(
                this, vitoria ? R.anim.sucesso : R.anim.falha));
        Sons.tocar(vitoria ? somVitoria : somDerrota);
    }

    private boolean temDigitoRepetido(String palpite) {
        for (int i = 0; i < palpite.length(); i++) {
            for (int j = i + 1; j < palpite.length(); j++) {
                if (palpite.charAt(i) == palpite.charAt(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeNoSegredo(int digito) {
        for (int valor : segredo) {
            if (valor == digito) {
                return true;
            }
        }
        return false;
    }

    private String codigoComoTexto() {
        StringBuilder texto = new StringBuilder();
        for (int valor : segredo) {
            texto.append(valor);
        }
        return texto.toString();
    }

    private void atualizarContador() {
        binding.txtTentativas.setText(
                getString(R.string.tentativas_contador, tentativas, MAX_TENTATIVAS));
    }

    private void mostrarErro(String mensagem) {
        binding.txtErro.setText(mensagem);
        binding.txtErro.setVisibility(View.VISIBLE);
        binding.txtErro.startAnimation(AnimationUtils.loadAnimation(this, R.anim.falha));
        binding.editPalpite.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "CodigoSecretoActivity: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Sons.pausar(somVitoria);
        Sons.pausar(somDerrota);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "CodigoSecretoActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "CodigoSecretoActivity: onDestroy");
        if (somVitoria != null) {
            somVitoria.release();
            somVitoria = null;
        }
        if (somDerrota != null) {
            somDerrota.release();
            somDerrota = null;
        }
    }
}
