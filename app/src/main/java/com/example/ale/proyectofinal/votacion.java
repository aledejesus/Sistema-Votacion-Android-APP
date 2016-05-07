package com.example.ale.proyectofinal;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class votacion extends ActionBarActivity {

    public final static String apiURL = "http://10.0.2.2:8080/api/candidatos"; // API url
    private static String token = ""; // token de autenticacion
    private static Bundle extras = null; // parametros enviados por la actividad anterior
    public static TextView title = null; // titulo del cuerpo de la actividad
    private static JSONArray responseJson = null; // respuesta del servidor en formato JSON
    private static TextView[] nombresCandArray = new TextView[4]; // arreglo de nombres de los candidatos
    private static TextView[] partidosCandArray = new TextView[4]; // arreglo de partidos de los candidatos

    public static Button cand1 = null; // boton del candidato 1
    public static Button cand2 = null; // boton del candidato 2
    public static Button cand3 = null; // boton del candidato 3
    public static Button cand4 = null; // boton del candidato 4

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votacion);

        extras = getIntent().getExtras();
        token = extras.getString("EXTRA_TOKEN");
        title = (TextView) findViewById(R.id.elija);
        cand1 = (Button) findViewById(R.id.cand1);
        cand2 = (Button) findViewById(R.id.cand2);
        cand3 = (Button) findViewById(R.id.cand3);
        cand4 = (Button) findViewById(R.id.cand4);

        title.setText("Cargando candidatos ...");

        new CallAPI().execute();
    }

    public void attemptVoting(View view) {
        String candId = null;
        Boolean error = false;
        String nombreCand = null;
        String partidoCand = null;
        Integer opt = 0;

        switch (view.getId()) {
            case R.id.cand1:
                opt = 0;
                error = false;
                break;
            case R.id.cand2:
                opt = 1;
                error = false;
                break;
            case R.id.cand3:
                opt = 2;
                error = false;
                break;
            case R.id.cand4:
                opt = 3;
                error = false;
                break;
            default:
                opt = -1;
                error = true;
        }

        if (!error) {
            try {
                candId = responseJson.getJSONObject(opt).getString("_id");
                nombreCand = nombresCandArray[opt].getText().toString();
                partidoCand = partidosCandArray[opt].getText().toString();

                cand1.setEnabled(false);
                cand2.setEnabled(false);
                cand3.setEnabled(false);
                cand4.setEnabled(false);

                Intent intentConfirmar = new Intent(this, confirmacion.class);
                intentConfirmar.putExtra("EXTRA_CAND_ID", candId);
                intentConfirmar.putExtra("EXTRA_CAND_NOMBRE", nombreCand);
                intentConfirmar.putExtra("EXTRA_CAND_PARTIDO", partidoCand);
                intentConfirmar.putExtra("EXTRA_TOKEN", token);
                startActivityForResult(intentConfirmar, 2);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            title.setText("ERROR");
        }
    }

    private class CallAPI extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            DataOutputStream wr = null;
            InputStream is = null;
            BufferedReader rd = null;
            String line = null;
            StringBuilder response = null;

            // HTTP get
            try {
                url = new URL(apiURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("x-access-token", token);

                is = urlConnection.getInputStream();
                rd = new BufferedReader(new InputStreamReader(is));
                response = new StringBuilder();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
                rd.close();

            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return response.toString();
        }

        protected void onPostExecute(String result) {
            TextView nombreCand1 = (TextView) findViewById(R.id.nombreCand1);
            TextView nombreCand2 = (TextView) findViewById(R.id.nombreCand2);
            TextView nombreCand3 = (TextView) findViewById(R.id.nombreCand3);
            TextView nombreCand4 = (TextView) findViewById(R.id.nombreCand4);

            nombresCandArray[0] = nombreCand1;
            nombresCandArray[1] = nombreCand2;
            nombresCandArray[2] = nombreCand3;
            nombresCandArray[3] = nombreCand4;

            TextView partidoCand1 = (TextView) findViewById(R.id.partidoCand1);
            TextView partidoCand2 = (TextView) findViewById(R.id.partidoCand2);
            TextView partidoCand3 = (TextView) findViewById(R.id.partidoCand3);
            TextView partidoCand4 = (TextView) findViewById(R.id.partidoCand4);

            partidosCandArray[0] = partidoCand1;
            partidosCandArray[1] = partidoCand2;
            partidosCandArray[2] = partidoCand3;
            partidosCandArray[3] = partidoCand4;

            try {
                responseJson = new JSONArray(result);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }

            for (int i = 0; i < nombresCandArray.length; i++) {
                try {
                    String nombre = responseJson.getJSONObject(i).getString("name");
                    String partido = responseJson.getJSONObject(i).getString("partido");

                    nombresCandArray[i].setText(nombre);
                    partidosCandArray[i].setText(partido);
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            title.setText("Elija su candidato: ");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
            else {
                cand1.setEnabled(true);
                cand2.setEnabled(true);
                cand3.setEnabled(true);
                cand4.setEnabled(true);
            }
        }
    }
}
