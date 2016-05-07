package com.example.ale.proyectofinal;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class VotacionCompleta extends ActionBarActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votacion_completa);
    }

    public void goToLogin(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
