package com.example.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchView searchViewUrl;
    SwipeRefreshLayout mySwipeRefreshLayout;
    WebView myWebView;
    Boolean clicked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = findViewById(R.id.webView);
        searchViewUrl = findViewById(R.id.searchViewUrl);
        
        WebSettings webSettings= myWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setAllowFileAccess(true);

        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        myWebView.setVerticalScrollBarEnabled(false);

        //Creación de los clientes del WebView
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.setWebChromeClient(new MyWebChromeClient());

        readSettingsFile("settings.txt");

        /*
        Cuando el usuario intenta subir entanto en el tope de la pagina, es decir, realiza la accion
        de querer recargar la pagina, entonces se ejecutará este codigo correspondiente
        */
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            findViewById(R.id.loaderWebView).setVisibility(View.VISIBLE);
            findViewById(R.id.fabBtn1).setVisibility(View.INVISIBLE);

            myWebView.reload();
            mySwipeRefreshLayout.setRefreshing(false);
        });
        searchViewUrl.setOnQueryTextListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.setWebChromeClient(new MyWebChromeClient());

        Intent i = getIntent();
        if (i.hasExtra("collections_selected_url")){
            Bundle collectionsExtras = i.getExtras();
            myWebView.loadUrl(collectionsExtras.getString("collections_selected_url"));
        }else{
            readSettingsFile("settings.txt");
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        myWebView.loadUrl(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    //INICIO DE LAS CONFIGURACIONES DEL WebViewClient
    private class MyWebViewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url){
            super.onPageFinished(view, url);

            findViewById(R.id.loaderWebView).setVisibility(View.GONE);

            findViewById(R.id.fabBtn1).setVisibility(View.VISIBLE);
            findViewById(R.id.fabBtn2).setVisibility(View.INVISIBLE);
            findViewById(R.id.fabBtn3).setVisibility(View.INVISIBLE);
            findViewById(R.id.fabBtn4).setVisibility(View.INVISIBLE);

        }
    }
    //FIN DE LAS CONFIGURACIONES DEL WebViewClient

    //INICIO DE LAS CONFIGURACIONES DEL WebChromeClient
    private class MyWebChromeClient extends WebChromeClient {
        //Declaración de Instancias
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallBack;
        private int mOriginalSystemVisibility;

        //Constructor
        MyWebChromeClient(){
        }

        //Creación de fondo de color default
        @Nullable
        public Bitmap getDefaultVideoPoster(){
            if (mCustomView ==null){
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(),2);
        }

        //Se hace referencia de la instancia de mCustomView y se cierra la vista.
        public void onHideCustomView(){
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemVisibility);
            this.mCustomViewCallBack.onCustomViewHidden();
            this.mCustomViewCallBack = null;
        }

        //Se hace referencia de la instancia de mCustomView y se muestra la vista.
        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallBack){
            if (this.mCustomView != null){
                onHideCustomView();
                return;
            }

            this.mCustomView = paramView;
            this.mOriginalSystemVisibility = getWindow().getDecorView().getWindowSystemUiVisibility();
            this.mCustomViewCallBack = paramCustomViewCallBack;

            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1,-1));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
    //FIN DE LAS CONFIGURACIONES DEL WebChromeClient


    /*CUANDO EL USUARIO PRESIONA EL BOTON DE IR HACIA ATRAS; SI SE PUEDE REALIZAR LA ACCION QUERIENDO
    VISUALIZAR UNA Ó PREVIA EN EL WEBVIEW, ENTONCES SE REALIZARÁ LA ACCIÓN, DE LO CONTRARIO SIMPLEMENTE
    SE SALDRÁ DE LA APLICACIÓN.*/
    @Override
    public void onBackPressed(){
        if (myWebView != null && myWebView.canGoBack()){
            myWebView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    public void onFloatingButtonPressed(View view){
        setVisibility(clicked);
        setAnimation(clicked);
        clicked =!clicked;
    }
    private void setVisibility(Boolean clicked) {
        if (!clicked){
            findViewById(R.id.fabBtn2).setVisibility(View.VISIBLE);
            findViewById(R.id.fabBtn3).setVisibility(View.VISIBLE);
            findViewById(R.id.fabBtn4).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.fabBtn2).setVisibility(View.INVISIBLE);
            findViewById(R.id.fabBtn3).setVisibility(View.INVISIBLE);
            findViewById(R.id.fabBtn4).setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(Boolean clicked) {
        if (!clicked){
            findViewById(R.id.fabBtn1).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_open_animation));
            findViewById(R.id.fabBtn2).startAnimation(AnimationUtils.loadAnimation(this, R.anim.from_bottom_animation));
            findViewById(R.id.fabBtn3).startAnimation(AnimationUtils.loadAnimation(this, R.anim.from_bottom_animation));
            findViewById(R.id.fabBtn4).startAnimation(AnimationUtils.loadAnimation(this, R.anim.from_bottom_animation));
        }else{
            findViewById(R.id.fabBtn1).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_close_animation));
            findViewById(R.id.fabBtn2).startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_bottom_animation));
            findViewById(R.id.fabBtn3).startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_bottom_animation));
            findViewById(R.id.fabBtn4).startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_bottom_animation));
        }
    }

    public void onSettingsFloatingButtonPressed(View view){
        Intent i = new Intent(this, settingsActivity.class);
        startActivity(i);
    }

    public void onCollectionsFloatingButtonPressed(View view){
        Intent i = new Intent(this, collectionsActivity.class);
        startActivity(i);
    }

    public void onAddUrlFloatingButton(View view){
        Intent i = new Intent(this, addActivity.class);
        i.putExtra("url", myWebView.getUrl());
        startActivity(i);
    }

    public void readSettingsFile(String file_name) {
        try {
            File archive = new File(getFilesDir()+"/"+file_name);
            StringBuilder contenido = null;
            JSONObject json;
            String  url;
            boolean zoom_controls, search_bar;

            if (archive.exists()){
                //Lectura del archivo, procesamiento para conversion a texto
                InputStreamReader inputStreamReader = new InputStreamReader(openFileInput(file_name));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String linea = bufferedReader.readLine();
                contenido = new StringBuilder();
                while (linea != null){
                    contenido.append(linea).append("\n");
                    linea = bufferedReader.readLine();
                }
                bufferedReader.close();
                inputStreamReader.close();
                //Conversion del contenido del archivo settings.txt a objeto json
                json = new JSONObject(contenido.toString());

                //Optencion de cada uno de los campos de objeto json
                url = json.get("url").toString();
                zoom_controls = Boolean.parseBoolean(json.get("zoom_controls").toString());
                search_bar = Boolean.parseBoolean(json.get("search_bar").toString());
            }else{
                url = "https://www.google.com/";
                zoom_controls = false;
                search_bar = false;
            }

            myWebView.loadUrl(url);

            settings(zoom_controls, search_bar);

        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void settings(@NonNull Boolean zoom, Boolean searchBar){
        if (zoom){
            myWebView.getSettings().setSupportZoom(true);
            myWebView.getSettings().setBuiltInZoomControls(true);
            myWebView.getSettings().setDisplayZoomControls(true);
        }else{
            myWebView.getSettings().setSupportZoom(false);
            myWebView.getSettings().setBuiltInZoomControls(false);
            myWebView.getSettings().setDisplayZoomControls(false);
        }

        if (searchBar){
            searchViewUrl.setVisibility(View.VISIBLE);
        }else{
            searchViewUrl.setVisibility(View.GONE);
        }
    }
}