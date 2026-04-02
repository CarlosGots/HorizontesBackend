package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.dao.ProveedorDAO;
import com.horizontes.models.Proveedor;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/proveedores/*")
public class ProveedorServlet extends HttpServlet {

    private final ProveedorDAO dao = new ProveedorDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            List<Proveedor> lista = dao.listar();
            out.print(gson.toJson(lista));
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
            Proveedor p = gson.fromJson(req.getReader(), Proveedor.class);
            boolean ok = dao.insertar(p);
            if (ok) {
                out.print("{\"mensaje\":\"Proveedor creado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo crear el proveedor\"}");
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
            Proveedor p = gson.fromJson(req.getReader(), Proveedor.class);
            boolean ok = dao.actualizar(p);
            if (ok) {
                out.print("{\"mensaje\":\"Proveedor actualizado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo actualizar el proveedor\"}");
            }
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}