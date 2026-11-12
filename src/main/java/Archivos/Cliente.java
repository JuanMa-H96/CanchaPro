package Archivos;


import javax.swing.JOptionPane;

public class Cliente {
private String nombre;
private String telefono;
private String email;
private String codigo;


public Cliente(String nombre, String telefono, String email, String codigo){
this.nombre=nombre;
this.telefono=telefono;
this.email=email;
this.codigo=codigo;
}

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


    public void mostrarcliente(){
        JOptionPane.showMessageDialog(null,"CLIENTE\nNombre: "+ nombre 
                + "\nTelefono: "+ telefono
                + "\nEmail: " + email
                + "\nCodigo: "+ codigo);
    }


    
}