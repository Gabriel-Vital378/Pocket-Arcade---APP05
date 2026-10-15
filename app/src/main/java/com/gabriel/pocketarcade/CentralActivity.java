package com.gabriel.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.gabriel.pocketarcade.databinding.ActivityCentralBinding;

/**
 * Tela 2: menu rolavel com quatro paineis de desafio.
 * finish() (botao Voltar do sistema) revela a Tela 1, que ja existe na pilha.
 */
public class CentralActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade";

    private ActivityCentralBinding binding;
    private String apelido;
    private String ultimoModulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "CentralActivity: onCreate");

        apelido = Extras.apelidoValido(getIntent());
        if (apelido == null) {
            finish();
            return;
        }

        binding = ActivityCentralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.txtSaudacao.setText(getString(R.string.saudacao, apelido));

        binding.btnScanner.setOnClickListener(v ->
                abrirDesafio(ScannerActivity.class, getString(R.string.modulo_scanner)));
        binding.btnArena.setOnClickListener(v ->
                abrirDesafio(ArenaActivity.class, getString(R.string.modulo_arena)));
        binding.btnCofre.setOnClickListener(v ->
                abrirDesafio(CodigoSecretoActivity.class, getString(R.string.modulo_cofre)));
        binding.btnRandomizador.setOnClickListener(v ->
                abrirDesafio(RandomizadorActivity.class, getString(R.string.modulo_randomizador)));
        binding.btnEncerrar.setOnClickListener(v -> Navegacao.encerrarSessao(this, ultimoModulo));

        animarPaineis();
    }

    /** Animacao XML de entrada (fade + translate), escalonada por painel. */
    private void animarPaineis() {
        View[] paineis = {
                binding.painelScanner,
                binding.painelArena,
                binding.painelCofre,
                binding.painelRandomizador
        };
        for (int i = 0; i < paineis.length; i++) {
            Animation animacao = AnimationUtils.loadAnimation(this, R.anim.painel_entrada);
            animacao.setStartOffset(i * 150L);
            paineis[i].startAnimation(animacao);
        }
    }

    private void abrirDesafio(Class<?> destino, String nomeModulo) {
        ultimoModulo = nomeModulo;
        Intent intent = new Intent(this, destino);
        intent.putExtra(Extras.APELIDO, apelido);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "CentralActivity: onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "CentralActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "CentralActivity: onDestroy");
    }
}
