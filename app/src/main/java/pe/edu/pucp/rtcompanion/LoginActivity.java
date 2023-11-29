package pe.edu.pucp.rtcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import pe.edu.pucp.rtcompanion.dtos.UserDTO;

public class LoginActivity extends AppCompatActivity {

    private View progressBar;
    private TextInputLayout inputServer, inputUser, inputPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.pbLogin);
        progressBar.setVisibility(View.VISIBLE);

        inputServer = findViewById(R.id.etLoginIP);
        inputUser = findViewById(R.id.etLoginCorreo);
        inputPwd = findViewById(R.id.etLoginPswd);
        inputPwd.setErrorIconDrawable(null);

        // Se intenta el logueo automático
        verifyCred();

        // Si falla se muestra
        progressBar.setVisibility(View.GONE);
    }

    public void submit (View view){

        progressBar.setVisibility(View.VISIBLE);

        String server = inputServer.getEditText().getText().toString().trim();
        String user = inputUser.getEditText().getText().toString().trim();
        String pwd = inputPwd.getEditText().getText().toString().trim();

        if (datosValidos(server,user,pwd)){
            login(server,user,pwd,true);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void verifyCred(){
        String fileName = "credenciales";
        String server="", user="", pwd="";

        try (FileInputStream fileInputStream = openFileInput(fileName);
             FileReader fileReader = new FileReader(fileInputStream.getFD());
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            server = bufferedReader.readLine();
            user = bufferedReader.readLine();
            pwd = bufferedReader.readLine();

            inputServer.getEditText().setText(server);
            inputUser.getEditText().setText(user);
            inputPwd.getEditText().setText(pwd);

            login(server,user,pwd,false);

        } catch (IOException e) {
            Log.i("logueo", "El archivo de credenciales aún no existe");
        }
    }

    // Se realiza el logueo
    private void login(String server, String user, String pwd, boolean saveNew) {

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        Log.i("logueo", String.format("Server: %s\nUser: %s\nPwd: %s",server,user,pwd));

        String url = "https://" + server + "/REST/2.0/user/" + user;
        Log.i("logueo", "URL: "+url);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response ->  {
                    Log.i("logueo", "JSON antes:\n"+response);
                    if (saveNew) {
                        // Se guarda el archivo de credenciales en memoria interna
                        String archivo = "credenciales";
                        String contenido = server + "\n" + user + "\n" + pwd;

                        //Se utiliza la clase FileOutputStream para poder almacenar en Android
                        try (FileOutputStream fileOutputStream = this.openFileOutput(archivo, Context.MODE_PRIVATE);
                             FileWriter fileWriter = new FileWriter(fileOutputStream.getFD())) {
                            fileWriter.write(contenido);
                        } catch (IOException e) {
                            Log.d("logueo", e.getMessage());
                        }
                    }

                    // Se guarda el JSON en una clase usuario
                    Gson gson = new Gson();
                    UserDTO usuario = gson.fromJson(response,UserDTO.class);
                    Log.i("logueo", "Objeto generado: " + usuario.getCorreo());
                    /*
                    JsonObject o = JsonParser.parseString(response).getAsJsonObject();
                    o.remove("LastUpdatedBy");
                    o.remove("_hyperlinks");
                    o.remove("Creator");

                    Log.i("logueo", "JSON despues:\n"+o);
                    UserDTO usuario = gson.fromJson(o,UserDTO.class);
                    Log.i("logueo", "Objeto generado:" + gson.toJson(usuario)
                     */

                    Intent intent = new Intent(getApplicationContext(),ListaTicketsActivity.class);
                    intent.putExtra("usuario",usuario).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                },
                error -> {
                    Log.e("logueo", error.getMessage());
                    Toast.makeText(LoginActivity.this, "Hubo un error de conexión", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                String credentials = user +":" + pwd;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(request);
    }

    private boolean datosValidos (String server, String correo, String pwd){
        progressBar.setVisibility(View.VISIBLE);
        boolean valido = true;

        // Se quitan los mensajes previos
        inputServer.setError(null);
        inputUser.setError(null);
        inputPwd.setError(null);

        // Se valida que la URL sea válida
        try {
            new URL("https://" + server).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            inputServer.setError("La URL ingresada no es válida");
            valido = false;
        }
        return valido;
    }
}