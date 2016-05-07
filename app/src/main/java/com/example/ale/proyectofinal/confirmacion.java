package com.example.ale.proyectofinal;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class confirmacion extends ActionBarActivity {

    public final static String apiURL = "http://10.0.2.2:8080/api/candidatos"; // API url
    private String candId = null; // id del candidato
    private String token = null; // token de autenticacion
    private JSONObject responseJson = null; // respuesta del servidor en formato JSON
    public Button confirmar = null; // boton de confirmacion
    public Button regresar = null; // boton de regresar

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion);

        confirmar = (Button) findViewById(R.id.btn_confirmar);
        regresar = (Button) findViewById(R.id.btn_regresar);

        String nombreCand = null;
        String partidoCand = null;

        Bundle extras = getIntent().getExtras();

        candId = extras.getString("EXTRA_CAND_ID");
        nombreCand = extras.getString("EXTRA_CAND_NOMBRE");
        partidoCand = extras.getString("EXTRA_CAND_PARTIDO");
        token = extras.getString("EXTRA_TOKEN");

        TextView txtvConfCand = (TextView) findViewById(R.id.confirmar_nombreCand);
        TextView txtvConfPartido = (TextView) findViewById(R.id.confirmar_partidoCand);

        txtvConfCand.setText(nombreCand);
        txtvConfPartido.setText(partidoCand);
    }

    public void attemptConfirm(View view) {
        TextView txtvConfirmarTest = (TextView) findViewById(R.id.confirmarTest);
        txtvConfirmarTest.setText("Procesando ...");
        txtvConfirmarTest.setTextColor(Color.BLUE);

        confirmar.setEnabled(false);
        regresar.setEnabled(false);

        new CallAPI().execute();
    }

    public void goBack(View view) {
        confirmar.setEnabled(false);
        regresar.setEnabled(false);

        setResult(RESULT_CANCELED);
        finish();
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
            String urlParameters = null;

            // HTTP get
            try {
                url = new URL(apiURL + "/" + candId);
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

            try {
                responseJson = new JSONObject(response.toString());
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }

            //HTTP put
            try {

                url = new URL(apiURL + "/" + candId);
                urlParameters = "cantVotos=" + URLEncoder.encode(Integer.toString(responseJson.getInt("cantVotos") + 1), "UTF-8");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("x-access-token", token);

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
                rd.close();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return response.toString();
        }

        protected void onPostExecute(String result) {
            setResult(RESULT_OK);
            finish();
        }
    }
}