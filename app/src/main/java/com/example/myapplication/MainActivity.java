package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    String url="https://www.google.com/";
    SwipeRefreshLayout mySwipeRefreshLayout;
    WebView myWebView;
    Boolean clicked = false;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = findViewById(R.id.webView);
        
        WebSettings webSettings= myWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setAllowFileAccess(true);

        webSettings.setSupportZoom(true);
        //webSettings.setBuiltInZoomControls(true);
        //webSettings.setDisplayZoomControls(true);

        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


        myWebView.setVerticalScrollBarEnabled(false);
        //myWebView.addJavascriptInterface(new myJavascriptInterface(), "Interface");

        //Creación de los clientes del WebView
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.setWebChromeClient(new MyWebChromeClient());

        myWebView.loadUrl(url);

        /*
        Cuando el usuario intenta subir entanto en el tope de la pagina, es decir, realiza la accion
        de querer recargar la pagina, entonces se ejecutará este codigo correspondiente
        */
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            /*
            En esta parte del código especificamos que view mostrar mientras se carga, en este caso
            se muestra el View de cuando carga por primera vez nuestra app, pero puedes seleccionar otra
            View que por ejemplo contenga un linerlayout con una imagen o gif de carga o algo por el estilo,
            o simplemene no mostrar nada mientras se recarga la página.
            */
            findViewById(R.id.loaderWebView).setVisibility(View.VISIBLE);
            findViewById(R.id.fabBtn1).setVisibility(View.INVISIBLE);

            myWebView.reload();
            mySwipeRefreshLayout.setRefreshing(false);
        });
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
            findViewById(R.id.webView).setVisibility(View.VISIBLE);

            //injectJS(view);
        }

        /*Inyección de codigo javascript para realizar la captura de cuando un usuario hace click por
        private void injectJS(WebView webview) {
            try {
                webview.loadUrl("javascript:(" +
                        "document.querySelectorAll(\".click\")" +
                        ".forEach(el => {" +
                        "   el.addEventListener(\"click\", e => {" +
                        "       const id = e.target.getAttribute(\"id\");" +
                        "       console.log(\"Se ha clickeado el id \"+id);" +
                        "   });" +
                        "});"
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
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
            return BitmapFactory.decodeResource(getApplicationContext().getResources(),213083757);
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
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
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
        }else{
            findViewById(R.id.fabBtn2).setVisibility(View.INVISIBLE);
            findViewById(R.id.fabBtn3).setVisibility(View.INVISIBLE);
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

    /*
    public class myJavascriptInterface {
        @JavascriptInterface
        public void logHtml() {
            Log.d("=====>", "Java from Android");
        }
    }
    */
}