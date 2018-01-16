package com.example.usuario.datosxml;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.usuario.datosxml.pojo.Noticias;
import com.example.usuario.datosxml.pojo.RestClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CreacionXMLActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String RSS = "http://www.europapress.es/rss/rss.aspx";
    //public static final String RSS = "http://10.0.2.2/feed/alejandro.xml";
    public static final String TEMPORAL = "alejandro.xml";
    public static final String FICHERO_XML = "resultado.xml";
    Button boton;
    Button boton2;
    static ArrayList<Noticias> noticias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creacion_xml);
        boton = (Button) findViewById(R.id.boton);
        boton2 = (Button) findViewById(R.id.boton2);
        boton.setOnClickListener(this);
        boton2.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v == boton)
            descarga(RSS, TEMPORAL);
        if (v == boton2)
            AbrirExplorador();
    }

    private void AbrirExplorador() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");

        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, 1);
    }


    public void crearXML(ArrayList<Noticias> noticias, String fichero) throws IOException {
        FileOutputStream fout;
        fout = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fichero));
        Toast.makeText(CreacionXMLActivity.this,Environment.getExternalStorageDirectory().getAbsolutePath()+fichero,Toast.LENGTH_LONG);
        XmlSerializer serializer = Xml.newSerializer();


        serializer.setOutput(fout, "UTF-8");
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true); //poner tabulación
        serializer.startTag(null, "titulares");
        for (int i = 0; i < noticias.size(); i++) {
            serializer.startTag(null, "item");

            serializer.startTag(null, "title");
            serializer.text(noticias.get(i).getTitle());
            serializer.endTag(null, "title");

            serializer.startTag(null, "link");
            serializer.text(noticias.get(i).getLink());
            serializer.endTag(null, "link");

            serializer.startTag(null, "description");
            serializer.text(noticias.get(i).getDescription());
            serializer.endTag(null, "description");

            serializer.startTag(null, "pubDate");
            serializer.text(noticias.get(i).getPubDate());
            serializer.endTag(null, "pubDate");

            serializer.endTag(null, "item");

        }
        serializer.endTag(null, "titulares");
        serializer.endDocument();
        serializer.flush();
        fout.close();
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
                Toast.makeText(CreacionXMLActivity.this, "Error: " + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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


                try {
                    crearXML(noticias,FICHERO_XML);
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