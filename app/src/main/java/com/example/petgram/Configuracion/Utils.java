package com.example.petgram.Configuracion;

import android.provider.ContactsContract;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Utils {

    public static DatabaseReference getMyReference() {
        return FirebaseDatabase.getInstance().getReference("usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public static DatabaseReference getUserReference(String uid) {
        return FirebaseDatabase.getInstance().getReference("usuarios").child(uid);
    }



}
