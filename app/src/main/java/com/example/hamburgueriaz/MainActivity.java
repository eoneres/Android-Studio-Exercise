package com.example.hamburgueriaz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Views
    private EditText editTextNome;
    private CheckBox checkBoxBacon;
    private CheckBox checkBoxQueijo;
    private CheckBox checkBoxOnionRings;
    private TextView textQuantidade;
    private TextView textResumoPedido;

    // Preços
    private static final double PRECO_BASE = 20.0;
    private static final double PRECO_BACON = 2.0;
    private static final double PRECO_QUEIJO = 2.0;
    private static final double PRECO_ONION_RINGS = 3.0;

    // Quantidade atual
    private int quantidade = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar views
        editTextNome       = findViewById(R.id.editTextNome);
        checkBoxBacon      = findViewById(R.id.checkBoxBacon);
        checkBoxQueijo     = findViewById(R.id.checkBoxQueijo);
        checkBoxOnionRings = findViewById(R.id.checkBoxOnionRings);
        textQuantidade     = findViewById(R.id.textQuantidade);
        textResumoPedido   = findViewById(R.id.textResumoPedido);
    }

    // ---------------------------------------------------------------
    // Etapa 5 – Funções somar e subtrair
    // ---------------------------------------------------------------

    /** Chamada pelo botão "+" */
    public void somar(View view) {
        quantidade++;
        textQuantidade.setText(String.valueOf(quantidade));
    }

    /** Chamada pelo botão "-" */
    public void subtrair(View view) {
        if (quantidade > 0) {          // Impede valores negativos
            quantidade--;
            textQuantidade.setText(String.valueOf(quantidade));
        }
    }

    // ---------------------------------------------------------------
    // Etapa 6 – Calcular preço total
    // ---------------------------------------------------------------

    private double calcularPrecoTotal() {
        double precoAdicionais = 0;

        if (checkBoxBacon.isChecked())      precoAdicionais += PRECO_BACON;
        if (checkBoxQueijo.isChecked())     precoAdicionais += PRECO_QUEIJO;
        if (checkBoxOnionRings.isChecked()) precoAdicionais += PRECO_ONION_RINGS;

        return (PRECO_BASE + precoAdicionais) * quantidade;
    }

    // ---------------------------------------------------------------
    // Etapa 6 – Enviar pedido (montar resumo)
    // Etapa 7 – Intent ACTION_SENDTO para abrir app de e-mail
    // ---------------------------------------------------------------

    /** Chamada pelo botão "FAZER PEDIDO" */
    public void enviarPedido(View view) {

        // Coletar dados
        String nome         = editTextNome.getText().toString().trim();
        boolean temBacon      = checkBoxBacon.isChecked();
        boolean temQueijo     = checkBoxQueijo.isChecked();
        boolean temOnionRings = checkBoxOnionRings.isChecked();
        double precoFinal   = calcularPrecoTotal();

        // Validação básica
        if (nome.isEmpty()) {
            editTextNome.setError("Por favor, informe seu nome.");
            editTextNome.requestFocus();
            return;
        }
        if (quantidade == 0) {
            textQuantidade.setError("Selecione ao menos 1 hambúrguer.");
            return;
        }

        // Montar mensagem de resumo
        String resumo =
                "Nome: " + nome + "\n" +
                "Tem Bacon? "       + (temBacon      ? "Sim" : "Não") + "\n" +
                "Tem Queijo? "      + (temQueijo     ? "Sim" : "Não") + "\n" +
                "Tem Onion Rings? " + (temOnionRings ? "Sim" : "Não") + "\n" +
                "Quantidade: "      + quantidade + "\n" +
                "Preço final: R$ "  + String.format("%.2f", precoFinal);

        // Exibir resumo na view da tela
        textResumoPedido.setText(resumo);

        // -------------------------------------------------------
        // Etapa 7 – Intent para abrir aplicativo de e-mail
        // -------------------------------------------------------
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));                          // somente apps de e-mail
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Pedido de " + nome);   // assunto
        emailIntent.putExtra(Intent.EXTRA_TEXT, resumo);                    // corpo

        // Verificar se há algum app capaz de lidar com o Intent
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        } else {
            // Fallback: Intent genérico de envio
            Intent fallback = new Intent(Intent.ACTION_SEND);
            fallback.setType("message/rfc822");
            fallback.putExtra(Intent.EXTRA_SUBJECT, "Pedido de " + nome);
            fallback.putExtra(Intent.EXTRA_TEXT, resumo);
            startActivity(Intent.createChooser(fallback, "Enviar pedido via:"));
        }
    }
}
