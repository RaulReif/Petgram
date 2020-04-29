package com.example.petgram.Configuracion;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CamposBD {
    public static final String EMAIL = "email";
    public static final String ESTADO = "estado";
    public static final String IMAGEN = "imagen";
    public static final String LOCALIDAD = "localidad";
    public static final String NOMBRE = "nombre";
    public static final String TIPO = "tipo";
    public static final String UID = "uid";
    public static final String VISTO = "visto";
    public static final String CONEXION = "conexion";
    public static final String CONVERSACIONES = "conversaciones";


    public DatabaseReference getMyReference() {
        return FirebaseDatabase.getInstance().getReference("usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
}
