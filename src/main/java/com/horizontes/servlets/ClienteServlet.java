package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.dao.ClienteDAO;
import com.horizontes.models.Cliente;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/clientes/*")
public class ClienteServlet extends HttpServlet {

    private final ClienteDAO dao = new ClienteDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            String dpiParam = req.getParameter("dpi");
            if (dpiParam != null) {
                // GET /api/clientes?dpi=123 — buscar por DPI
                Cliente c = dao.buscarPorDpi(dpiParam);
                if (c != null) {
                    out.print(gson.toJson(c));
                } else {
                    res.setStatus(404);
                    out.print("{\"error\":\"Cliente no encontrado\"}");
                }
            } else {
                List<Cliente> lista = dao.listar();
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
            Cliente c = gson.fromJson(req.getReader(), Cliente.class);
            // Verificar si ya existe
            if (dao.buscarPorDpi(c.getDpi()) != null) {
                res.setStatus(400);
                out.print("{\"error\":\"Ya existe un cliente con ese DPI\"}");
                return;
            }
            boolean ok = dao.insertar(c);
            if (ok) {
                out.print("{\"mensaje\":\"Cliente registrado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo registrar el cliente\"}");
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
            Cliente c = gson.fromJson(req.getReader(), Cliente.class);
            boolean ok = dao.actualizar(c);
            if (ok) {
                out.print("{\"mensaje\":\"Cliente actualizado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo actualizar el cliente\"}");
            }
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}