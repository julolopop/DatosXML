package com.example.usuario.datosxml;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class XmlActivity extends AppCompatActivity {

    TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xml);

        this.texto = (TextView) findViewById(R.id.txv_texto);


        try {
            texto.setText(analizarNombres());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String analizarNombres() throws XmlPullParserException,
            IOException {
        StringBuilder cadena = new StringBuilder();
        XmlResourceParser xrp = getResources().getXml(R.xml.alumnos);
        int eventType = xrp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                if (xrp.getName().equals("nombre")) {
                    cadena.append("\nNombre : " + xrp.nextText());
                }
                if (xrp.getName().equals("nota")) {

                    cadena.append("\nAsignatura : " + xrp.getAttributeValue(0));
                    cadena.append("\nFecha : " + xrp.getAttributeValue(1));
                    cadena.append("\nNota : " + xrp.nextText());
                }
            }


            if (eventType == XmlPullParser.END_TAG)
                if (xrp.getName().equals("alumno"))
                    cadena.append("\n\n");


            eventType = xrp.next();
        }
        return cadena.toString();
    }
}
