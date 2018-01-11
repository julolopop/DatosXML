package com.example.usuario.datosxml.pojo;

/**
 * Created by usuario on 11/01/18.
 */

public class FailureEvent {
    public final String Message;
    public final int Codigo;

    public FailureEvent(String message, int codigo) {
        Message = message;
        Codigo = codigo;
    }
}
