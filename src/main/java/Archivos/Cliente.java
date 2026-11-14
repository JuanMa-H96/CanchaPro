package Archivos;

import java.time.LocalDate;
import javax.swing.JOptionPane;

public class Cliente {

    private String codigo;
    private String nombre;
    private String telefono;
    private String email;
    private boolean activo;
    private LocalDate fechaRegistro;

    public Cliente(String nombre, String telefono, String email, String codigo, LocalDate fechaRegistro) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.codigo = codigo;
        this.activo = true;
        this.fechaRegistro = fechaRegistro == null
        ? LocalDate.now()
        : fechaRegistro;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public LocalDate getFechaRegistro() {
    return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void mostrarcliente() {
        JOptionPane.showMessageDialog(null,
                "CLIENTE\n"
                + "Nombre: " + nombre
                + "\nTeléfono: " + telefono
                + "\nEmail: " + email
                + "\nCódigo: " + codigo
                + "\nEstado: " + (activo ? "Activo" : "Inactivo")
                 +"\nFecha de Registro: " + fechaRegistro);
    }
}