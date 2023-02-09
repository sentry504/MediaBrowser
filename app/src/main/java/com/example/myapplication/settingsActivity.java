package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class settingsActivity extends AppCompatActivity {
    private static final String FILE_NAME ="settings.txt";
    EditText editTextUrl;
    SwitchMaterial switch1;
    SwitchMaterial switch2;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editTextUrl = findViewById(R.id.editTextUrl);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        btnSave = findViewById(R.id.btnSave);

        if (fileExist(FILE_NAME)) {
            readSettingsFile(FILE_NAME);
        }
    }

    public String estructure(String url, Boolean zoom_controls, Boolean search_bar) {
        JSONObject json = new JSONObject();
        try{
            json.put("url",url);
            json.put("zoom_controls",zoom_controls);
            json.put("search_bar",search_bar);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return json.toString();
    }

    public Boolean fileExist(String dir){
        File archive = new File(getFilesDir()+"/"+dir);
        return archive.exists();
    }

    public void saveSettings(String file_name){
        String estructura="";
        if(fileExist(FILE_NAME)){
            estructura = estructure(editTextUrl.getText().toString(),switch1.isChecked(),switch2.isChecked());
        }else{
            estructura = estructure("https://www.google.com/",false,false);
        }

        try{
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(file_name,MODE_PRIVATE));
            outputStreamWriter.write(estructura);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            Toast.makeText(this, "Settings updated", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readSettingsFile(String file_name) {
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
            JSONObject json = new JSONObject(contenido);
            //Optencion de cada uno de los campos de objeto json siguiendo la estructura asignada en estructure()
            String  url;
            Boolean zoom_controls, search_bar;

            url = json.get("url").toString();
            zoom_controls =Boolean.valueOf(json.get("zoom_controls").toString());
            search_bar = Boolean.valueOf(json.get("search_bar").toString());

            //Setea los controles de acuerdo a la informacion del archivo settings.txt
            editTextUrl.setText(url);
            switch1.setChecked(zoom_controls);
            switch2.setChecked(search_bar);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onSaveButtonClicked(View view){
        saveSettings(FILE_NAME);
    }
}
