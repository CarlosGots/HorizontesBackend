package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.dao.CancelacionDAO;
import com.horizontes.dao.ReservacionDAO;
import com.horizontes.utils.PdfGenerator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/reportes/*")
public class ReporteServlet extends HttpServlet {

    private final ReservacionDAO reservacionDAO = new ReservacionDAO();
    private final CancelacionDAO cancelacionDAO = new CancelacionDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        String fechaInicio = req.getParameter("inicio");
        String fechaFin = req.getParameter("fin");
        boolean exportarPdf = "true".equals(req.getParameter("pdf"));

        try {
            if (pathInfo == null) {
                res.setStatus(400);
                res.getWriter().print("{\"error\":\"Especifica el tipo de reporte\"}");
                return;
            }

            switch (pathInfo) {
                case "/ventas" -> {
                    var lista = reservacionDAO.listar();
                    if (exportarPdf) {
                        byte[] pdf = PdfGenerator.generarReporteVentas(lista);
                        res.setContentType("application/pdf");
                        res.setHeader("Content-Disposition", "attachment; filename=reporte_ventas.pdf");
                        res.getOutputStream().write(pdf);
                    } else {
                        res.setContentType("application/json");
                        res.getWriter().print(gson.toJson(lista));
                    }
                }
                case "/cancelaciones" -> {
                    var lista = cancelacionDAO.listarPorIntervalo(fechaInicio, fechaFin);
                    if (exportarPdf) {
                        byte[] pdf = PdfGenerator.generarReporteCancelaciones(lista);
                        res.setContentType("application/pdf");
                        res.setHeader("Content-Disposition", "attachment; filename=reporte_cancelaciones.pdf");
                        res.getOutputStream().write(pdf);
                    } else {
                        res.setContentType("application/json");
                        res.getWriter().print(gson.toJson(lista));
                    }
                }
                default -> {
                    res.setStatus(400);
                    res.getWriter().print("{\"error\":\"Tipo de reporte no reconocido\"}");
                }
            }
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}