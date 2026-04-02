package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.dao.UsuarioDAO;
import com.horizontes.models.Usuario;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/usuarios/*")
public class UsuarioServlet extends HttpServlet {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            List<Usuario> lista = dao.listar();
            // Limpiamos passwords antes de enviar
            lista.forEach(u -> u.setPassword(null));
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
            Usuario u = gson.fromJson(req.getReader(), Usuario.class);
            if (dao.existeNombre(u.getNombre())) {
                res.setStatus(400);
                out.print("{\"error\":\"El nombre de usuario ya existe\"}");
                return;
            }
            boolean ok = dao.insertar(u);
            if (ok) {
                out.print("{\"mensaje\":\"Usuario creado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo crear el usuario\"}");
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
            Usuario u = gson.fromJson(req.getReader(), Usuario.class);
            boolean ok = dao.actualizar(u);
            if (ok) {
                out.print("{\"mensaje\":\"Usuario actualizado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo actualizar el usuario\"}");
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
                out.print("{\"mensaje\":\"Usuario desactivado correctamente\"}");
            } else {
                res.setStatus(500);
                out.print("{\"error\":\"No se pudo desactivar el usuario\"}");
            }
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}