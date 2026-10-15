# Pocket Arcade (APP05) — Java + XML + View Binding

Projeto Android Studio com **seis Activities** e **quatro desafios**.
Abra a pasta `PocketArcade` no Android Studio (Hedgehog ou mais novo, JDK 17), aguarde o *Gradle Sync* e rode.

## Antes de entregar (EDITE)
1. `app/src/main/res/values/strings.xml` → `aluno_dados`: nome completo, matrícula e curso (em dupla, adicione uma linha com o segundo integrante).
2. Troque logotipo/cores/sons/categorias se quiser personalizar mais (o visual deve ser diferente do dos colegas).
3. **Compile e teste no emulador antes de entregar** (app que não compila perde os critérios de funcionamento).

## Onde cada requisito está
| Requisito | Onde |
|---|---|
| Tela 1: dados, logo, ConstraintLayout, apelido ≥ 3, ViewPropertyAnimator, Intent explícita | `ApresentacaoActivity`, `activity_apresentacao.xml` |
| Tela 1 trata retorno em `onNewIntent()` e mostra o último módulo | `ApresentacaoActivity.onNewIntent` |
| Tela 2: NestedScrollView + um único LinearLayout, `fillViewport`, saudação, 4 painéis, animação XML | `CentralActivity`, `activity_central.xml`, `anim/painel_entrada.xml` |
| Tela 3: ≥ 3 caracteres, Analisar/Limpar, erros, animação, 5 medidas, palíndromo | `ScannerActivity` |
| Tela 4: 3 botões com imagem, melhor de 3, empate não pontua, Nova partida, animações 500 ms, ≥ 2 sons em `res/raw` | `ArenaActivity` |
| MediaPlayer: criar em `onCreate`, `pause()` em `onPause` (sem `stop()`), `release()` em `onDestroy` | `ArenaActivity`, `CodigoSecretoActivity`, `RandomizadorActivity`, `Sons` |
| Tela 5: 4 dígitos sem repetição, 8 tentativas, 3 validações, histórico colorido, vitória/derrota, animação, Nova partida | `CodigoSecretoActivity` |
| Tela 5: tempo com `System.currentTimeMillis()` no início (`novaPartida`) e no fim (`finalizar`), sem Handler/Thread/Runnable | `CodigoSecretoActivity` |
| Tela 6: Spinner, Adicionar/Sortear/Lista-Grade, `notifyItemInserted`, rolagem, clique longo + `notifyItemRemoved`, Linear/Grid, sorteio só na categoria, animação + som | `RandomizadorActivity`, `ItemAdapter`, `ItemSorteio` |
| Navegação: extras como constantes, validação no destino, `finish()` volta ao menu, Encerrar sessão com `CLEAR_TOP \| SINGLE_TOP` | `Extras`, `Navegacao` |
| Ciclo de vida: `Log.d` em `onCreate/onResume/onStop/onDestroy` (filtre por `PocketArcade` no Logcat) | todas as Activities |
| Textos em `strings.xml`, `contentDescription`, IDs claros, `inputType`, cores/dimens em recursos | `res/values`, layouts |

## Proibições respeitadas
Sem `findViewById`, sem `android:onClick`, sem banco/internet (manifesto sem `INTERNET`), sem Handler/Thread/Runnable, sem Compose.

## Roteiro do vídeo
Validações (apelido, mensagem, código) → rolagem da Central → abrir cada módulo e voltar com Voltar/`finish()` → animações e sons → Scanner → Arena (partida completa + Nova partida) → Código Secreto (vitória ou derrota + tempo) → Randomizador (adicionar, excluir por clique longo, sortear, lista/grade) → Encerrar sessão (Tela 1 mostra o último módulo) → Logcat com o ciclo de vida.

## Créditos
- Ícones vetoriais de mensagem, tesoura, cadeado, sorteio, papel, interrogação e controle: Material Icons (Google), licença Apache 2.0. Pedra e logotipo: desenhados no projeto.
- Efeitos sonoros (`res/raw/*.wav`): gerados por código (ondas senoidais), sem direitos de terceiros.
