package pe.edu.pucp.rtcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextInputLayout inputServer, inputCorreo, inputPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.pbLogin);
        progressBar.setVisibility(View.VISIBLE);

        inputServer = findViewById(R.id.etLoginIP);
        inputCorreo = findViewById(R.id.etLoginCorreo);
        inputPwd = findViewById(R.id.etLoginPswd);
        inputPwd.setErrorIconDrawable(null);

        // TODO: Implementar Logueo
        if (existenCred()){
        }

        progressBar.setVisibility(View.GONE);
    }

    public void logueo (View view){

        progressBar.setVisibility(View.VISIBLE);

        String server = inputServer.getEditText().getText().toString().trim();
        String correo = inputCorreo.getEditText().getText().toString().trim();
        String pwd = inputPwd.getEditText().getText().toString().trim();

        if (datosValidos(server,correo,pwd)){

            // Se guarda el archivo de credenciales en memoria interna
            String archivo = "credenciales";
            String contenido = server + "\n" + correo + "\n" + pwd;

            //Se utiliza la clase FileOutputStream para poder almacenar en Android
            try (FileOutputStream fileOutputStream = this.openFileOutput(archivo, Context.MODE_PRIVATE);
                 FileWriter fileWriter = new FileWriter(fileOutputStream.getFD())) {
                fileWriter.write(contenido);
            } catch (IOException e) {
                e.printStackTrace();
            }

            startActivity(new Intent(getApplicationContext(),ListaTicketsActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        // Se logueo con Firebase Auth
            /*
            auth.signInWithEmailAndPassword(correo,pwd)
                    .addOnCompleteListener(logueo -> {
                        if (logueo.isSuccessful() ){
                            if (auth.getCurrentUser().isEmailVerified()){

                                // Se busca la cuenta en Realtime DB
                                ref.child(auth.getCurrentUser().getUid()).get()
                                        .addOnCompleteListener(task -> {

                                            progressBar.setVisibility(View.GONE);
                                            // Si se encuentra algo en la búsqueda
                                            if (task.isSuccessful() && task.getResult().exists()){

                                                UsuarioDTO user = task.getResult().getValue(UsuarioDTO.class);

                                                // Si el usuario no fue baneado, se redirige por rol
                                                if(user.isActivo()){
                                                    if (user.getRol().equals("admin")){
                                                        Log.d("logueo", "Logueo Exitoso: admin");
                                                        startActivity(new Intent(getApplicationContext(), ListaEspaciosActivity.class));
                                                        finish();
                                                    } else if (user.getRol().equals("usuario")) {
                                                        Log.d("logueo", "Logueo Exitoso: usuario");
                                                        startActivity(new Intent(getApplicationContext(), ListaEspaciosUsuarioActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(),"No se pudo obtener el rol",Toast.LENGTH_SHORT).show();
                                                        Log.e("logueo", "No se pudo obtener el rol");
                                                    }
                                                } else {
                                                    Toast.makeText(getApplicationContext(),"Su cuenta se encuentra bloqueada",Toast.LENGTH_SHORT).show();
                                                    Log.e("logueo", "El usuario se encuentra baneado");
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                Log.e("logueo", task.getException().getMessage());
                                            }
                                        });
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"Debe verificar su correo para poder loguearse",Toast.LENGTH_SHORT).show();
                                Log.d("logueo", "Debe verificar su correo para poder loguearse");
                                inputPwd.getEditText().setText("");
                                auth.getCurrentUser().sendEmailVerification();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.e("logueo", logueo.getException().getMessage());
                            inputPwd.getEditText().setText("");
                            inputPwd.setError("Correo o Contraseña inválidos");
                            inputCorreo.setError("Correo o Contraseña inválidos");
                        }
                    });
            */
        } else {progressBar.setVisibility(View.GONE);}
    }

    private boolean existenCred(){
        String fileName = "credenciales";

        try (FileInputStream fileInputStream = openFileInput(fileName);
             FileReader fileReader = new FileReader(fileInputStream.getFD());
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            inputServer.getEditText().setText(bufferedReader.readLine());
            inputCorreo.getEditText().setText(bufferedReader.readLine());
            inputPwd.getEditText().setText(bufferedReader.readLine());

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    public void pasarRapido(View view){
        startActivity(new Intent(getApplicationContext(),ListaTicketsActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private boolean datosValidos (String server, String correo, String pwd){
        progressBar.setVisibility(View.VISIBLE);
        boolean valido = true;

        // Se quitan los mensajes previos
        inputServer.setError(null);
        inputCorreo.setError(null);
        inputPwd.setError(null);

        try {
            new URL(server).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            inputServer.setError("La URL ingresada no es válida");
            valido = false;
        }

        if (!correo.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+")){
            inputCorreo.setError("El correo ingresado no es válido");
            valido = false;
        }
        if (!pwd.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[*.!¡¿?@$%^&~_+-=]).{8,}$")) {
            inputPwd.setError("La contraseña debe tener al menos 8 dígitos, un número y un caracter especial");
            valido = false;
        }
        return valido;
    }
}