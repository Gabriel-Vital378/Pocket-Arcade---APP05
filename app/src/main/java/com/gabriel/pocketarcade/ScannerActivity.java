package com.gabriel.pocketarcade;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.gabriel.pocketarcade.databinding.ActivityScannerBinding;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Tela 3: o Scanner de Mensagens analisa um texto em cinco medidas.
 */
public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade";
    private static final int MIN_CARACTERES = 3;

    private ActivityScannerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ScannerActivity: onCreate");

        String apelido = Extras.apelidoValido(getIntent());
        if (apelido == null) {
            finish();
            return;
        }

        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.txtBoasVindas.setText(getString(R.string.boas_vindas_jogador, apelido));
        binding.btnAnalisar.setOnClickListener(v -> analisar());
        binding.btnLimpar.setOnClickListener(v -> limpar());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrar.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_scanner)));
    }

    private void analisar() {
        String texto = binding.editMensagem.getText().toString().trim();

        if (texto.isEmpty()) {
            mostrarErro(getString(R.string.erro_mensagem_vazia));
            return;
        }
        if (texto.length() < MIN_CARACTERES) {
            mostrarErro(getString(R.string.erro_mensagem_curta, MIN_CARACTERES));
            return;
        }
        binding.txtErro.setVisibility(View.GONE);

        // 1) Caracteres sem espacos (e texto compacto em minusculas para o palindromo)
        int semEspacos = 0;
        StringBuilder compacto = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (!Character.isWhitespace(c)) {
                semEspacos++;
                compacto.append(Character.toLowerCase(c));
            }
        }

        // 2) Palavras
        int palavras = texto.split("\\s+").length;

        // 3) Vogais (acentos ignorados na contagem: a, e, i, o, u)
        int vogais = contarVogais(texto);

        // 4) Texto invertido
        String invertido = new StringBuilder(texto).reverse().toString();

        // 5) Palindromo: ignora espacos e diferencas entre maiusculas e minusculas
        String semEspacosMinusculo = compacto.toString();
        boolean palindromo = semEspacosMinusculo.equals(
                new StringBuilder(semEspacosMinusculo).reverse().toString());

        binding.valorCaracteres.setText(String.valueOf(semEspacos));
        binding.valorPalavras.setText(String.valueOf(palavras));
        binding.valorVogais.setText(String.valueOf(vogais));
        binding.valorInvertido.setText(invertido);
        binding.valorPalindromo.setText(palindromo ? R.string.sim : R.string.nao);

        binding.painelResultado.setVisibility(View.VISIBLE);
        binding.painelResultado.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.resultado_entrada));
    }

    private int contarVogais(String texto) {
        String base = Normalizer.normalize(texto, Normalizer.Form.NFD).toLowerCase(Locale.ROOT);
        int total = 0;
        for (int i = 0; i < base.length(); i++) {
            if ("aeiou".indexOf(base.charAt(i)) >= 0) {
                total++;
            }
        }
        return total;
    }

    private void mostrarErro(String mensagem) {
        binding.painelResultado.setVisibility(View.GONE);
        binding.txtErro.setText(mensagem);
        binding.txtErro.setVisibility(View.VISIBLE);
        binding.txtErro.startAnimation(AnimationUtils.loadAnimation(this, R.anim.falha));
        binding.editMensagem.requestFocus();
    }

    private void limpar() {
        binding.editMensagem.setText("");
        binding.txtErro.setVisibility(View.GONE);
        binding.painelResultado.setVisibility(View.GONE);
        binding.editMensagem.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ScannerActivity: onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ScannerActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ScannerActivity: onDestroy");
    }
}
