package com.horizontes.utils;

import com.horizontes.dao.*;
import com.horizontes.models.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.*;

/**
 * Clase encargada de leer y procesar el archivo .txt de carga inicial de datos.
 * Lee cada línea del archivo, identifica el tipo de instrucción y la ejecuta.
 * Al final retorna un resumen con los registros exitosos y los errores encontrados.
 */
public class ArchivoParser {

    /**
     * Procesa el archivo de entrada línea por línea.
     * Soporta los siguientes tipos de instrucción:
     * USUARIO, DESTINO, PROVEEDOR, PAQUETE, SERVICIO_PAQUETE, CLIENTE, RESERVACION, PAGO
     * 
     * @param stream archivo .txt recibido desde el frontend
     * @return mapa con el resumen: exitosos, errores_count y lista de errores
     */
    public static Map<String, Object> procesar(InputStream stream) {
        int exitosos = 0;
        List<String> errores = new ArrayList<>();

        // Creamos los DAOs necesarios para insertar cada tipo de dato
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

                // Ignoramos líneas vacías
                if (linea.isEmpty()) continue;

                try {
                    // Procesamos cada tipo de instrucción
                    if (linea.startsWith("USUARIO(")) {
                        procesarUsuario(linea, usuarioDAO);
                        exitosos++;

                    } else if (linea.startsWith("DESTINO(")) {
                        procesarDestino(linea, destinoDAO);
                        exitosos++;

                    } else if (linea.startsWith("PROVEEDOR(")) {
                        procesarProveedor(linea, proveedorDAO);
                        exitosos++;

                    } else if (linea.startsWith("PAQUETE(")) {
                        procesarPaquete(linea, paqueteDAO, destinoDAO);
                        exitosos++;

                    } else if (linea.startsWith("SERVICIO_PAQUETE(")) {
                        procesarServicioPaquete(linea, servicioDAO, paqueteDAO, proveedorDAO);
                        exitosos++;

                    } else if (linea.startsWith("CLIENTE(")) {
                        procesarCliente(linea, clienteDAO);
                        exitosos++;

                    } else if (linea.startsWith("RESERVACION(")) {
                        procesarReservacion(linea, reservacionDAO, paqueteDAO, usuarioDAO, clienteDAO);
                        exitosos++;

                    } else if (linea.startsWith("PAGO(")) {
                        procesarPago(linea, pagoDAO, reservacionDAO);
                        exitosos++;

                    } else {
                        // Instrucción desconocida
                        errores.add("Linea " + numeroLinea + ": instruccion desconocida");
                    }

                } catch (Exception e) {
                    // Guardamos el error con el número de línea para el reporte
                    errores.add("Linea " + numeroLinea + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errores.add("Error al leer el archivo: " + e.getMessage());
        }

        // Armamos el resumen final
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("exitosos", exitosos);
        resultado.put("errores_count", errores.size());
        resultado.put("errores", errores);
        return resultado;
    }

    // ─── Métodos privados para procesar cada tipo de instrucción ──────────────

    /** Registra un usuario en el sistema */
    private static void procesarUsuario(String linea, UsuarioDAO dao) throws Exception {
        String[] p = extraerParametros(linea);
        if (p.length < 3) throw new Exception("Formato incorrecto");
        if (p[1].length() < 6) throw new Exception("Password debe tener minimo 6 caracteres");
        if (dao.existeNombre(p[0])) throw new Exception("Usuario '" + p[0] + "' ya existe");
        Usuario u = new Usuario();
        u.setNombre(p[0]);
        u.setPassword(p[1]);
        u.setTipo(Integer.parseInt(p[2]));
        dao.insertar(u);
    }

    /** Registra un destino turístico */
    private static void procesarDestino(String linea, DestinoDAO dao) throws Exception {
        String[] p = extraerParametros(linea);
        if (p.length < 3) throw new Exception("Formato incorrecto");
        if (dao.buscarPorNombre(p[0]) != null) throw new Exception("Destino '" + p[0] + "' ya existe");
        Destino d = new Destino();
        d.setNombre(p[0]);
        d.setPais(p[1]);
        d.setDescripcion(p[2]);
        dao.insertar(d);
    }

    /** Registra un proveedor de servicios */
    private static void procesarProveedor(String linea, ProveedorDAO dao) throws Exception {
        String[] p = extraerParametros(linea);
        if (p.length < 3) throw new Exception("Formato incorrecto");
        if (dao.buscarPorNombre(p[0]) != null) throw new Exception("Proveedor '" + p[0] + "' ya existe");
        Proveedor pr = new Proveedor();
        pr.setNombre(p[0]);
        pr.setTipo(Integer.parseInt(p[1]));
        pr.setPais(p[2]);
        dao.insertar(pr);
    }

    /** Registra un paquete turístico — el destino debe existir previamente */
    private static void procesarPaquete(String linea, PaqueteDAO dao, DestinoDAO destinoDAO) throws Exception {
        String[] p = extraerParametros(linea);
        if (p.length < 5) throw new Exception("Formato incorrecto");
        Destino destino = destinoDAO.buscarPorNombre(p[1]);
        if (destino == null) throw new Exception("Destino '" + p[1] + "' no existe");
        if (dao.buscarPorNombre(p[0]) != null) throw new Exception("Paquete '" + p[0] + "' ya existe");
        Paquete pq = new Paquete();
        pq.setNombre(p[0]);
        pq.setDestinoId(destino.getId());
        pq.setDuracion(Integer.parseInt(p[2]));
        pq.setPrecioVenta(Double.parseDouble(p[3]));
        pq.setCapacidad(Integer.parseInt(p[4]));
        dao.insertar(pq);
    }

    /** Asigna un servicio a un paquete — el paquete y proveedor deben existir */
    private static void procesarServicioPaquete(String linea, ServicioPaqueteDAO dao,
            PaqueteDAO paqueteDAO, ProveedorDAO proveedorDAO) throws Exception {
        String[] p = extraerParametros(linea);
        if (p.length < 4) throw new Exception("Formato incorrecto");
        Paquete pq = paqueteDAO.buscarPorNombre(p[0]);
        if (pq == null) throw new Exception("Paquete '" + p[0] + "' no existe");
        Proveedor pr = proveedorDAO.buscarPorNombre(p[1]);
        if (pr == null) throw new Exception("Proveedor '" + p[1] + "' no existe");
        ServicioPaquete sp = new ServicioPaquete();
        sp.setPaqueteId(pq.getId());
        sp.setProveedorId(pr.getId());
        sp.setDescripcion(p[2]);
        sp.setCosto(Double.parseDouble(p[3]));
        dao.insertar(sp);
    }

    /** Registra un cliente — el DPI debe ser único */
    private static void procesarCliente(String linea, ClienteDAO dao) throws Exception {
        String[] p = extraerParametros(linea);
        if (p.length < 6) throw new Exception("Formato incorrecto");
        if (dao.buscarPorDpi(p[0]) != null) throw new Exception("Cliente con DPI '" + p[0] + "' ya existe");
        // Convertimos la fecha de dd/MM/yyyy a yyyy-MM-dd para MySQL
        String[] fechaParts = p[2].split("/");
        Cliente c = new Cliente();
        c.setDpi(p[0]);
        c.setNombre(p[1]);
        c.setFechaNacimiento(fechaParts[2] + "-" + fechaParts[1] + "-" + fechaParts[0]);
        c.setTelefono(p[3]);
        c.setEmail(p[4]);
        c.setNacionalidad(p[5]);
        dao.insertar(c);
    }

    /** Registra una reservación con sus pasajeros */
    private static void procesarReservacion(String linea, ReservacionDAO dao,
            PaqueteDAO paqueteDAO, UsuarioDAO usuarioDAO, ClienteDAO clienteDAO) throws Exception {
        String[] p = extraerParametros(linea);
        if (p.length < 4) throw new Exception("Formato incorrecto");

        // Verificamos que el paquete y el agente existan
        Paquete pq = paqueteDAO.buscarPorNombre(p[0]);
        if (pq == null) throw new Exception("Paquete '" + p[0] + "' no existe");

        Usuario agente = null;
        for (Usuario u : usuarioDAO.listar()) {
            if (u.getNombre().equals(p[1])) { agente = u; break; }
        }
        if (agente == null) throw new Exception("Usuario '" + p[1] + "' no existe");

        // Convertimos la fecha de dd/MM/yyyy a yyyy-MM-dd
        String[] fechaParts = p[2].split("/");
        String fechaViaje = fechaParts[2] + "-" + fechaParts[1] + "-" + fechaParts[0];

        Reservacion r = new Reservacion();
        r.setPaqueteId(pq.getId());
        r.setAgenteId(agente.getId());
        r.setFechaViaje(fechaViaje);
        r.setCostoTotal(pq.getPrecioVenta());
        int resId = dao.insertar(r);

        // Agregamos cada pasajero a la reservación
        String[] dpis = p[3].split("\\|");
        for (String dpi : dpis) {
            Cliente c = clienteDAO.buscarPorDpi(dpi.trim());
            if (c == null) throw new Exception("Cliente con DPI '" + dpi + "' no existe");
            dao.agregarPasajero(resId, c.getId());
        }
    }

    /** Registra un pago sobre una reservación existente */
    private static void procesarPago(String linea, PagoDAO dao,
            ReservacionDAO reservacionDAO) throws Exception {
        String[] p = extraerParametros(linea);
        if (p.length < 4) throw new Exception("Formato incorrecto");

        Reservacion r = reservacionDAO.buscarPorNumero(p[0]);
        if (r == null) throw new Exception("Reservacion '" + p[0] + "' no existe");

        // Convertimos la fecha de dd/MM/yyyy a yyyy-MM-dd
        String[] fechaParts = p[3].split("/");
        String fecha = fechaParts[2] + "-" + fechaParts[1] + "-" + fechaParts[0];

        Pago pago = new Pago();
        pago.setReservacionId(r.getId());
        pago.setMonto(Double.parseDouble(p[1]));
        pago.setMetodo(Integer.parseInt(p[2]));
        pago.setFecha(fecha);
        dao.insertar(pago);

        // Si el pago cubre el total, confirmamos la reservación automáticamente
        double totalPagado = reservacionDAO.getTotalPagado(r.getId());
        if (totalPagado >= r.getCostoTotal()) {
            reservacionDAO.actualizarEstado(r.getId(), "CONFIRMADA");
        }
    }

    /**
     * Extrae los parámetros de una instrucción del archivo.
     * Por ejemplo: USUARIO("jperez","miPass",1) → ["jperez", "miPass", "1"]
     * Respeta las comillas para no separar textos con comas adentro.
     */
    private static String[] extraerParametros(String linea) {
        List<String> params = new ArrayList<>();
        int inicio = linea.indexOf('(');
        int fin = linea.lastIndexOf(')');
        if (inicio < 0 || fin < 0) return new String[0];

        String contenido = linea.substring(inicio + 1, fin);

        // Separamos por comas respetando lo que está entre comillas
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