package pe.edu.pucp.rtcompanion.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private class Grupo implements Serializable {
        String type;
        String id;
        String _url;
    }
    @SerializedName("Disabled")
    private Integer estaDeshabilitado;
    @SerializedName("Created")
    private String creacion;
    private Integer id;
    @SerializedName("RealName")
    private String nombreCompleto;
    @SerializedName("Name")
    private String alias;
    @SerializedName("EmailAddress")
    private String correo;
    @SerializedName("HomePhone")
    private String telefono;
    @SerializedName("MobilePhone")
    private String celular;
    @SerializedName("Address1")
    private String direccion;
    @SerializedName("City")
    private String ciudad;
    @SerializedName("Country")
    private String pais;
    @SerializedName("Organization")
    private String organizacion;
    @SerializedName("Memberships")
    private Grupo[] grupos;

    public Integer getEstaDeshabilitado() {
        return estaDeshabilitado;
    }
    public String getAlias() {
        return alias;
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
    public String getCreacion() {
        return creacion;
    }
    public String getTelefono() {return telefono;}
    public String getCelular() {return celular;}
    public String getDireccion() {return direccion;}
    public String getCiudad() {return ciudad;}
    public String getPais() {return pais;}
    public String getOrganizacion() {return organizacion;}
    public String getIdGrupos() {return grupos[0].id;}
}
