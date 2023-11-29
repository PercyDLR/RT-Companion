package pe.edu.pucp.rtcompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pe.edu.pucp.rtcompanion.dtos.TicketDTO;
import pe.edu.pucp.rtcompanion.dtos.UserDTO;

public class ListaTicketsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextInputLayout buscadorEspacios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tickets);
        // Se obtienen los datos del usuario
        UserDTO usuario = (UserDTO) getIntent().getExtras().get("usuario");
        configurarNavBar(usuario);

        // Se obtienen los datos de conexion
        String fileName = "credenciales";
        String server="", user="", pwd="";

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

        // Setea los valores iniciales de la lista en la pantalla
        buscadorEspacios = findViewById(R.id.etBusquedaEspaciosUsuario);
        listarTickets(server,user,pwd);

        // Realiza la busqueda si se presiona el botón del teclado
        buscadorEspacios.getEditText().setOnEditorActionListener(((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                listarEspacios(buscadorEspacios.getEditText().getText().toString().trim());
                return true;
            }
            return false;
        }));
    }

    public void listarEspacios(String busqueda){
        /*ListaEspaciosAdapter adapter = new ListaEspaciosAdapter();
        ArrayList<EspacioDTO> listaEspacios = new ArrayList<>();

        adapter.setListaEspacios(listaEspacios);
        adapter.setContext(this);
        adapter.setFuncion("usuario");

        ref.orderByChild("nombre").startAt(busqueda).endAt(busqueda+"\uf8ff")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        EspacioDTO espacio = snapshot.getValue(EspacioDTO.class);
                        espacio.setKey(snapshot.getKey());

                        if(espacio.isActivo()){
                            listaEspacios.add(espacio);
                            Log.d("listaEspacios", "Se agregó espacio de key: "+snapshot.getKey());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        for (int ii=0; ii<listaEspacios.size(); ii++){
                            if (snapshot.getKey().equals(listaEspacios.get(ii).getKey())){
                                EspacioDTO espacio = snapshot.getValue(EspacioDTO.class);
                                espacio.setKey(snapshot.getKey());

                                if(espacio.isActivo()){
                                    listaEspacios.set(ii,espacio);
                                    Log.d("listaEspacios", "Se modificó el espacio de key: "+snapshot.getKey());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("listaEspacios", error.getMessage());
                    }
                });

        RecyclerView recyclerView = findViewById(R.id.rvListaEspaciosUsuario);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
*/
    }

    private void listarTickets (String server, String user, String pwd) {

        // Se crea el request de Volley
        RequestQueue queue = Volley.newRequestQueue(ListaTicketsActivity.this);
        String url = "https://" + server + "/REST/2.0/tickets?query=(Status = 'new' OR Status = 'open')&fields=Owner,Status,Created,Subject,Queue,CustomFields,Requestor,Cc,AdminCc,CustomRoles";
        Log.i("tickets", "URL: "+url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response ->  {
                    Log.i("tickets", "JSON antes:\n"+response);

                    // Se filtra sol la lista de tickets
                    String o = JsonParser.parseString(response).getAsJsonObject().get("items").toString();
                    Log.i("tickets", "JSON despues:\n"+o);

                    // Se guarda el JSON en una clase tickets
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();;
                    TicketDTO[] listaTickets = gson.fromJson(o,TicketDTO[].class);
                    Log.i("tickets", "Objeto generado: " + gson.toJson(listaTickets));
                /*
[{"AdminCc":[],"Created":"2023-11-07T05:19:39Z","Requestor":[{"id":"root","_url":"http://rt5.tesis.cloudns.ph/REST/2.0/user/root","type":"user"}],"Queue":{"id":"1","type":"queue","_url":"http://rt5.tesis.cloudns.ph/REST/2.0/queue/1"},"Subject":"Prueba para la tesis","type":"ticket","Status":"new","Cc":[],"CustomRoles":{},"id":"1","Owner":{"type":"user","_url":"http://rt5.tesis.cloudns.ph/REST/2.0/user/Nobody","id":"Nobody"},"_url":"http://rt5.tesis.cloudns.ph/REST/2.0/ticket/1","CustomFields":""},{"Subject":"prueba tesis 2","Requestor":[{"id":"root","_url":"http://rt5.tesis.cloudns.ph/REST/2.0/user/root","type":"user"}],"Queue":{"id":"1","_url":"http://rt5.tesis.cloudns.ph/REST/2.0/queue/1","type":"queue"},"Created":"2023-11-07T05:38:15Z","AdminCc":[],"Cc":[],"CustomRoles":{},"type":"ticket","Status":"new","Owner":{"id":"Nobody","type":"user","_url":"http://rt5.tesis.cloudns.ph/REST/2.0/user/Nobody"},"id":"2","CustomFields":"","_url":"http://rt5.tesis.cloudns.ph/REST/2.0/ticket/2"},{"Owner":{"_url":"http://rt5.tesis.cloudns.ph/REST/2.0/user/root","type":"user","id":"root"},"id":"3","CustomFields":"","_url":"http://rt5.tesis.cloudns.ph/REST/2.0/ticket/3","Requestor":[{"type":"user","_url":"http://rt5.tesis.cloudns.ph/REST/2.0/user/root","id":"root"}],"Created":"2023-11-09T07:07:30Z","Queue":{"_url":"http://rt5.tesis.cloudns.ph/REST/2.0/queue/1","type":"queue","id":"1"},"Subject":"","AdminCc":[],"Cc":[],"CustomRoles":{},"type":"ticket","Status":"new"}]

                JsonObject o = JsonParser.parseString(response).getAsJsonObject();
                o.remove("LastUpdatedBy");
                o.remove("_hyperlinks");
                o.remove("Creator");

                Log.i("logueo", "JSON despues:\n"+o);
                UserDTO usuario = gson.fromJson(o,UserDTO.class);
                Log.i("logueo", "Objeto generado:" + gson.toJson(usuario)
                 */
                },
                error -> {
                    Log.e("tickets", error.getMessage());
                    Toast.makeText(ListaTicketsActivity.this, "Hubo un error de conexión", Toast.LENGTH_SHORT).show();
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


    public void configurarNavBar(UserDTO usuario){
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