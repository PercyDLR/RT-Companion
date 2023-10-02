package pe.edu.pucp.rtcompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class ListaTicketsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextInputLayout buscadorEspacios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tickets);

        configurarNavBar();
        buscadorEspacios = findViewById(R.id.etBusquedaEspaciosUsuario);

        // Setea los valores iniciales de la lista en la pantalla
        listarEspacios("");

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

    public void configurarNavBar(){
        bottomNavigationView = findViewById(R.id.nvListaTickets);
        bottomNavigationView.setSelectedItemId(R.id.navigation_tickets);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (R.id.navigation_tickets == item.getItemId()){
                return true;
            }
            else if (R.id.navigation_info_cuenta == item.getItemId()){
                startActivity(new Intent(getApplicationContext(), CuentaUsuarioActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(0,0);
                return true;
            }
            return false;
        });
    }
}