package pe.edu.pucp.rtcompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import pe.edu.pucp.rtcompanion.adapters.ListaTicketsAdapter;
import pe.edu.pucp.rtcompanion.dtos.TicketDTO;
import pe.edu.pucp.rtcompanion.dtos.UserDTO;

public class CuentaUsuarioActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView tvNombreCompleto, tvCorreo, tvTelefono, tvDireccion, tvCiudad, tvPais, tvNombreOrg, tvRol, tvAlias, tvCelular;
    private BlurView progressBar;
    private String server, user, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta_usuario);

        // Se setea la progressbar
        progressBar = findViewById(R.id.pbCuentaUsuario);
        setupBlur();
        progressBar.setVisibility(View.VISIBLE);

        // Se obtienen los datos del usuario
        UserDTO usuario = (UserDTO) getIntent().getExtras().get("usuario");
        configurarNavBar(usuario);

        // Se mapean los campos a rellenar
        tvNombreCompleto = findViewById(R.id.tvNombreCuentaUsuario);
        tvAlias = findViewById(R.id.tvAliasCuentaUsuario);
        tvCorreo = findViewById(R.id.tvCorreoCuentaUsuario);
        tvTelefono = findViewById(R.id.tvTelefonoCuentaUsuario);
        tvCelular = findViewById(R.id.tvCelularCuentaUsuario);
        tvDireccion = findViewById(R.id.tvDireccionCuentaUsuario);
        tvCiudad = findViewById(R.id.tvCiudadCuentaUsuario);
        tvPais = findViewById(R.id.tvPaisCuentaUsuario);
        tvNombreOrg = findViewById(R.id.tvNombreOrgCuentaUsuario);
        tvRol = findViewById(R.id.tvRolOrgCuentaUsuario);

        // Se setean sus valores
        tvNombreCompleto.setText(usuario.getNombreCompleto());
        tvAlias.setText(usuario.getAlias());
        tvCorreo.setText(usuario.getCorreo());
        tvTelefono.setText(usuario.getTelefono().equals("")?"---":usuario.getTelefono());
        tvCelular.setText(usuario.getCelular());
        tvDireccion.setText(usuario.getDireccion());
        tvCiudad.setText(usuario.getCiudad());
        tvPais.setText(usuario.getPais());
        tvNombreOrg.setText(usuario.getOrganizacion());

        obtenerRol(usuario.getIdGrupos());
    }

    private void obtenerRol(String id) {
        progressBar.setVisibility(View.VISIBLE);

        // Se obtienen los datos de conexion
        String fileName = "credenciales";

        try (FileInputStream fileInputStream = openFileInput(fileName);
             FileReader fileReader = new FileReader(fileInputStream.getFD());
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            server = bufferedReader.readLine();
            user = bufferedReader.readLine();
            pwd = bufferedReader.readLine();
        } catch (IOException e) {
            Log.e("cuenta", "No se encontraron las credenciales");
        }
        Log.i("cuenta", String.format("Server: %s\nUser: %s\nPwd: %s",server,user,pwd));

        RequestQueue queue = Volley.newRequestQueue(CuentaUsuarioActivity.this);
        String url = "https://" + server + "/REST/2.0/group/"+id;
        Log.i("cuenta", "URL: "+url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response ->  {
                    Log.i("cuenta", "JSON obtenido:\n"+response);

                    // Se obtiene el nombre del rol
                    String nombreRol = JsonParser.parseString(response).getAsJsonObject().get("Name").toString();
                    Log.i("cuenta", "El nombre del rol es: "+nombreRol);

                    // Se escribe el nombre del rol
                    tvRol.setText(nombreRol.replaceAll("\"",""));
                    progressBar.setVisibility(View.GONE);
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
                            Toast.makeText(CuentaUsuarioActivity.this, "No es posible acceder al servidor", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CuentaUsuarioActivity.this, "Es necesaria una conexión a Internet", Toast.LENGTH_SHORT).show();
                        }

                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(CuentaUsuarioActivity.this, "No se recibió respuesta del servidor", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(CuentaUsuarioActivity.this, "Las credenciales son inválidas", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof ServerError) {
                        //Indicates that the server responded with a error response
                        Toast.makeText(CuentaUsuarioActivity.this, "Hay un problema con el servidor", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof NetworkError) {
                        //Indicates that there was network error while performing the request
                        Toast.makeText(CuentaUsuarioActivity.this, "Hubo un error de conexión", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof ParseError) {
                        // Indicates that the server response could not be parsed
                        Toast.makeText(CuentaUsuarioActivity.this, "No se pudo procesar a respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                    tvRol.setText("");
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

    public void logout(View view){
        new MaterialAlertDialogBuilder(view.getContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estas seguro de querer cerrar tu sesión actual?")
                .setNegativeButton("Cancelar",((dialogInterface, i) -> {
                    dialogInterface.cancel();
                })).setPositiveButton("Cerrar Sesión", ((dialogInterface, i) -> {
                    deleteFile("credenciales");
                    startActivity(new Intent(CuentaUsuarioActivity.this, LoginActivity.class));
                    dialogInterface.dismiss();
                    finish();
                })).show();
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

    public void configurarNavBar(UserDTO usuario){

        bottomNavigationView = findViewById(R.id.nvCuentaUsuario);
        bottomNavigationView.setSelectedItemId(R.id.navigation_info_cuenta);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (R.id.navigation_tickets == item.getItemId()){
                Intent intent = new Intent(getApplicationContext(), ListaTicketsActivity.class)
                        .putExtra("usuario",usuario)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            }
            else return R.id.navigation_info_cuenta == item.getItemId();
        });
    }

}