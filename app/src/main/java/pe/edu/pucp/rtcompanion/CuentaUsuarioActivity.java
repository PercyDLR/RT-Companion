package pe.edu.pucp.rtcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

import pe.edu.pucp.rtcompanion.dtos.UserDTO;

public class CuentaUsuarioActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView tvNombreCompleto, tvCorreo, tvRol;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta_usuario);

        // Se setea la progressbar
        progressBar = findViewById(R.id.pbCuentaUsuario);
        progressBar.setVisibility(View.VISIBLE);

        // Se obtienen los datos del usuario
        UserDTO usuario = (UserDTO) getIntent().getExtras().get("usuario");
        configurarNavBar(usuario);

        tvNombreCompleto = findViewById(R.id.tvNombreCuentaUsuario);
        tvCorreo = findViewById(R.id.tvCorreoCuentaUsuario);
        tvRol = findViewById(R.id.tvTICuentaUsuario);

        tvNombreCompleto.setText(usuario.getNombreCompleto());
        tvCorreo.setText(usuario.getCorreo());

        // Se llena la información del usuario

       /*
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()){
                UsuarioDTO user = task.getResult().getValue(UsuarioDTO.class);

                tvNombreCompleto.setText(user.getNombre());
                tvCorreo.setText(user.getCorreo());
                tvTI.setText(user.getTI());

                // Si ya pasó la fecha de la recarga y no se tienen los créditos completos
                Log.d("cuenta", "Prox recarga: " + user.getTimestampSiguienteRecarga() + " , Ahora: " + Instant.now().getEpochSecond());
                if(user.getTimestampSiguienteRecarga() < Instant.now().getEpochSecond()){
                    tvCreditos.setText("100");

                    // Este es el timestamp del próximo lunes a las 00:00
                    Long timestampProxLunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond();

                    // Se actualizan los creditos y el timestamp en la db
                    Map<String,Object> updates = new HashMap<>();
                    updates.put("timestampSiguienteRecarga",timestampProxLunes);
                    updates.put("creditos",100);
                    ref.updateChildren(updates);
                }
                else {tvCreditos.setText(user.getCreditos().toString());}
            }
        });
        */
        progressBar.setVisibility(View.GONE);
    }

    public void logout(View view){
        new MaterialAlertDialogBuilder(view.getContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estas seguro de querer cerrar tu sesión actual?")
                .setNegativeButton("Cancelar",((dialogInterface, i) -> {
                    dialogInterface.cancel();
                })).setPositiveButton("Cerrar Sesión", ((dialogInterface, i) -> {
                    // auth.signOut();
                    startActivity(new Intent(CuentaUsuarioActivity.this, LoginActivity.class));
                    dialogInterface.dismiss();
                    finish();
                })).show();
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