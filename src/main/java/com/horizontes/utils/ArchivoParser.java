package com.horizontes.utils;

import com.horizontes.dao.*;
import com.horizontes.models.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.*;

public class ArchivoParser {

    public static Map<String, Object> procesar(InputStream stream) {
        int exitosos = 0;
        List<String> errores = new ArrayList<>();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        DestinoDAO destinoDAO = new DestinoDAO();
        ProveedorDAO proveedorDAO = new ProveedorDAO();
        PaqueteDAO paqueteDAO = new PaqueteDAO();
        ServicioPaqueteDAO servicioDAO = new ServicioPaqueteDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        ReservacionDAO reservacionDAO = new ReservacionDAO();
        PagoDAO pagoDAO = new PagoDAO();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String linea;
            int numeroLinea = 0;

            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                try {
                    if (linea.startsWith("USUARIO(")) {
                        String[] p = extraerParametros(linea);
                        if (p.length < 3) throw new Exception("Formato incorrecto");
                        if (p[1].length() < 6) throw new Exception("Password debe tener minimo 6 caracteres");
                        Usuario u = new Usuario();
                        u.setNombre(p[0]);
                        u.setPassword(p[1]);
                        u.setTipo(Integer.parseInt(p[2]));
                        if (usuarioDAO.existeNombre(u.getNombre()))
                            throw new Exception("Usuario '" + u.getNombre() + "' ya existe");
                        usuarioDAO.insertar(u);
                        exitosos++;

                    } else if (linea.startsWith("DESTINO(")) {
                        String[] p = extraerParametros(linea);
                        if (p.length < 3) throw new Exception("Formato incorrecto");
                        Destino d = new Destino();
                        d.setNombre(p[0]);
                        d.setPais(p[1]);
                        d.setDescripcion(p[2]);
                        if (destinoDAO.buscarPorNombre(d.getNombre()) != null)
                            throw new Exception("Destino '" + d.getNombre() + "' ya existe");
                        destinoDAO.insertar(d);
                        exitosos++;

                    } else if (linea.startsWith("PROVEEDOR(")) {
                        String[] p = extraerParametros(linea);
                        if (p.length < 3) throw new Exception("Formato incorrecto");
                        Proveedor pr = new Proveedor();
                        pr.setNombre(p[0]);
                        pr.setTipo(Integer.parseInt(p[1]));
                        pr.setPais(p[2]);
                        if (proveedorDAO.buscarPorNombre(pr.getNombre()) != null)
                            throw new Exception("Proveedor '" + pr.getNombre() + "' ya existe");
                        proveedorDAO.insertar(pr);
                        exitosos++;

                    } else if (linea.startsWith("PAQUETE(")) {
                        String[] p = extraerParametros(linea);
                        if (p.length < 5) throw new Exception("Formato incorrecto");
                        Destino destino = destinoDAO.buscarPorNombre(p[1]);
                        if (destino == null)
                            throw new Exception("Destino '" + p[1] + "' no existe");
                        Paquete pq = new Paquete();
                        pq.setNombre(p[0]);
                        pq.setDestinoId(destino.getId());
                        pq.setDuracion(Integer.parseInt(p[2]));
                        pq.setPrecioVenta(Double.parseDouble(p[3]));
                        pq.setCapacidad(Integer.parseInt(p[4]));
                        if (paqueteDAO.buscarPorNombre(pq.getNombre()) != null)
                            throw new Exception("Paquete '" + pq.getNombre() + "' ya existe");
                        paqueteDAO.insertar(pq);
                        exitosos++;

                    } else if (linea.startsWith("SERVICIO_PAQUETE(")) {
                        String[] p = extraerParametros(linea);
                        if (p.length < 4) throw new Exception("Formato incorrecto");
                        Paquete pq = paqueteDAO.buscarPorNombre(p[0]);
                        if (pq == null)
                            throw new Exception("Paquete '" + p[0] + "' no existe");
                        Proveedor pr = proveedorDAO.buscarPorNombre(p[1]);
                        if (pr == null)
                            throw new Exception("Proveedor '" + p[1] + "' no existe");
                        ServicioPaquete sp = new ServicioPaquete();
                        sp.setPaqueteId(pq.getId());
                        sp.setProveedorId(pr.getId());
                        sp.setDescripcion(p[2]);
                        sp.setCosto(Double.parseDouble(p[3]));
                        servicioDAO.insertar(sp);
                        exitosos++;

                    } else if (linea.startsWith("CLIENTE(")) {
                        String[] p = extraerParametros(linea);
                        if (p.length < 6) throw new Exception("Formato incorrecto");
                        if (clienteDAO.buscarPorDpi(p[0]) != null)
                            throw new Exception("Cliente con DPI '" + p[0] + "' ya existe");
                        Cliente c = new Cliente();
                        c.setDpi(p[0]);
                        c.setNombre(p[1]);
                        // Convertir fecha de dd/MM/yyyy a yyyy-MM-dd para MySQL
                        String[] fechaParts = p[2].split("/");
                        c.setFechaNacimiento(fechaParts[2] + "-" + fechaParts[1] + "-" + fechaParts[0]);
                        c.setTelefono(p[3]);
                        c.setEmail(p[4]);
                        c.setNacionalidad(p[5]);
                        clienteDAO.insertar(c);
                        exitosos++;

                    } else if (linea.startsWith("RESERVACION(")) {
                        String[] p = extraerParametros(linea);
                        if (p.length < 4) throw new Exception("Formato incorrecto");
                        Paquete pq = paqueteDAO.buscarPorNombre(p[0]);
                        if (pq == null)
                            throw new Exception("Paquete '" + p[0] + "' no existe");
                        Usuario agente = null;
                        // Buscar agente por nombre
                        for (Usuario u : usuarioDAO.listar()) {
                            if (u.getNombre().equals(p[1])) { agente = u; break; }
                        }
                        if (agente == null)
                            throw new Exception("Usuario '" + p[1] + "' no existe");
                        String[] fechaParts = p[2].split("/");
                        String fechaViaje = fechaParts[2] + "-" + fechaParts[1] + "-" + fechaParts[0];
                        Reservacion r = new Reservacion();
                        r.setPaqueteId(pq.getId());
                        r.setAgenteId(agente.getId());
                        r.setFechaViaje(fechaViaje);
                        r.setCostoTotal(pq.getPrecioVenta());
                        int resId = reservacionDAO.insertar(r);
                        // Agregar pasajeros
                        String[] dpis = p[3].split("\\|");
                        for (String dpi : dpis) {
                            Cliente c = clienteDAO.buscarPorDpi(dpi.trim());
                            if (c == null)
                                throw new Exception("Cliente con DPI '" + dpi + "' no existe");
                            reservacionDAO.agregarPasajero(resId, c.getId());
                        }
                        exitosos++;

                    } else if (linea.startsWith("PAGO(")) {
                        String[] p = extraerParametros(linea);
                        if (p.length < 4) throw new Exception("Formato incorrecto");
                        Reservacion r = reservacionDAO.buscarPorNumero(p[0]);
                        if (r == null)
                            throw new Exception("Reservacion '" + p[0] + "' no existe");
                        String[] fechaParts = p[3].split("/");
                        String fecha = fechaParts[2] + "-" + fechaParts[1] + "-" + fechaParts[0];
                        Pago pago = new Pago();
                        pago.setReservacionId(r.getId());
                        pago.setMonto(Double.parseDouble(p[1]));
                        pago.setMetodo(Integer.parseInt(p[2]));
                        pago.setFecha(fecha);
                        pagoDAO.insertar(pago);
                        // Verificar si quedó confirmada
                        double totalPagado = reservacionDAO.getTotalPagado(r.getId());
                        if (totalPagado >= r.getCostoTotal()) {
                            reservacionDAO.actualizarEstado(r.getId(), "CONFIRMADA");
                        }
                        exitosos++;

                    } else {
                        errores.add("Linea " + numeroLinea + ": instruccion desconocida");
                    }

                } catch (Exception e) {
                    errores.add("Linea " + numeroLinea + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errores.add("Error al leer el archivo: " + e.getMessage());
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("exitosos", exitosos);
        resultado.put("errores_count", errores.size());
        resultado.put("errores", errores);
        return resultado;
    }

    /**
     * Extrae los parámetros de una instrucción como:
     * USUARIO("jperez","miPass",1) → ["jperez", "miPass", "1"]
     */
    private static String[] extraerParametros(String linea) {
        List<String> params = new ArrayList<>();
        // Buscar el contenido entre los primeros paréntesis
        int inicio = linea.indexOf('(');
        int fin = linea.lastIndexOf(')');
        if (inicio < 0 || fin < 0) return new String[0];

        String contenido = linea.substring(inicio + 1, fin);

        // Separar respetando comillas
        Pattern patron = Pattern.compile("\"([^\"]*)\"|([^,]+)");
        Matcher matcher = patron.matcher(contenido);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                params.add(matcher.group(1).trim());
            } else {
                params.add(matcher.group(2).trim());
            }
        }
        return params.toArray(new String[0]);
    }
}