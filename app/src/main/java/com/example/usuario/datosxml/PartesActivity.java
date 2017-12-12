package com.example.usuario.datosxml;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

public class PartesActivity extends AppCompatActivity {

    public static final String TEXTO = "<texto><uno>Hello World!</uno><dos>Goodbye</dos></texto>";

    TextView informacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partes);

        this.informacion = (TextView)findViewById(R.id.txvInformacion);

            try {
                informacion.setText(analizar(TEXTO));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public static String analizar(String texto) throws XmlPullParserException, IOException {
        StringBuilder cadena = new StringBuilder();
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(new StringReader(texto));
        int eventType = xpp.getEventType();
        cadena.append("\nInicio . . . ");
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
                cadena.append("\nStart document");
            } else if(eventType == XmlPullParser.START_TAG) {
                cadena.append("\nStart tag "+xpp.getName());
            } else if(eventType == XmlPullParser.END_TAG) {
                cadena.append("\nEnd tag "+xpp.getName());
            } else if(eventType == XmlPullParser.TEXT) {
                cadena.append("\nText "+xpp.getText());
            }
            eventType = xpp.next();
        }
        //System.out.println("End document");
        cadena.append("End document" + "\n" + "Fin");
        return cadena.toString();
    }
}

