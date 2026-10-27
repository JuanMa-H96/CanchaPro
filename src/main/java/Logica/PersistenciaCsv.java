
package Logica;
import Datos.Clientes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;

public class PersistenciaCsv {
    
   public Clientes[] leerClientes(){
       Clientes[] clientes=new Clientes[500];
       int cantidadClientes = 0;
       
       try{
           
           BufferedReader lector=new BufferedReader(
           new FileReader("C:\\Users\\juan1\\OneDrive\\Documentos\\NetBeansProjects\\CanchaPro\\csv/Clientes.csv"));
           
           System.out.println("CSV abierto correctamente");
           lector.readLine();
            String linea;
           
           while((linea=lector.readLine()) !=null){
               System.out.println("leyendo");
               String[] datos = linea.split(",");
                
               clientes[cantidadClientes]=new Clientes(
               Integer.parseInt(datos[0].trim()),
               datos[1].trim(), Integer.parseInt(datos[2].trim()), datos[3].trim());
               cantidadClientes++;
               }
             lector.close();
           System.out.println("total de clientes leidos"+ cantidadClientes);
       }catch(IOException error){
           JOptionPane.showMessageDialog(null,"Error al leer CSV");
       }
       
        return clientes; 
   }
    
}
