package com.example.ale.proyectofinal;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class login extends ActionBarActivity {

    public EditText etCedula; // cedula input
    public EditText etPin; // pin input
    public Button btnVotar; // boton de inicio de sesion
    public TextView res; // TextView del resultado
    public final static String apiURL = "http://10.0.2.2:8080/api/authenticate"; // API url
    private static Boolean success = false; // indicador de exito de autenticacion

    //CEDULA = "22312345614"
    //PASSWORD = "abc123"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCedula = (EditText) findViewById(R.id.etCedula);
        etPin = (EditText) findViewById(R.id.etPin);
        btnVotar = (Button) findViewById(R.id.btnVotar);
        res = (TextView) findViewById(R.id.txtTest);
    }

    public void attemptLogin(View view) {
        Editable cedulaText = null;
        Editable pinText = null;
        String cedula = null;
        String pin = null;

        cedulaText = (Editable) etCedula.getText();
        cedula = (String) cedulaText.toString();
        pinText = (Editable) etPin.getText();
        pin = (String) pinText.toString();

        btnVotar.setEnabled(false);
        res.setTextColor(Color.BLUE);
        res.setText("Procesando ...");

        new CallAPI().execute(cedula, pin);
    }

    private class CallAPI extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {
            String cedula = params[0];
            String pin = params[1];

            URL url = null;
            String urlParameters = null;
            HttpURLConnection urlConnection = null;
            DataOutputStream wr = null;
            InputStream is = null;
            BufferedReader rd = null;
            String line = null;
            StringBuilder response = null;
            JSONObject responseJson = null;
            String token = null;

            // HTTP post
            try {
                url = new URL(apiURL);
                urlParameters = "username=" + URLEncoder.encode(cedula, "UTF-8") +
                        "&password=" + URLEncoder.encode(pin, "UTF-8");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                is = urlConnection.getInputStream();
                rd = new BufferedReader(new InputStreamReader(is));
                response = new StringBuilder();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
                responseJson = new JSONObject(response.toString());
                success = responseJson.getBoolean("success");
                rd.close();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }

            if (success) {
                try {
                    token = responseJson.getString("token");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            return token;
        }

        protected void onPostExecute(String result) {
            if (success) {
                Intent intentVotar = new Intent(getApplicationContext(), votacion.class);
                intentVotar.putExtra("EXTRA_TOKEN", result);
                startActivityForResult(intentVotar, 1);
                success = false;
                res.setText("");
            }
            else {
                res.setTextColor(Color.RED);
                res.setText("Cedula o PIN incorrecto");
                btnVotar.setEnabled(true);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Intent intentFin = new Intent(getApplicationContext(), VotacionCompleta.class);
                startActivityForResult(intentFin,3);
            }

            else {
                btnVotar.setEnabled(true);
            }
        }

        if(requestCode == 3) {
            if (resultCode == RESULT_CANCELED) {
                etCedula.setText("");
                etPin.setText("");
            }
            btnVotar.setEnabled(true);
        }
    }
}