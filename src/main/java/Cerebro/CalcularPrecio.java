package Cerebro;

import Archivos.Cancha;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class CalcularPrecio {

    private double NocheSemana = 0.25;
    private double FinSemanaDia = 0.20;
    private double SemanaNoche = 0.75;

    private double precioBalon = 25.00;
    private double precioArbitro = 75.00;
    private double precioChaleco = 10.00;

   public double calcularSubtotalCancha(Cancha cancha, LocalDate fecha, LocalTime horaInicio, int duracionHoras) {
    double precioPorHora = cancha.getPrecioHora();

    boolean finDeSemana =
            fecha.getDayOfWeek() == DayOfWeek.SATURDAY
            || fecha.getDayOfWeek() == DayOfWeek.SUNDAY;

    boolean esNoche = horaInicio.getHour() >= 17;

    
   if (finDeSemana && esNoche) {
        precioPorHora = precioPorHora * (1 + SemanaNoche);
    } else if (finDeSemana) {
        precioPorHora = precioPorHora * (1 + FinSemanaDia);
    } else if (esNoche) {
        precioPorHora = precioPorHora * (1 + NocheSemana);
    }

    return precioPorHora * duracionHoras;
}

    public double calcularTotalExtras(
            int cantidadBalones,
            int cantidadArbitros,
            int cantidadChalecos
    ) {
        return cantidadBalones * precioBalon
                + cantidadArbitros * precioArbitro
                + cantidadChalecos * precioChaleco;
    }

    public double calcularTotalReserva(
            double subtotalCancha,
            double totalExtras
    ) {
        return subtotalCancha + totalExtras;
    }
    
    public double getPrecioBalon() {
    return precioBalon;
    }

    public double getPrecioArbitro() {
        return precioArbitro;
    }

    public double getPrecioChaleco() {
        return precioChaleco;
    }
}