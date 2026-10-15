package com.gabriel.pocketarcade;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gabriel.pocketarcade.databinding.ActivityRandomizadorBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tela 6: Randomizador que sorteia itens por categoria usando RecyclerView.
 */
public class RandomizadorActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade";
    private static final int COLUNAS_GRADE = 2;

    private ActivityRandomizadorBinding binding;
    private final List<ItemSorteio> itens = new ArrayList<>();
    private final Random random = new Random();

    private ItemAdapter adapter;
    private LinearLayoutManager layoutLista;
    private GridLayoutManager layoutGrade;
    private boolean exibindoGrade = false;

    private MediaPlayer somSorteio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "RandomizadorActivity: onCreate");

        String apelido = Extras.apelidoValido(getIntent());
        if (apelido == null) {
            finish();
            return;
        }

        binding = ActivityRandomizadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.txtBoasVindas.setText(getString(R.string.boas_vindas_jogador, apelido));

        somSorteio = MediaPlayer.create(this, R.raw.som_sorteio);

        popularExemplos();
        adapter = new ItemAdapter(itens, this::removerItem);
        layoutLista = new LinearLayoutManager(this);
        layoutGrade = new GridLayoutManager(this, COLUNAS_GRADE);
        binding.recyclerItens.setLayoutManager(layoutLista);
        binding.recyclerItens.setAdapter(adapter);

        binding.btnAdicionar.setOnClickListener(v -> adicionar());
        binding.btnSortear.setOnClickListener(v -> sortear());
        binding.btnAlternar.setOnClickListener(v -> alternarLayout());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrar.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_randomizador)));
    }

    private void popularExemplos() {
        String[] categorias = getResources().getStringArray(R.array.categorias);
        itens.add(new ItemSorteio(getString(R.string.seed_filme_1), categorias[0]));
        itens.add(new ItemSorteio(getString(R.string.seed_filme_2), categorias[0]));
        itens.add(new ItemSorteio(getString(R.string.seed_comida_1), categorias[1]));
        itens.add(new ItemSorteio(getString(R.string.seed_comida_2), categorias[1]));
        itens.add(new ItemSorteio(getString(R.string.seed_lugar_1), categorias[2]));
        itens.add(new ItemSorteio(getString(R.string.seed_lugar_2), categorias[2]));
        itens.add(new ItemSorteio(getString(R.string.seed_desafio_1), categorias[3]));
        itens.add(new ItemSorteio(getString(R.string.seed_desafio_2), categorias[3]));
    }

    private String categoriaSelecionada() {
        return String.valueOf(binding.spinnerCategoria.getSelectedItem());
    }

    /** Cadastro: notifyItemInserted() e rolagem ate o novo item. */
    private void adicionar() {
        String valor = binding.editValor.getText().toString().trim();
        String categoria = categoriaSelecionada();

        if (valor.isEmpty()) {
            mostrarErro(getString(R.string.erro_valor_vazio));
            return;
        }
        for (ItemSorteio existente : itens) {
            if (existente.getCategoria().equals(categoria)
                    && existente.getPalavra().equalsIgnoreCase(valor)) {
                mostrarErro(getString(R.string.erro_item_duplicado, categoria));
                return;
            }
        }
        binding.txtErro.setVisibility(View.GONE);

        itens.add(new ItemSorteio(valor, categoria));
        int posicao = itens.size() - 1;
        adapter.notifyItemInserted(posicao);
        binding.recyclerItens.smoothScrollToPosition(posicao);
        binding.editValor.setText("");
    }

    /** Exclusao por clique longo: notifyItemRemoved(). */
    private void removerItem(ItemSorteio item, int posicao) {
        if (posicao < 0 || posicao >= itens.size()) {
            return;
        }
        if (item == adapter.getDestacado()) {
            adapter.setDestacado(null);
        }
        itens.remove(posicao);
        adapter.notifyItemRemoved(posicao);
        Toast.makeText(this, getString(R.string.item_removido, item.getPalavra()),
                Toast.LENGTH_SHORT).show();
    }

    /** Sorteia apenas na categoria selecionada, com animacao e som. */
    private void sortear() {
        String categoria = categoriaSelecionada();
        List<ItemSorteio> candidatos = new ArrayList<>();
        for (ItemSorteio item : itens) {
            if (item.getCategoria().equals(categoria)) {
                candidatos.add(item);
            }
        }
        if (candidatos.isEmpty()) {
            mostrarErro(getString(R.string.erro_categoria_vazia, categoria));
            return;
        }
        binding.txtErro.setVisibility(View.GONE);

        ItemSorteio sorteado = candidatos.get(random.nextInt(candidatos.size()));
        adapter.setDestacado(sorteado);
        binding.recyclerItens.smoothScrollToPosition(itens.indexOf(sorteado));

        binding.txtSorteado.setText(getString(R.string.sorteado,
                sorteado.getPalavra(), sorteado.getCategoria()));
        binding.txtSorteado.startAnimation(AnimationUtils.loadAnimation(this, R.anim.sorteio));
        Sons.tocar(somSorteio);
    }

    /** Alterna entre LinearLayoutManager (lista) e GridLayoutManager (grade). */
    private void alternarLayout() {
        exibindoGrade = !exibindoGrade;
        binding.recyclerItens.setLayoutManager(exibindoGrade ? layoutGrade : layoutLista);
        binding.btnAlternar.setText(exibindoGrade ? R.string.btn_ver_lista : R.string.btn_ver_grade);
    }

    private void mostrarErro(String mensagem) {
        binding.txtErro.setText(mensagem);
        binding.txtErro.setVisibility(View.VISIBLE);
        binding.txtErro.startAnimation(AnimationUtils.loadAnimation(this, R.anim.falha));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "RandomizadorActivity: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Sons.pausar(somSorteio);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "RandomizadorActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "RandomizadorActivity: onDestroy");
        if (somSorteio != null) {
            somSorteio.release();
            somSorteio = null;
        }
    }
}
