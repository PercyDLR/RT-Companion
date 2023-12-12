package pe.edu.pucp.rtcompanion.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import pe.edu.pucp.rtcompanion.DetallesTicketActivity;
import pe.edu.pucp.rtcompanion.R;
import pe.edu.pucp.rtcompanion.dtos.TicketDTO;
import pe.edu.pucp.rtcompanion.dtos.UserDTO;

public class ListaTicketsAdapter extends RecyclerView.Adapter<ListaTicketsAdapter.TicketViewHolder> {
    private ArrayList<TicketDTO> listaTickets;
    private Context context;
    private UserDTO usuario;

    // Definiendo algunos métodosSe agregó el guardado de credenciales y la ventana de cerrar sesión.
    public void setListaTickets(ArrayList<TicketDTO> listaTickets) {this.listaTickets = listaTickets;}

    public void setContext(Context context) {this.context = context;}
    public void setUsuario(UserDTO usuario) {this.usuario = usuario;}

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TicketDTO ticket;
        public TicketViewHolder(@NonNull View itemView) {super(itemView);}
    }
    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.elemento_ticket,parent,false);
        return new TicketViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaTicketsAdapter.TicketViewHolder holder, int position) {
        TicketDTO ticket = listaTickets.get(position);
        holder.ticket = ticket;

        // Se mapean los elementos del fragmento
        TextView encabezado = holder.itemView.findViewById(R.id.rvEncabezadoTicket);
        TextView dias = holder.itemView.findViewById(R.id.rvFechaTicket);
        TextView prioridad = holder.itemView.findViewById(R.id.rvPrioridadTicket);
        ImageButton boton = holder.itemView.findViewById(R.id.rvBotonDetallesTicket);

        // Título
        encabezado.setText(ticket.getAsunto().equals("") ? "Sin Asunto":ticket.getAsunto());

        // Fecha
        Date hoy = new Date();
        long different = hoy.getTime() - ticket.getFechaCreacion().getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthsInMilli = daysInMilli * 30;

        long elapsedTime; String time;
        if ((elapsedTime = different / monthsInMilli) > 0) {
            Log.d("adapter", "meses:" + different);
            time = "m";
        } else if ((elapsedTime = different / daysInMilli) > 0) {
            Log.d("adapter", "dias:" + elapsedTime);
            time = "d";
        } else if ((elapsedTime = different / hoursInMilli) > 0) {
            Log.d("adapter", "horas:" + elapsedTime);
            time = "h";
        } else if ((elapsedTime = different / minutesInMilli) > 0) {
            Log.d("adapter", "minutos:" + elapsedTime);
            time = "min";
        } else {
            elapsedTime = different / secondsInMilli;
            Log.d("adapter", "segundos:" + elapsedTime);
            time = "segundos";
        }
        Log.d("adapter", "monthInMilis: "+monthsInMilli);
        dias.setText(elapsedTime + " " + time);

        // Prioridad
        String prioridadStr;
        if (ticket.getPrioridad().equals("0")){prioridadStr  = "Baja";}
        else if (ticket.getPrioridad().equals("100")){prioridadStr  = "Alta";}
        else {prioridadStr  = "Media";}
        prioridad.setText(prioridadStr);

        // Se configura el botón
        boton.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetallesTicketActivity.class);
            intent.putExtra("ticket",ticket);
            intent.putExtra("usuario",usuario);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaTickets.size();
    }
}
