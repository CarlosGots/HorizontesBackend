package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.dao.PaqueteDAO;
import com.horizontes.dao.ServicioPaqueteDAO;
import com.horizontes.models.Paquete;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/paquetes/*")
public class PaqueteServlet extends HttpServlet {

    private final PaqueteDAO dao = new PaqueteDAO();
    private final ServicioPaqueteDAO servicioDao = new ServicioPaqueteDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            String pathInfo = req.getPathInfo();
            String destinoParam = req.getParameter("destino");

            if (pathInfo != null && pathInfo.length() > 1) {
                // GET /api/paquetes/{id} — detalle con servicios
                int id = Integer.parseInt(pathInfo.substring(1));
                Paquete p = dao.buscarPorId(id);
                if (p != null) {
                    p.setServicios(servicioDao.listarPorPaquete(id));
                    // Calcular ocupación y alertar si supera 80%
                    int ocupacion = dao.contarOcupacion(id);
                    double porcentaje = (double) ocupacion / p.getCapacidad() * 100;
                    if (porcentaje >= 80) {
                        p.setDescripcion(p.getDescripcion() + " [ALTA DEMANDA: " + 
                                         String.format("%.0f", porcentaje) + "% ocupado]");
                    }
                    out.print(gson.toJson(p));
                } else {
                    res.setStatus(404);
                    out.print("{\"error\":\"Paquete no encontrado\"}");
                }
            } else if (destinoParam != null) {
                // GET /api/paquetes?destino={id}
                List<Paquete> lista = dao.listarPorDestino(Integer.parseInt(destinoParam));
                out.print(gson.toJson(lista));
            } else {
                // GET /api/paquetes — listar todos
                List<Paquete> lista = dao.listar();
                out.print(gson.toJson(lista));
            }
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            Paquete p = gson.fromJson(req.getReader(), Paquete.class);
            int id = dao.insertar(p);
            if (id > 0 && p.getServicios() != null) {
                for (var servicio : p.getServicios()) {
                    servicio.setPaqueteId(id);
                    servicioDao.insertar(servicio);
                }
                out.print("{\"mensaje\":\"Paquete creado correctamente\",\"id\":" + id + "}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo crear el paquete\"}");
            }
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            Paquete p = gson.fromJson(req.getReader(), Paquete.class);
            boolean ok = dao.actualizar(p);
            if (ok && p.getServicios() != null) {
                servicioDao.eliminarPorPaquete(p.getId());
                for (var servicio : p.getServicios()) {
                    servicio.setPaqueteId(p.getId());
                    servicioDao.insertar(servicio);
                }
                out.print("{\"mensaje\":\"Paquete actualizado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo actualizar el paquete\"}");
            }
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            String pathInfo = req.getPathInfo();
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean ok = dao.desactivar(id);
            if (ok) {
                out.print("{\"mensaje\":\"Paquete desactivado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo desactivar el paquete\"}");
            }
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}