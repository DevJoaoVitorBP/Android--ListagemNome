package com.example.listadenomes;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNome;
    private Button buttonAdicionar;
    private ListView listViewNomes;
    private EditText editTextBusca;

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaNomes;
    private ArrayList<Integer> listaIds;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNome = findViewById(R.id.editTextNome);
        buttonAdicionar = findViewById(R.id.buttonAdicionar);
        listViewNomes = findViewById(R.id.listViewNomes);
        editTextBusca = findViewById(R.id.editTextBusca);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        listaNomes = new ArrayList<>();
        listaIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaNomes);
        listViewNomes.setAdapter(adapter);

        SharedPreferences sharedPref = getSharedPreferences("user_data", MODE_PRIVATE);
        userRole = sharedPref.getString("role", "User");

        // Controlar a visibilidade dos elementos da interface do usuário com base na função do usuário
        if (userRole.equals("Admin")) {
            editTextNome.setVisibility(View.VISIBLE);
            buttonAdicionar.setVisibility(View.VISIBLE);
        } else {
            editTextNome.setVisibility(View.GONE);
            buttonAdicionar.setVisibility(View.GONE);
        }

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

                if (userRole.equals("Admin")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Ações");
                    builder.setItems(new CharSequence[]{"Editar", "Excluir"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            if (which == 0) {
                                mostrarDialogEditar(itemId, nomeAtual);
                            } else {
                                excluirNome(itemId);
                            }
                        }
                    });
                    builder.show();
                }
            }
        });

        editTextBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
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
        dbHelper.adicionarNome(db, nome, userRole);
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