package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.dao.DestinoDAO;
import com.horizontes.models.Destino;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/destinos/*")
public class DestinoServlet extends HttpServlet {

    private final DestinoDAO dao = new DestinoDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            List<Destino> lista = dao.listar();
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
            Destino d = gson.fromJson(req.getReader(), Destino.class);
            boolean ok = dao.insertar(d);
            if (ok) {
                out.print("{\"mensaje\":\"Destino creado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo crear el destino\"}");
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
            Destino d = gson.fromJson(req.getReader(), Destino.class);
            boolean ok = dao.actualizar(d);
            if (ok) {
                out.print("{\"mensaje\":\"Destino actualizado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo actualizar el destino\"}");
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
            boolean ok = dao.eliminar(id);
            if (ok) {
                out.print("{\"mensaje\":\"Destino eliminado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo eliminar el destino\"}");
            }
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}