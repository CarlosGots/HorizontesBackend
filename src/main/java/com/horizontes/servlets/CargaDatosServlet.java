package com.horizontes.servlets;

import com.google.gson.Gson;
import com.horizontes.utils.ArchivoParser;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/api/carga")
@MultipartConfig
public class CargaDatosServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        try {
            Part archivo = req.getPart("archivo");
            if (archivo == null) {
                res.setStatus(400);
                out.print("{\"error\":\"No se recibio ningun archivo\"}");
                return;
            }
            InputStream stream = archivo.getInputStream();
            Map<String, Object> resultado = ArchivoParser.procesar(stream);
            out.print(gson.toJson(resultado));
        } catch (Exception e) {
            res.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}