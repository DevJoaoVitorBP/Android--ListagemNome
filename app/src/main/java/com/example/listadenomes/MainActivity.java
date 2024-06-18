package com.example.listadenomes;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNome;
    private Button buttonAdicionar;
    private ListView listViewNomes;

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaNomes;
    private ArrayList<Integer> listaIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNome = findViewById(R.id.editTextNome);
        buttonAdicionar = findViewById(R.id.buttonAdicionar);
        listViewNomes = findViewById(R.id.listViewNomes);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        listaNomes = new ArrayList<>();
        listaIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaNomes);
        listViewNomes.setAdapter(adapter);

        carregarNomes();

        buttonAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = editTextNome.getText().toString();
                if (!nome.isEmpty()) {
                    adicionarNome(nome);
                    editTextNome.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Digite um nome!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listViewNomes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final int itemId = listaIds.get(position);
                final String nomeAtual = listaNomes.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Ações");
                builder.setItems(new CharSequence[]{"Editar", "Excluir"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == 0) {
                            // Editar
                            mostrarDialogEditar(itemId, nomeAtual);
                        } else {
                            // Excluir
                            excluirNome(itemId);
                        }
                    }
                });
                builder.show();
            }
        });

        EditText editTextBusca = findViewById(R.id.editTextBusca);
        editTextBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Não precisa implementar nada aqui
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filtra a lista quando o texto mudar
                MainActivity.this.adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Não precisa implementar nada aqui
            }
        });
    }

    private void carregarNomes() {
        listaNomes.clear();
        listaIds.clear();

        Cursor cursor = db.query(DbHelper.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ID));
                String nome = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NOME));

                listaIds.add(id);
                listaNomes.add(nome);
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void adicionarNome(String nome) {
        db.execSQL("INSERT INTO " + DbHelper.TABLE_NAME + " (" + DbHelper.COLUMN_NOME + ") VALUES ('" + nome + "')");
        carregarNomes();
    }

    private void atualizarNome(int id, String novoNome) {
        db.execSQL("UPDATE " + DbHelper.TABLE_NAME + " SET " + DbHelper.COLUMN_NOME + " = '" + novoNome + "' WHERE " + DbHelper.COLUMN_ID + " = " + id);
        carregarNomes();
    }

    private void excluirNome(int id) {
        db.execSQL("DELETE FROM " + DbHelper.TABLE_NAME + " WHERE " + DbHelper.COLUMN_ID + " = " + id);
        carregarNomes();
    }

    private void mostrarDialogEditar(final int id, String nomeAtual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Editar Nome");

        final EditText input = new EditText(MainActivity.this);
        input.setText(nomeAtual);
        builder.setView(input);

        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String novoNome = input.getText().toString();
                atualizarNome(id, novoNome);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }
}