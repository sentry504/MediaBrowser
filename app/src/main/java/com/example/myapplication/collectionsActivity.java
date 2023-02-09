package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class collectionsActivity extends AppCompatActivity {
    private static final String FILE_NAME = "directions.txt";
    RecyclerView rv_item;
    private adapterRecycler adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        rv_item = findViewById(R.id.rv_item);

        llenarRecycler();
    }

    private void llenarRecycler() {
        JSONArray json = new JSONArray();
        JSONObject obj = new JSONObject();

       try{
           if(fileExist(FILE_NAME)){
               json = readListFile(FILE_NAME);
           }else{
               obj.put("name","Google");
               obj.put("url","https://www.google.com");
               json.put(obj);
           }
       } catch (JSONException e) {
           throw new RuntimeException(e);
       }

        adapter = new adapterRecycler(json);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_item.setLayoutManager(layoutManager);
        rv_item.addItemDecoration(new DividerItemDecoration(rv_item.getContext(), DividerItemDecoration.VERTICAL));
        rv_item.setAdapter(adapter);

        JSONArray finalJson = json;
        ItemTouchHelper.SimpleCallback  simpleCallback= new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                finalJson.remove(viewHolder.getAdapterPosition());
                try{
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FILE_NAME,MODE_PRIVATE));
                    outputStreamWriter.write(finalJson.toString());
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                    Toast.makeText(collectionsActivity.this, "deleted item", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }

        };

        ItemTouchHelper itemDeleteTouchHelper = new ItemTouchHelper(simpleCallback);
        itemDeleteTouchHelper.attachToRecyclerView(rv_item);
    }

    public Boolean fileExist(String dir){
        File archive = new File(getFilesDir()+"/"+dir);
        return archive.exists();
    }

    public JSONArray readListFile(String file_name) {
        try {
            //Lectura del archivo, procesamiento para conversion a texto
            InputStreamReader inputStreamReader = new InputStreamReader(openFileInput(file_name));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String linea = bufferedReader.readLine();
            String contenido = "";
            while (linea != null){
                contenido += linea + "\n";
                linea = bufferedReader.readLine();
            }
            bufferedReader.close();
            inputStreamReader.close();

            //Conversion del contenido del archivo settings.txt a objeto json
            return new JSONArray(contenido);

        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}