package pe.edu.pucp.rtcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import pe.edu.pucp.rtcompanion.dtos.TicketDTO;
import pe.edu.pucp.rtcompanion.dtos.UserDTO;

public class DetallesTicketActivity extends AppCompatActivity {
    private TicketDTO ticket;
    private UserDTO usuario;

    //TODO: Agregar los elementos de información

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_ticket);

        //TODO: Setear Progress bar
        //progressBar = findViewById(R.id.pbCuentaUsuario);
        //progressBar.setVisibility(View.VISIBLE);

        // Se obtienen los datos
        usuario = (UserDTO) getIntent().getExtras().get("usuario");
        ticket = (TicketDTO) getIntent().getExtras().get("ticket");

        //TODO: Agregar info a cada elemento de información
    }
}