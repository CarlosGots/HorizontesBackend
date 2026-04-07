package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.dao.CancelacionDAO;
import com.horizontes.dao.ReservacionDAO;
import com.horizontes.models.Cancelacion;
import com.horizontes.models.Reservacion;
import com.horizontes.utils.ReembolsoCalculator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Servlet que maneja las cancelaciones de reservaciones.
 * Calcula automáticamente el reembolso según la política de la agencia.
 * URL: /api/cancelaciones
 */
@WebServlet("/api/cancelaciones/*")
public class CancelacionServlet extends HttpServlet {

    private final CancelacionDAO dao = new CancelacionDAO();
    private final ReservacionDAO reservacionDAO = new ReservacionDAO();
    private final Gson gson = new Gson();

    /**
     * Procesa una cancelación de reservación.
     * Verifica que la reservación pueda cancelarse y calcula el reembolso.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            // Recibimos el id de la reservación a cancelar
            Cancelacion datos = gson.fromJson(req.getReader(), Cancelacion.class);
            Reservacion r = reservacionDAO.buscarPorId(datos.getReservacionId());

            // Verificamos que la reservación exista
            if (r == null) {
                res.setStatus(404);
                out.print("{\"error\":\"Reservacion no encontrada\"}");
                return;
            }

            // Solo se pueden cancelar reservaciones PENDIENTES o CONFIRMADAS
            if (!r.getEstado().equals("PENDIENTE") && !r.getEstado().equals("CONFIRMADA")) {
                res.setStatus(400);
                out.print("{\"error\":\"Solo se pueden cancelar reservaciones PENDIENTES o CONFIRMADAS\"}");
                return;
            }

            // Calculamos cuántos días faltan para el viaje
            LocalDate hoy = LocalDate.now();
            LocalDate fechaViaje = LocalDate.parse(r.getFechaViaje());
            long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaViaje);

            // No se permite cancelar con menos de 7 días de anticipación
            if (diasRestantes < 7) {
                res.setStatus(400);
                out.print("{\"error\":\"No se puede cancelar con menos de 7 dias de anticipacion\"}");
                return;
            }

            // Calculamos el monto a reembolsar según la política
            double totalPagado = reservacionDAO.getTotalPagado(r.getId());
            int[] resultado = ReembolsoCalculator.calcular(diasRestantes, totalPagado);

            // Registramos la cancelación en la base de datos
            Cancelacion cancelacion = new Cancelacion();
            cancelacion.setReservacionId(r.getId());
            cancelacion.setMontoReembolso(resultado[0]);
            cancelacion.setPorcentajeReembolso(resultado[1]);
            dao.insertar(cancelacion);

            // Actualizamos el estado de la reservación a CANCELADA
            reservacionDAO.actualizarEstado(r.getId(), "CANCELADA");

            out.print("{\"mensaje\":\"Reservacion cancelada\",\"reembolso\":" + resultado[0] +
                      ",\"porcentaje\":" + resultado[1] + "}");
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}