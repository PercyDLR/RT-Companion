package pe.edu.pucp.rtcompanion.dtos;

import android.graphics.ColorMatrix;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class TicketDTO {
    class Usuario {
        String id;
        String _url;
        String type;
    }
    @SerializedName("Subject")
    String asunto;
    @SerializedName("Owner")
    Usuario creador;
    @SerializedName("Requestor")
    Usuario[] requestors;
    @SerializedName("Created")
    Date fechaCreacion;
    @SerializedName("Due")
    Date fechaCierre;
    @SerializedName("Status")
    String estado;
}
