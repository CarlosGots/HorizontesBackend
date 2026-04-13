package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.dao.CancelacionDAO;
import com.horizontes.dao.ReservacionDAO;
import com.horizontes.utils.PdfGenerator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

        // Convertir cadenas vacias a null para traer todos los registros
        if (fechaInicio != null && fechaInicio.isEmpty()) fechaInicio = null;
        if (fechaFin != null && fechaFin.isEmpty()) fechaFin = null;

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
                        enviarPdf(res, PdfGenerator.generarReporteVentas(lista), "reporte_ventas.pdf");
                    } else {
                        enviarJson(res, lista);
                    }
                }

                case "/cancelaciones" -> {
                    var lista = cancelacionDAO.listarPorIntervalo(fechaInicio, fechaFin);
                    if (exportarPdf) {
                        enviarPdf(res, PdfGenerator.generarReporteCancelaciones(lista), "reporte_cancelaciones.pdf");
                    } else {
                        enviarJson(res, lista);
                    }
                }

                case "/ganancias" -> {
                    Map<String, Object> datos = reservacionDAO.getReporteGanancias(fechaInicio, fechaFin);
                    if (exportarPdf) {
                        enviarPdf(res, PdfGenerator.generarReporteGanancias(datos), "reporte_ganancias.pdf");
                    } else {
                        enviarJson(res, datos);
                    }
                }

                case "/agente-ventas" -> {
                    Map<String, Object> datos = reservacionDAO.getAgentesMasVentas(fechaInicio, fechaFin);
                    if (exportarPdf) {
                        enviarPdf(res, PdfGenerator.generarReporteAgentes(datos, "REPORTE AGENTE CON MAS VENTAS"), "reporte_agente_ventas.pdf");
                    } else {
                        enviarJson(res, datos);
                    }
                }

                case "/agente-ganancias" -> {
                    Map<String, Object> datos = reservacionDAO.getAgentesMasGanancias(fechaInicio, fechaFin);
                    if (exportarPdf) {
                        enviarPdf(res, PdfGenerator.generarReporteAgentes(datos, "REPORTE AGENTE CON MAS GANANCIAS"), "reporte_agente_ganancias.pdf");
                    } else {
                        enviarJson(res, datos);
                    }
                }

                case "/paquetes-ventas" -> {
                    List<Map<String, Object>> lista = reservacionDAO.getPaquetesPorVentas(fechaInicio, fechaFin);
                    if (exportarPdf) {
                        enviarPdf(res, PdfGenerator.generarReportePaquetes(lista), "reporte_paquetes.pdf");
                    } else {
                        enviarJson(res, lista);
                    }
                }

                case "/ocupacion-destino" -> {
                    List<Map<String, Object>> lista = reservacionDAO.getOcupacionPorDestino(fechaInicio, fechaFin);
                    if (exportarPdf) {
                        enviarPdf(res, PdfGenerator.generarReporteOcupacion(lista), "reporte_ocupacion.pdf");
                    } else {
                        enviarJson(res, lista);
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

    private void enviarJson(HttpServletResponse res, Object datos) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().print(gson.toJson(datos));
    }

    private void enviarPdf(HttpServletResponse res, byte[] pdf, String nombreArchivo) throws IOException {
        res.setContentType("application/pdf");
        res.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo);
        res.getOutputStream().write(pdf);
    }
}