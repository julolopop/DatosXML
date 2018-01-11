package com.example.usuario.datosxml;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.usuario.datosxml.pojo.Noticias;
import com.example.usuario.datosxml.pojo.RestClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class NewsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {


    public static final String CANAL = "http://www.europapress.es/rss/rss.aspx?ch=279";
    //public static final String CANAL = "http://192.168.1.200/feed/europapress.xml";
    public static final String TEMPORAL = "europapress.xml";
    static ArrayList<Noticias> noticias;
    ListView lista;
    ArrayAdapter<Noticias> adapter;
    FloatingActionButton fab;

    @Override
    public void onClick(View v) {
        if (v == fab)
            descarga(CANAL, TEMPORAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        lista = (ListView) findViewById(android.R.id.list);
        lista.setOnItemClickListener(this);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this);
    }


    public static ArrayList<Noticias> analizarNoticias(File file) throws XmlPullParserException, IOException {
        int eventType;
        noticias = new ArrayList<>();
        boolean title = false;
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(new FileReader(file));
        eventType = xpp.getEventType();

        String titulo="";
        String link="";
        String descripcion="";
        String pub="";
        boolean btitulo = false;
        boolean blink=false;
        boolean bdescripcion=false;
        boolean bpub=false;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("item")) {
                        title = true;
                    }
                    if (title) {
                        if (xpp.getName().equals("title")) {
                            btitulo=true;
                        }
                        if (xpp.getName().equals("link")) {
                            blink=true;
                        }
                        if (xpp.getName().equals("description")) {
                            bdescripcion=true;
                        }
                        if (xpp.getName().equals("pubDate")) {
                            bpub=true;
                        }
                    }

                    break;
                case XmlPullParser.TEXT:
                    if (btitulo) {
                        btitulo=false;
                        titulo = xpp.getText();
                    }
                    if (blink) {
                        blink=false;
                        link=xpp.getText();
                    }
                    if (bdescripcion) {
                        bdescripcion=false;
                        descripcion=xpp.getText();
                    }
                    if (bpub) {
                        bpub=false;
                        pub=xpp.getText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (xpp.getName().equals("item")) {
                        noticias.add(new Noticias(titulo,link,descripcion,pub));
                        title = false;
                    }
                    break;
            }
            eventType = xpp.next();
        }
        //devolver el array de noticias
        return noticias;
    }


    private void descarga(String canal, String temporal) {
        final ProgressDialog progreso = new ProgressDialog(this);
        File miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), temporal);
        RestClient.get(canal, new FileAsyncHttpResponseHandler(miFichero) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                progreso.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                progreso.dismiss();

                try {
                    noticias = analizarNoticias(file);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mostrar();
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

    private void mostrar() {
        if (noticias != null) {
            if (adapter == null) {
                adapter = new ArrayAdapter<Noticias>(this, android.R.layout.simple_list_item_1, noticias);
                lista.setAdapter(adapter);
            } else {
                adapter.clear();
                adapter.addAll(noticias);
            }
        } else
            Toast.makeText(getApplicationContext(), "Error al crear la lista", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Uri uri = Uri.parse((String) noticias.get(position).getLink());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(getApplicationContext(), "No hay un navegador", Toast.LENGTH_SHORT).show();
    }

}
