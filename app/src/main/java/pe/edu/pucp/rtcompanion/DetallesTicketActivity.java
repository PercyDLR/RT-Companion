package pe.edu.pucp.rtcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import pe.edu.pucp.rtcompanion.dtos.TicketDTO;
import pe.edu.pucp.rtcompanion.dtos.UserDTO;

public class DetallesTicketActivity extends AppCompatActivity {
    private TicketDTO ticket;
    private UserDTO usuario;
    private BlurView progressBar;
    private TextView tvAsunto, tvDescripcion, tvEstado, tvPrioridad, tvInicio, tvFin, tvCreador, tvSolicitantes, tvObservadores;

    //TODO: Agregar los elementos de información

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_ticket);

        progressBar = findViewById(R.id.pbDetalle);
        setupBlur();
        progressBar.setVisibility(View.VISIBLE);

        // Se obtienen los datos del intent
        usuario = (UserDTO) getIntent().getExtras().get("usuario");
        ticket = (TicketDTO) getIntent().getExtras().get("ticket");

        //Se mapean todos los campos a rellenar
        tvAsunto = findViewById(R.id.tvAsuntoDetalle);
        tvDescripcion = findViewById(R.id.tvDescripcionDetalle);
        tvEstado = findViewById(R.id.tvEstadoDetalle);
        tvPrioridad = findViewById(R.id.tvPrioridadDetalle);
        tvInicio = findViewById(R.id.tvInicioDetalle);
        tvFin = findViewById(R.id.tvFinDetalle);
        tvCreador = findViewById(R.id.tvCreadorDetalle);
        tvSolicitantes = findViewById(R.id.tvSoliciantesDetalle);
        tvObservadores = findViewById(R.id.tvObservadoresDetalle);

        // Se adiciona la info a cada campo
        tvAsunto.setText(ticket.getAsunto());
        tvEstado.setText(ticket.getEstado());
        tvPrioridad.setText(ticket.getPrioridad());
        tvCreador.setText(ticket.getCreador().getId());

        // Info de fecha
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        tvInicio.setText(df.format(ticket.getFechaCreacion()));
        tvFin.setText(df.format(ticket.getFechaCierre()));

        // Info de solicitantes
        ArrayList<String> listaSolicitantes = new ArrayList<>();
        for (int i=0; i<ticket.getSolicitantes().length; i++) {
            listaSolicitantes.add(ticket.getSolicitantes()[i].getId());
        }
        if (ticket.getSolicitantes().length == 1){
            tvSolicitantes.setText(listaSolicitantes.get(0));
        }
        else {
            listaSolicitantes.set(0,"• "+listaSolicitantes.get(0));
            tvSolicitantes.setText(String.join("\n• ",listaSolicitantes));
        }

        // Info de observadores
        Set<String> listaObservadores = new HashSet<>();
        for (int i=0; i<ticket.getCc().length; i++) {
            listaObservadores.add(ticket.getSolicitantes()[i].getId());
        }
        for (int i=0; i<ticket.getAdminCc().length; i++) {
            listaObservadores.add(ticket.getSolicitantes()[i].getId());
        }
        if (listaObservadores.size() == 1){
            tvObservadores.setText(String.join("\n• ",listaObservadores));
        }
        else {
            tvObservadores.setText("• "+String.join("\n• ",listaObservadores));
        }
        progressBar.setVisibility(View.GONE);
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