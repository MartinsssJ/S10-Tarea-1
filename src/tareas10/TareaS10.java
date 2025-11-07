package tareas10;

import java.util.ArrayList;
import java.util.List;

class Pedido{
    private String cliente;
    private String producto;
    private int cantidad;
    private double subtotal;
    private double igv;
    private double total;
}

class PedidoRepository{
    private List<Pedido> almacen;
}


class LegacyBillingSystem {
    public void generateLegacyInvoice(double total, String cliente) {
        System.out.println("-----[LegacyBillingSystem]-----");
        System.out.println("Factura generada para: " + cliente);
        System.out.println("Total: S/ " + total);
    }
}

interface FacturaService {
    void generarFactura(String cliente, String producto, double total);
}

class FacturaAdapter implements FacturaService {
    private LegacyBillingSystem legacySystem;

    public FacturaAdapter(LegacyBillingSystem legacySystem) {
        this.legacySystem = legacySystem;
    }
    
    @Override
    public void generarFactura(String cliente, String producto, double total){
        legacySystem.generateLegacyInvoice(total, cliente);
    }
}

class ServicioStock{
    public boolean validarStock(String producto, int cantidad){
        return cantidad>0 && cantidad<=15;
    }
}

class ServicioImpuesto{
    public double calcularIGV(double subtotal) {
        return subtotal*0.18;
    }
}

class ServicioPedido{
    public void registrarPedido(String cliente, String producto, int cantidad){
        System.out.println("Pedido registrado correctamente para " + cliente + ".");
    }
}

class PedidoFacade {
    
    private ServicioStock serviciostock;
    private ServicioImpuesto servicioimpuesto;        
    private ServicioPedido serviciopedido;
    private FacturaService facturaservice;

    public PedidoFacade() {
        this.serviciostock = new ServicioStock();
        this.servicioimpuesto = new ServicioImpuesto();
        this.serviciopedido = new ServicioPedido();
        this.facturaservice = new FacturaAdapter(new LegacyBillingSystem());
    }
    
    public void procesarPedido(String cliente, String producto, int cantidad, double precioUnitario) {
        System.out.println("----- Procesando pedido -----");

        if (!serviciostock.validarStock(producto, cantidad)) {
            System.out.println("Error: sin stock disponible.");
            return;
        }

        double subtotal=precioUnitario*cantidad;
        double igv=servicioimpuesto.calcularIGV(subtotal);
        double total=subtotal+igv;

        serviciopedido.registrarPedido(cliente, producto, cantidad);

        System.out.println("----- COMPROBANTE DE COMPRA -----");
        System.out.println("Cliente: " + cliente);
        System.out.println("Producto: " + producto);
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Subtotal: S/ " + subtotal);
        System.out.println("IGV: S/ " + igv);
        System.out.println("Total: S/ " + total);
        
        facturaservice.generarFactura(cliente, producto, total);
    }
}

public class TareaS10 {
    public static void main(String[] args) {
        
        PedidoFacade p = new PedidoFacade();
        p.procesarPedido("Martin Jaime", "La play 4", 2, 1500.0);
        
    }
}
