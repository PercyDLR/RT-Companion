package pe.edu.pucp.rtcompanion.dtos;

import android.graphics.ColorMatrix;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class TicketDTO implements Serializable {
    public class Usuario implements Serializable {
        String id;
        String _url;
        String type;
        public String getId() {return id;}
    }
    @SerializedName("Subject")
    private String asunto;
    @SerializedName("Owner")
    private Usuario creador;
    @SerializedName("Requestor")
    private Usuario[] solicitantes;
    @SerializedName("Cc")
    private Usuario[] cc;
    @SerializedName("AdminCc")
    private Usuario[] adminCc;
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
    public Usuario[] getSolicitantes() {return solicitantes;}
    public Usuario[] getCc() {return cc;}
    public Usuario[] getAdminCc() {return adminCc;}
    public Date getFechaCreacion() {return fechaCreacion;}
    public Date getFechaCierre() {return fechaCierre;}
    public String getEstado() {return estado;}
    public String getPrioridad() {return prioridad;}
}
