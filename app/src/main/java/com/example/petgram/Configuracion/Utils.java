package com.example.petgram.Configuracion;


import android.text.format.DateFormat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class Utils {

    public static DatabaseReference getMyReference() {
        return FirebaseDatabase.getInstance().getReference("usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public static DatabaseReference getUserReference(String uid) {
        return FirebaseDatabase.getInstance().getReference("usuarios").child(uid);
    }

    public static String getStringFecha(long timestamp) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        return DateFormat.format("dd/MM HH:mm", calendar).toString();
        //fecha = fecha.substring(0, 5) + " " + fecha.substring(5);
    }


}
