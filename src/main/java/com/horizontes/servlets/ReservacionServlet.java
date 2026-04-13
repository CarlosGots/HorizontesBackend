package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.dao.ClienteDAO;
import com.horizontes.dao.ReservacionDAO;
import com.horizontes.models.Cliente;
import com.horizontes.models.Reservacion;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/reservaciones/*")
public class ReservacionServlet extends HttpServlet {

    private final ReservacionDAO dao = new ReservacionDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final Gson gson = new Gson();

    @Override
protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    PrintWriter out = res.getWriter();
    try {
        String pathInfo = req.getPathInfo();
        String clienteParam = req.getParameter("cliente");
        String diaParam = req.getParameter("dia");
        String fechaParam = req.getParameter("fecha");
        String destinoParam = req.getParameter("destino");

        if (pathInfo != null && pathInfo.length() > 1) {
            // Detalle con pasajeros
            int id = Integer.parseInt(pathInfo.substring(1));
            Reservacion r = dao.buscarPorId(id);
            if (r != null) {
                r.setPasajeros(dao.listarPasajeros(id));
                out.print(gson.toJson(r));
            } else {
                res.setStatus(404);
                out.print("{\"error\":\"Reservacion no encontrada\"}");
            }
        } else if (clienteParam != null) {
            out.print(gson.toJson(dao.listarPorCliente(Integer.parseInt(clienteParam))));
        } else if ("hoy".equals(diaParam)) {
            out.print(gson.toJson(dao.listarDelDia()));
        } else if (fechaParam != null || destinoParam != null) {
            // Busqueda por fecha y/o destino
            String fecha = (fechaParam != null && !fechaParam.isEmpty()) ? fechaParam : null;
            int destinoId = (destinoParam != null && !destinoParam.isEmpty()) ? Integer.parseInt(destinoParam) : 0;
            out.print(gson.toJson(dao.buscarPorFechaYDestino(fecha, destinoId)));
        } else {
            out.print(gson.toJson(dao.listar()));
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
            Reservacion r = gson.fromJson(req.getReader(), Reservacion.class);
            int id = dao.insertar(r);
            if (id > 0 && r.getPasajeros() != null) {
                for (Cliente pasajero : r.getPasajeros()) {
                    Cliente existente = clienteDAO.buscarPorDpi(pasajero.getDpi());
                    if (existente != null) {
                        dao.agregarPasajero(id, existente.getId());
                    }
                }
                out.print("{\"mensaje\":\"Reservacion creada correctamente\",\"id\":" + id + "}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo crear la reservacion\"}");
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
        Reservacion r = gson.fromJson(req.getReader(), Reservacion.class);
        boolean ok = dao.actualizarEstado(r.getId(), r.getEstado());
        if (ok) {
            out.print("{\"mensaje\":\"Estado actualizado correctamente\"}");
        } else {
            res.setStatus(500);
            out.print("{\"error\":\"No se pudo actualizar el estado\"}");
        }
    } catch (Exception e) {
        res.setStatus(500);
        out.print("{\"error\":\"" + e.getMessage() + "\"}");
    }
}
}