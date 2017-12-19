package com.example.usuario.datosxml;

import android.app.ProgressDialog;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.datosxml.pojo.RestClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class RSSActivity extends AppCompatActivity {
    TextView texto;

    public static final String RSS = "http://www.alejandrosuarez.es/feed/";
    //public static final String RSS = "http://10.0.2.2/feed/alejandro.xml";
    public static final String TEMPORAL = "alejandro.xml";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);

        this.texto = (TextView) findViewById(R.id.txv_texto);

        descarga(RSS, TEMPORAL);
    }


    public static String analizarRSS(File file) throws NullPointerException, XmlPullParserException,
            IOException {
        StringBuilder builder = new StringBuilder();
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(new FileReader(file));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG){
                if(xpp.getName().equals("title")){
                    builder.append("\nTitulo : "+xpp.nextText());
                }
                if(xpp.getName().equals("link")){
                    builder.append("\nTitulo : "+xpp.nextText());
                }
            }

            if(eventType == XmlPullParser.END_TAG){
                if(xpp.getName().equals("channel")){
                    builder.append("\n\n");
                }
            }


            eventType = xpp.next();
        }
        return builder.toString();
    }


    private void descarga(String rss, String temporal) {
        final ProgressDialog progreso = new ProgressDialog(this);
        File miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), temporal);
        RestClient.get(rss, new FileAsyncHttpResponseHandler(miFichero) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                progreso.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                progreso.dismiss();

                Toast.makeText(RSSActivity.this,"Fichero descargado correctamente",Toast.LENGTH_LONG).show();
                try {
                    texto.setText(analizarRSS(file));
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Conectando . . .");
                progreso.setCancelable(false);
                progreso.show();
            }
        });
    }
}

