package pe.edu.pucp.rtcompanion.dtos;

import com.google.gson.annotations.SerializedName;

public class UserDTO {
    String LastUpdated;
    Integer Disabled;
    String AuthToken;
    Integer Privileged;
    String Comments;
    @SerializedName("Name")
    String nombre;
    @SerializedName("EmailAddress")
    String correo;
    class LastUpdatedBy {
        String _url;

    }
/*
 {
    "LastUpdated" : "2023-11-09T07:56:36Z",
    "Disabled" : "0",
    "AuthToken" : "bb6fbba502f42b5d",
    "Privileged" : 1,
    "Memberships" : [],
    "CustomFields" : [],
    "Comments" : "SuperUser",
    "Name" : "root",
    "EmailAddress" : "root@localhost",
    "LastUpdatedBy" : {
        "_url" : "http://rt5.tesis.cloudns.ph/REST/2.0/user/RT_System",
        "id" : "RT_System",
        "type" : "user"
    },
    "id" : 14,
    "RealName" : "Enoch Root",
    "Gecos" : "root",
    "_hyperlinks" : [
        {
            "_url" : "http://rt5.tesis.cloudns.ph/REST/2.0/user/14",
            "type" : "user",
            "ref" : "self",
            "id" : "14"
        },
        {
            "ref" : "history",
            "_url" : "http://rt5.tesis.cloudns.ph/REST/2.0/user/14/history"
        },
        {
            "ref" : "memberships",
            "_url" : "http://rt5.tesis.cloudns.ph/REST/2.0/user/14/groups"
        }
    ],
    "Creator" : {
        "_url" : "http://rt5.tesis.cloudns.ph/REST/2.0/user/RT_System",
        "id" : "RT_System",
        "type" : "user"
    },
    "Created" : "2023-11-04T04:40:05Z"
    }
 */
}
