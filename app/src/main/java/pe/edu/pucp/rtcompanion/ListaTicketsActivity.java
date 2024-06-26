package pe.edu.pucp.rtcompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import pe.edu.pucp.rtcompanion.adapters.ListaTicketsAdapter;
import pe.edu.pucp.rtcompanion.dtos.TicketDTO;
import pe.edu.pucp.rtcompanion.dtos.UserDTO;

public class ListaTicketsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextInputLayout buscadorEspacios;
    private UserDTO usuario;
    private View progressBar;
    private String server, user, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tickets);

        setTitle("Inicio");

        // Se obtienen los datos del usuario
        usuario = (UserDTO) getIntent().getExtras().get("usuario");

        progressBar = findViewById(R.id.pbListaTickets);

        // Setea los valores iniciales de la lista en la pantalla
        buscadorEspacios = findViewById(R.id.etBusquedaEspaciosUsuario);
        listarTickets("");

        // Realiza la busqueda si se presiona el botón del teclado
        buscadorEspacios.getEditText().setOnEditorActionListener(((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                listarTickets(textView.getText().toString());
                return true;
            }
            return false;
        }));
        configurarNavBar();
    }

    private void listarTickets (String busqueda) {

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
            Log.e("tickets", "No se encontraron las credenciales");
        }
        Log.i("tickets", String.format("Server: %s\nUser: %s\nPwd: %s",server,user,pwd));

        // Se crea el request de Volley
        RequestQueue queue = Volley.newRequestQueue(ListaTicketsActivity.this);
        String url = "https://" + server + "/REST/2.0/tickets?query=((Status != 'resolved' AND Status != 'stalled' AND Status != 'rejected') AND Subject LIKE '"+busqueda+"')&fields=Owner,Status,Created,Subject,Queue,CustomFields,Requestor,Cc,AdminCc,CustomRoles,Priority,Due";
        Log.i("tickets", "URL: "+url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response ->  {
                    Log.i("tickets", "JSON antes:\n"+response);

                    // Se filtra sol la lista de tickets
                    String o = JsonParser.parseString(response).getAsJsonObject().get("items").toString();
                    Log.i("tickets", "JSON despues:\n"+o);

                    // Se guarda el JSON en una clase tickets
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
                    Type type = new TypeToken<ArrayList<TicketDTO>>(){}.getType();
                    ArrayList<TicketDTO> listaTickets = gson.fromJson(o, type);

                    // Se definen los elementos del adapter
                    ListaTicketsAdapter adapter = new ListaTicketsAdapter();
                    adapter.setListaTickets(listaTickets);
                    adapter.setUsuario(usuario);
                    adapter.setContext(ListaTicketsActivity.this);

                    // Se desaparece la barra de carga
                    progressBar.setVisibility(View.GONE);

                    // Se vincula con el Recycler View
                    RecyclerView recyclerView = findViewById(R.id.rvListaTickets);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
                            Toast.makeText(ListaTicketsActivity.this, "No es posible acceder al servidor", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ListaTicketsActivity.this, "Es necesaria una conexión a Internet", Toast.LENGTH_SHORT).show();
                        }

                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(ListaTicketsActivity.this, "No se recibió respuesta del servidor", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(ListaTicketsActivity.this, "Las credenciales son inválidas", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof ServerError) {
                        //Indicates that the server responded with a error response
                        Toast.makeText(ListaTicketsActivity.this, "Hay un problema con el servidor", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof NetworkError) {
                        //Indicates that there was network error while performing the request
                        Toast.makeText(ListaTicketsActivity.this, "Hubo un error de conexión", Toast.LENGTH_SHORT).show();

                    } else if (error instanceof ParseError) {
                        // Indicates that the server response could not be parsed
                        Toast.makeText(ListaTicketsActivity.this, "No se pudo procesar a respuesta del servidor", Toast.LENGTH_SHORT).show();
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


    public void configurarNavBar(){
        bottomNavigationView = findViewById(R.id.nvListaTickets);
        bottomNavigationView.setSelectedItemId(R.id.navigation_tickets);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (R.id.navigation_tickets == item.getItemId()){
                return true;
            }
            else if (R.id.navigation_info_cuenta == item.getItemId()){
                Intent intent = new Intent(getApplicationContext(), CuentaUsuarioActivity.class)
                        .putExtra("usuario",usuario)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            }
            return false;
        });
    }
}