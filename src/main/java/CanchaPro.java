import Grafica.Login;
import Grafica.Grafica;
import Logica.PersistenciaCsv;
import Datos.Clientes;

public class CanchaPro {

    public static void main(String[] args) {
      /* Login v1 =new Login();
       v1.setLocationRelativeTo(null);
       v1.setVisible(true);*/
        
      PersistenciaCsv archivo=new PersistenciaCsv();
      Clientes[]clientes=archivo.leerClientes();
        
      
      
        Grafica v1 =new Grafica();
        v1.setLocationRelativeTo(null);
        v1.setVisible(true);
       
       
    }
}
