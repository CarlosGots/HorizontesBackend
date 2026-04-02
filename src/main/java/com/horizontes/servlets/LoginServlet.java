package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.dao.UsuarioDAO;
import com.horizontes.models.Usuario;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            Usuario body = gson.fromJson(req.getReader(), Usuario.class);
            Usuario usuario = dao.login(body.getNombre(), body.getPassword());
            if (usuario != null) {
                // Guardamos el usuario en sesión
                HttpSession sesion = req.getSession();
                sesion.setAttribute("usuario", usuario);
                // No enviamos el password al frontend
                usuario.setPassword(null);
                out.print(gson.toJson(usuario));
            } else {
                res.setStatus(401);
                out.print("{\"error\":\"Usuario o contraseña incorrectos\"}");
            }
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.getSession().invalidate();
        res.setContentType("application/json");
        res.getWriter().print("{\"mensaje\":\"Sesion cerrada\"}");
    }
}