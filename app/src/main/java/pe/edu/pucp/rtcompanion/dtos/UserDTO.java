package pe.edu.pucp.rtcompanion.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDTO implements Serializable {
    @SerializedName("Disabled")
    private
    Integer estaDeshabilitado;
    private String AuthToken;
    private Integer Privileged;
    private String Comments;
    @SerializedName("Name")
    private
    String nombre;
    @SerializedName("EmailAddress")
    private
    String correo;
    private Integer id;
    @SerializedName("RealName")
    private
    String nombreCompleto;
    private String Gecos;
    @SerializedName("Created")
    private
    String creacion;

    public Integer getEstaDeshabilitado() {
        return estaDeshabilitado;
    }

    public String getAuthToken() {
        return AuthToken;
    }

    public Integer getPrivileged() {
        return Privileged;
    }

    public String getComments() {
        return Comments;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public Integer getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getGecos() {
        return Gecos;
    }

    public String getCreacion() {
        return creacion;
    }
}
