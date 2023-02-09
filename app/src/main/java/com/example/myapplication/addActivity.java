package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class addActivity extends AppCompatActivity {
    private static final String FILE_NAME ="directions.txt";
    EditText editTextCardNameUrl;
    EditText editTextCardUrl;
    Button cardSaveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        editTextCardNameUrl = findViewById(R.id.editTextCardNameurl);
        editTextCardUrl = findViewById(R.id.editTextCardUrl);
        cardSaveButton =  findViewById(R.id.card_save_button);

        Intent i = getIntent();
        Bundle extras = i.getExtras();

        editTextCardUrl.setText(extras.getString("url"));
    }

    public Boolean fileExist(String dir){
        File archive = new File(getFilesDir()+"/"+dir);
        return archive.exists();
    }

    public void saveList(String file_name){
        JSONArray json = new JSONArray();
        JSONObject obj = new JSONObject();
        try{
            if(fileExist(FILE_NAME)){
                json = readListFile(FILE_NAME);
            }

            obj.put("name", editTextCardNameUrl.getText().toString());
            obj.put("url", editTextCardUrl.getText().toString());
            json.put(obj);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(file_name,MODE_PRIVATE));
            outputStreamWriter.write(json.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();
            Toast.makeText(this, "Updated list", Toast.LENGTH_SHORT).show();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
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

    public void onCardSaveButtonClicked(View view){
        saveList(FILE_NAME);
    }
}