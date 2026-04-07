package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.dao.PagoDAO;
import com.horizontes.dao.ReservacionDAO;
import com.horizontes.models.Pago;
import com.horizontes.models.Reservacion;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/pagos/*")
public class PagoServlet extends HttpServlet {

    private final PagoDAO dao = new PagoDAO();
    private final ReservacionDAO reservacionDAO = new ReservacionDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String comprobanteParam = req.getParameter("comprobante");
        String reservacionParam = req.getParameter("reservacion");

        if (comprobanteParam != null) {
            try {
                int reservacionId = Integer.parseInt(comprobanteParam);
                Reservacion r = reservacionDAO.buscarPorId(reservacionId);
                List<Pago> pagos = dao.listarPorReservacion(reservacionId);
                byte[] pdf = com.horizontes.utils.PdfGenerator.generarComprobantePago(r, pagos);
                res.setContentType("application/pdf");
                res.setHeader("Content-Disposition", "attachment; filename=comprobante_" + r.getNumero() + ".pdf");
                res.getOutputStream().write(pdf);
            } catch (Exception e) {
                res.setStatus(500);
                res.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
            }
        } else if (reservacionParam != null) {
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            PrintWriter out = res.getWriter();
            try {
                List<Pago> lista = dao.listarPorReservacion(Integer.parseInt(reservacionParam));
                out.print(gson.toJson(lista));
            } catch (Exception e) {
                res.setStatus(500);
                out.print("{\"error\":\"" + e.getMessage() + "\"}");
            }
        } else {
            res.setStatus(400);
            res.getWriter().print("{\"error\":\"Se requiere el parametro reservacion o comprobante\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            Pago p = gson.fromJson(req.getReader(), Pago.class);
            boolean ok = dao.insertar(p);
            if (ok) {
                Reservacion r = reservacionDAO.buscarPorId(p.getReservacionId());
                double totalPagado = reservacionDAO.getTotalPagado(p.getReservacionId());
                if (totalPagado >= r.getCostoTotal()) {
                    reservacionDAO.actualizarEstado(r.getId(), "CONFIRMADA");
                    out.print("{\"mensaje\":\"Pago registrado. Reservacion CONFIRMADA\",\"confirmada\":true}");
                } else {
                    out.print("{\"mensaje\":\"Pago registrado correctamente\",\"confirmada\":false," +
                              "\"pendiente\":" + (r.getCostoTotal() - totalPagado) + "}");
                }
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo registrar el pago\"}");
            }
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}