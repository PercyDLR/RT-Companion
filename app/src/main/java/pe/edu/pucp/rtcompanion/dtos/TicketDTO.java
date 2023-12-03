package pe.edu.pucp.rtcompanion.dtos;

import android.graphics.ColorMatrix;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class TicketDTO implements Serializable {
    private class Usuario {
        String id;
        String _url;
        String type;
    }
    @SerializedName("Subject")
    private String asunto;
    @SerializedName("Owner")
    private Usuario creador;
    @SerializedName("Requestor")
    private Usuario[] requestors;
    @SerializedName("Created")
    private Date fechaCreacion;
    @SerializedName("Due")
    private Date fechaCierre;
    @SerializedName("Status")
    private String estado;
    @SerializedName("Priority")
    private String prioridad;

    public String getAsunto() {return asunto;}
    public Usuario getCreador() {return creador;}
    public Usuario[] getRequestors() {return requestors;}
    public Date getFechaCreacion() {return fechaCreacion;}
    public Date getFechaCierre() {return fechaCierre;}
    public String getEstado() {return estado;}
    public String getPrioridad() {return prioridad;}
}
