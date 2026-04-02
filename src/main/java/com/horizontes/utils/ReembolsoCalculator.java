package com.horizontes.utils;

public class ReembolsoCalculator {

    /**
     * Calcula el reembolso según los días de anticipación.
     * @return int[] donde [0] = monto reembolso, [1] = porcentaje aplicado
     */
    public static int[] calcular(long diasRestantes, double totalPagado) {
        int porcentaje;

        if (diasRestantes > 30) {
            porcentaje = 100;
        } else if (diasRestantes >= 15) {
            porcentaje = 70;
        } else if (diasRestantes >= 7) {
            porcentaje = 40;
        } else {
            porcentaje = 0;
        }

        int montoReembolso = (int) (totalPagado * porcentaje / 100);
        return new int[]{montoReembolso, porcentaje};
    }
}