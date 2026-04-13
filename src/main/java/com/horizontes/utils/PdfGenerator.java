package com.horizontes.utils;

import com.horizontes.models.Cancelacion;
import com.horizontes.models.Pago;
import com.horizontes.models.Reservacion;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

public class PdfGenerator {

    private static final Font TITULO = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font SUBTITULO = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
    private static final Font NORMAL = new Font(Font.FontFamily.HELVETICA, 11);
    private static final Font NEGRITA = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

    // ─── Comprobante de pago ───────────────────────────────────────────────────

    public static byte[] generarComprobantePago(Reservacion r, List<Pago> pagos) throws Exception {
        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        // Encabezado
        doc.add(new Paragraph("Horizontes sin Limites", TITULO));
        doc.add(new Paragraph("Agencia de Viajes", NORMAL));
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("COMPROBANTE DE PAGO", SUBTITULO));
        doc.add(Chunk.NEWLINE);

        // Datos de la reservación
        doc.add(new Paragraph("Reservacion: " + r.getNumero(), NEGRITA));
        doc.add(new Paragraph("Paquete: " + r.getPaqueteNombre(), NORMAL));
        doc.add(new Paragraph("Fecha de viaje: " + r.getFechaViaje(), NORMAL));
        doc.add(new Paragraph("Agente: " + r.getAgenteNombre(), NORMAL));
        doc.add(new Paragraph("Estado: " + r.getEstado(), NORMAL));
        doc.add(Chunk.NEWLINE);

        // Tabla de pagos
        doc.add(new Paragraph("Detalle de pagos:", SUBTITULO));
        doc.add(Chunk.NEWLINE);

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.addCell(celda("Fecha", true));
        tabla.addCell(celda("Metodo", true));
        tabla.addCell(celda("Monto", true));

        double totalPagado = 0;
        for (Pago p : pagos) {
            tabla.addCell(celda(p.getFecha(), false));
            tabla.addCell(celda(metodoPago(p.getMetodo()), false));
            tabla.addCell(celda("Q. " + String.format("%.2f", p.getMonto()), false));
            totalPagado += p.getMonto();
        }
        doc.add(tabla);
        doc.add(Chunk.NEWLINE);

        doc.add(new Paragraph("Costo total: Q. " + String.format("%.2f", r.getCostoTotal()), NEGRITA));
        doc.add(new Paragraph("Total pagado: Q. " + String.format("%.2f", totalPagado), NEGRITA));

        doc.close();
        return out.toByteArray();
    }

    // ─── Reporte de ventas ────────────────────────────────────────────────────

    public static byte[] generarReporteVentas(List<Reservacion> reservaciones) throws Exception {
        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        doc.add(new Paragraph("Horizontes sin Limites", TITULO));
        doc.add(new Paragraph("REPORTE DE VENTAS", SUBTITULO));
        doc.add(Chunk.NEWLINE);

        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.addCell(celda("Numero", true));
        tabla.addCell(celda("Paquete", true));
        tabla.addCell(celda("Agente", true));
        tabla.addCell(celda("Fecha viaje", true));
        tabla.addCell(celda("Monto", true));

        double totalGeneral = 0;
        for (Reservacion r : reservaciones) {
            if ("CONFIRMADA".equals(r.getEstado()) || "COMPLETADA".equals(r.getEstado())) {
                tabla.addCell(celda(r.getNumero(), false));
                tabla.addCell(celda(r.getPaqueteNombre(), false));
                tabla.addCell(celda(r.getAgenteNombre(), false));
                tabla.addCell(celda(r.getFechaViaje(), false));
                tabla.addCell(celda("Q. " + String.format("%.2f", r.getCostoTotal()), false));
                totalGeneral += r.getCostoTotal();
            }
        }
        doc.add(tabla);
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("TOTAL: Q. " + String.format("%.2f", totalGeneral), NEGRITA));

        doc.close();
        return out.toByteArray();
    }

    // ─── Reporte de cancelaciones ─────────────────────────────────────────────

    public static byte[] generarReporteCancelaciones(List<Cancelacion> cancelaciones) throws Exception {
        Document doc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        doc.add(new Paragraph("Horizontes sin Limites", TITULO));
        doc.add(new Paragraph("REPORTE DE CANCELACIONES", SUBTITULO));
        doc.add(Chunk.NEWLINE);

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.addCell(celda("Reservacion", true));
        tabla.addCell(celda("Fecha cancelacion", true));
        tabla.addCell(celda("% Reembolso", true));
        tabla.addCell(celda("Monto reembolsado", true));

        double totalReembolsos = 0;
        for (Cancelacion c : cancelaciones) {
            tabla.addCell(celda(c.getReservacionNumero(), false));
            tabla.addCell(celda(c.getFechaCancelacion(), false));
            tabla.addCell(celda(c.getPorcentajeReembolso() + "%", false));
            tabla.addCell(celda("Q. " + String.format("%.2f", c.getMontoReembolso()), false));
            totalReembolsos += c.getMontoReembolso();
        }
        doc.add(tabla);
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("TOTAL REEMBOLSADO: Q. " + String.format("%.2f", totalReembolsos), NEGRITA));

        doc.close();
        return out.toByteArray();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static PdfPCell celda(String texto, boolean esEncabezado) {
        Font fuente = esEncabezado ? NEGRITA : NORMAL;
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setPadding(5);
        if (esEncabezado) {
            celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
        }
        return celda;
    }

    private static String metodoPago(int metodo) {
        return switch (metodo) {
            case 1 -> "Efectivo";
            case 2 -> "Tarjeta";
            case 3 -> "Transferencia";
            default -> "Desconocido";
        };
    }
    
    // Reporte de ganancias en PDF
public static byte[] generarReporteGanancias(Map<String, Object> datos) throws Exception {
    Document doc = new Document();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PdfWriter.getInstance(doc, out);
    doc.open();

    doc.add(new Paragraph("Horizontes sin Limites", TITULO));
    doc.add(new Paragraph("REPORTE DE GANANCIAS", SUBTITULO));
    doc.add(Chunk.NEWLINE);

    doc.add(new Paragraph("Ganancias brutas: Q. " + 
        String.format("%.2f", datos.get("gananciasBrutas")), NEGRITA));
    doc.add(new Paragraph("Total reembolsos: Q. " + 
        String.format("%.2f", datos.get("totalReembolsos")), NORMAL));
    doc.add(new Paragraph("Ganancia neta: Q. " + 
        String.format("%.2f", datos.get("gananciaNeta")), NEGRITA));

    doc.close();
    return out.toByteArray();
}

// Reporte de agentes en PDF
public static byte[] generarReporteAgentes(Map<String, Object> datos, String titulo) throws Exception {
    Document doc = new Document();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PdfWriter.getInstance(doc, out);
    doc.open();

    doc.add(new Paragraph("Horizontes sin Limites", TITULO));
    doc.add(new Paragraph(titulo, SUBTITULO));
    doc.add(Chunk.NEWLINE);

    List<Map<String, Object>> agentes = (List<Map<String, Object>>) datos.get("agentes");
    PdfPTable tabla = new PdfPTable(2);
    tabla.setWidthPercentage(100);
    tabla.addCell(celda("Agente", true));
    tabla.addCell(celda("Total", true));

    for (Map<String, Object> a : agentes) {
        tabla.addCell(celda(a.get("nombre").toString(), false));
        if (a.containsKey("montoTotal")) {
            tabla.addCell(celda("Q. " + String.format("%.2f", 
                ((Number) a.get("montoTotal")).doubleValue()), false));
        } else {
            tabla.addCell(celda("Q. " + String.format("%.2f", 
                ((Number) a.get("gananciaTotal")).doubleValue()), false));
        }
    }
    doc.add(tabla);
    doc.close();
    return out.toByteArray();
}

// Reporte de paquetes por ventas en PDF
public static byte[] generarReportePaquetes(List<Map<String, Object>> lista) throws Exception {
    Document doc = new Document();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PdfWriter.getInstance(doc, out);
    doc.open();

    doc.add(new Paragraph("Horizontes sin Limites", TITULO));
    doc.add(new Paragraph("REPORTE DE PAQUETES POR VENTAS", SUBTITULO));
    doc.add(Chunk.NEWLINE);

    PdfPTable tabla = new PdfPTable(3);
    tabla.setWidthPercentage(100);
    tabla.addCell(celda("Paquete", true));
    tabla.addCell(celda("Total ventas", true));
    tabla.addCell(celda("Monto total", true));

    for (int i = 0; i < lista.size(); i++) {
        Map<String, Object> p = lista.get(i);
        String nombre = p.get("nombre").toString();
        if (i == 0) nombre += " [MAS VENDIDO]";
        if (i == lista.size() - 1 && lista.size() > 1) nombre += " [MENOS VENDIDO]";
        tabla.addCell(celda(nombre, false));
        tabla.addCell(celda(p.get("totalVentas").toString(), false));
        tabla.addCell(celda("Q. " + String.format("%.2f", 
            ((Number) p.get("montoTotal")).doubleValue()), false));
    }
    doc.add(tabla);
    doc.close();
    return out.toByteArray();
}

// Reporte de ocupacion por destino en PDF
public static byte[] generarReporteOcupacion(List<Map<String, Object>> lista) throws Exception {
    Document doc = new Document();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PdfWriter.getInstance(doc, out);
    doc.open();

    doc.add(new Paragraph("Horizontes sin Limites", TITULO));
    doc.add(new Paragraph("REPORTE DE OCUPACION POR DESTINO", SUBTITULO));
    doc.add(Chunk.NEWLINE);

    PdfPTable tabla = new PdfPTable(2);
    tabla.setWidthPercentage(100);
    tabla.addCell(celda("Destino", true));
    tabla.addCell(celda("Total reservaciones", true));

    for (Map<String, Object> d : lista) {
        tabla.addCell(celda(d.get("destino").toString(), false));
        tabla.addCell(celda(d.get("totalReservaciones").toString(), false));
    }
    doc.add(tabla);
    doc.close();
    return out.toByteArray();
}
}