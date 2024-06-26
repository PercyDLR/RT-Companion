package pe.edu.pucp.rtcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
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

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import pe.edu.pucp.rtcompanion.dtos.UserDTO;

public class LoginActivity extends AppCompatActivity {

    private BlurView progressBar;
    private TextInputLayout inputServer, inputUser, inputPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.pbLogin);
        setupBlur();
        progressBar.setVisibility(View.VISIBLE);

        inputServer = findViewById(R.id.etLoginIP);
        inputUser = findViewById(R.id.etLoginCorreo);
        inputPwd = findViewById(R.id.etLoginPswd);
        inputPwd.setErrorIconDrawable(null);

        // Se intenta el logueo automático
        verifyCred();
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

        progressBar.setVisibility(View.VISIBLE);

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

            if (datosValidos(server,user,pwd)){
                login(server,user,pwd,false);
            } else {
                progressBar.setVisibility(View.GONE);
            }

        } catch (IOException e) {
            progressBar.setVisibility(View.GONE);
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
                    Log.i("logueo", "Objeto generado: " + usuario.getEstaDeshabilitado());

                    if (usuario.getEstaDeshabilitado() == 0) {
                        Intent intent = new Intent(getApplicationContext(),ListaTicketsActivity.class);
                        intent.putExtra("usuario",usuario).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "El usuario con el que tratas de ingresar está deshabilitado en el sistema. Comunícate con tu administrador para resolver el problema.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error instanceof NoConnectionError) {
                        //This indicates that the reuest has either time out or there is no connection
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = null;
                        if (cm != null) {
                            activeNetwork = cm.getActiveNetworkInfo();
                        }
                        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
                            inputServer.setError("Revisar la dirección del servidor");
                            Toast.makeText(LoginActivity.this, "No es posible acceder al servidor", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Es necesaria una conexión a Internet", Toast.LENGTH_SHORT).show();
                        }

                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(LoginActivity.this, "No se recibió respuesta del servidor", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(LoginActivity.this, "Las credenciales son inválidas", Toast.LENGTH_SHORT).show();
                        inputUser.setError("El usuario o la contraseña son inválidos");
                        inputPwd.setError("El usuario o la contraseña son inválidos");

                    } else if (error instanceof ServerError) {
                        //Indicates that the server responded with a error response
                        Toast.makeText(LoginActivity.this, "Hay un problema con el servidor", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof NetworkError) {
                        //Indicates that there was network error while performing the request
                        Toast.makeText(LoginActivity.this, "Hubo un error de conexión", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof ParseError) {
                        // Indicates that the server response could not be parsed
                        Toast.makeText(LoginActivity.this, "No se pudo procesar a respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
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

    private boolean datosValidos (String server, String usuario, String pwd){
        progressBar.setVisibility(View.VISIBLE);
        boolean valido = true;

        // Se quitan los mensajes previos
        inputServer.setError(null);
        inputUser.setError(null);
        inputPwd.setError(null);

        // Se valida que los campos se hayan llenado
        if (usuario.equals("")) {
            inputUser.setError("El usuario no puede estar vacío");
            valido = false;
        }
        if (pwd.equals("")){
            inputPwd.setError("La contraseña no puede estar vacía");
            valido = false;
        }

        // Se valida el usuario ingresado
        if (usuario.equals("root")) {
            inputUser.setError("Usuario inválido");
            Toast.makeText(LoginActivity.this, "No puede ingresar con el usuario root. Intente con otro.", Toast.LENGTH_SHORT).show();
            valido = false;
        }

        // Se valida que la URL sea válida
        if (!Patterns.WEB_URL.matcher("https://" + server).matches()) {
            inputServer.setError("La URL ingresada no es válida");
            valido = false;
        }
        return valido;
    }

    private void setupBlur(){
        View decorView = getWindow().getDecorView();
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = decorView.findViewById(android.R.id.content);

        // Optional:
        // Set drawable to draw in the beginning of each blurred frame.
        // Can be used in case your layout has a lot of transparent space and your content
        // gets a too low alpha value after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        progressBar.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(15f);
    }
}