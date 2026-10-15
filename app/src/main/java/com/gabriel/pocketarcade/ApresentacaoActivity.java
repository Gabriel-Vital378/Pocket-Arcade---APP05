package com.gabriel.pocketarcade;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gabriel.pocketarcade.databinding.ActivityApresentacaoBinding;

/**
 * Tela 1: o jogador se identifica e entra no Pocket Arcade.
 */
public class ApresentacaoActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade";

    private ActivityApresentacaoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ApresentacaoActivity: onCreate");
        binding = ActivityApresentacaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnEntrar.setOnClickListener(v -> tentarEntrar());
    }

    private void tentarEntrar() {
        String apelido = binding.editApelido.getText().toString().trim();
        if (apelido.length() < Extras.MIN_APELIDO) {
            binding.editApelido.setError(getString(R.string.erro_apelido_curto, Extras.MIN_APELIDO));
            binding.editApelido.requestFocus();
            return;
        }
        binding.editApelido.setError(null);
        animarBotaoEAbrir(apelido);
    }

    /** Animacao do botao com ViewPropertyAnimator: reduz, volta ao normal e entao navega. */
    private void animarBotaoEAbrir(final String apelido) {
        binding.btnEntrar.setEnabled(false);
        binding.btnEntrar.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(150L)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.btnEntrar.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150L)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        binding.btnEntrar.animate().setListener(null);
                                        abrirCentral(apelido);
                                    }
                                });
                    }
                });
    }

    private void abrirCentral(String apelido) {
        // Intent explicita; esta Activity permanece na pilha (sem finish()).
        Intent intent = new Intent(this, CentralActivity.class);
        intent.putExtra(Extras.APELIDO, apelido);
        startActivity(intent);
    }

    /** Retorno via CLEAR_TOP | SINGLE_TOP: informa o ultimo modulo usado. */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d(TAG, "ApresentacaoActivity: onNewIntent");

        String ultimo = intent.getStringExtra(Extras.ULTIMO_MODULO);
        if (ultimo == null || ultimo.isEmpty()) {
            binding.txtUltimoModulo.setText(R.string.sessao_encerrada);
        } else {
            binding.txtUltimoModulo.setText(getString(R.string.ultimo_modulo, ultimo));
        }
        binding.txtUltimoModulo.setAlpha(0f);
        binding.txtUltimoModulo.setVisibility(View.VISIBLE);
        binding.txtUltimoModulo.animate().alpha(1f).setDuration(500L).start();
        binding.editApelido.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ApresentacaoActivity: onResume");
        binding.btnEntrar.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ApresentacaoActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ApresentacaoActivity: onDestroy");
    }
}
