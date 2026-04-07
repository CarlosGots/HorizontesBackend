package com.horizontes.utils;

/**
 * La política de reembolso depende de cuántos días faltan para el viaje.
 */
public class ReembolsoCalculator {

    /**
     * Calcula el monto a reembolsar según los días de anticipación.
     * 
     * Política:
     * - Más de 30 días antes del viaje → 100% de reembolso
     * - Entre 15 y 30 días → 70% de reembolso
     * - Entre 7 y 14 días → 40% de reembolso
     * - Menos de 7 días → no se permite cancelar
     * 
     * @param diasRestantes días que faltan para la fecha de viaje
     * @param totalPagado monto total que el cliente ya pagó
     * @return arreglo de dos valores: [0] monto a reembolsar, [1] porcentaje aplicado
     */
    public static int[] calcular(long diasRestantes, double totalPagado) {
        int porcentaje;

        // Determinamos el porcentaje según los días de anticipación
        if (diasRestantes > 30) {
            porcentaje = 100; // Cancelación con mucha anticipación, reembolso total
        } else if (diasRestantes >= 15) {
            porcentaje = 70; // Cancelación con anticipación media
        } else if (diasRestantes >= 7) {
            porcentaje = 40; // Cancelación con poca anticipación
        } else {
            porcentaje = 0; // Menos de 7 días, no se permite cancelar
        }

        // Calculamos el monto según el porcentaje
        int montoReembolso = (int) (totalPagado * porcentaje / 100);
        return new int[]{montoReembolso, porcentaje};
    }
}