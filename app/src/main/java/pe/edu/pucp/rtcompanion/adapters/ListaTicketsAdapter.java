package pe.edu.pucp.rtcompanion.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pe.edu.pucp.rtcompanion.R;
import pe.edu.pucp.rtcompanion.dtos.TicketDTO;

public class ListaTicketsAdapter extends RecyclerView.Adapter<ListaTicketsAdapter.TicketViewHolder> {
    private ArrayList<TicketDTO> listaTickets;
    private Context context;

    // Definiendo algunos métodosSe agregó el guardado de credenciales y la ventana de cerrar sesión.
    public ArrayList<TicketDTO> getListaTickets() {return listaTickets;}
    public void setListaTickets(ArrayList<TicketDTO> listaTickets) {this.listaTickets = listaTickets;}
    public Context getContext() {return context;}
    public void setContext(Context context) {this.context = context;}

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

        // TODO: Extraer valores desde la API
        encabezado.setText(ticket.getAsunto());
        // contador.setText(espacio.getHorariosDisponibles().toString());

        //boton.setOnClickListener(view -> {
        //    Intent intent = new Intent(context, DetallesEspacioActivity.class);
        //    intent.putExtra("espacio",espacio);
        //    intent.putExtra("funcion",funcion);
        //    context.startActivity(intent);
        //});
    }

    @Override
    public int getItemCount() {
        return listaTickets.size();
    }
}
