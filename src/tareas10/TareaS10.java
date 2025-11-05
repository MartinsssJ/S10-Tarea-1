package tareas10;

class LegacyBillingSystem {
    public void generateLegacyInvoice(String cliente, String producto, double total) {
        System.out.println("[LegacyBillingSystem] Factura antigua generada para " + cliente);
    }
}

interface FacturaService {
    void generarFactura(String cliente, String producto, double total);
}

class FacturaAdapter implements FacturaService {
    
    @Override
    public void generarFactura(String cliente, String producto, double total){
    }
}

class servicioStock{
    public boolean validarStock(String producto, int cantidad){
        return cantidad>0 && cantidad<=15;
    }
}

class servicioImpuesto{
    public double calcularIGV(double subtotal) {
        return subtotal*0.18;
    }
}

class servicioPedido{
    public void registrarPedido(String cliente, String producto, int cantidad){
        System.out.println("Pedido registrado correctamente para " + cliente + ".");
    }
}

class PedidoFacade {
    private servicioStock stock = new servicioStock();
    private servicioImpuesto impuesto = new servicioImpuesto();
    private servicioPedido pedido = new servicioPedido();

    public void procesarPedido(String cliente, String producto, int cantidad, double precioUnitario) {
        System.out.println("----- Procesando pedido -----");

        if (!stock.validarStock(producto, cantidad)) {
            System.out.println("Error: sin stock disponible.");
            return;
        }

        double subtotal=precioUnitario*cantidad;
        double igv=impuesto.calcularIGV(subtotal);
        double total=subtotal+igv;

        pedido.registrarPedido(cliente, producto, cantidad);

        System.out.println("----- COMPROBANTE DE COMPRA -----");
        System.out.println("Cliente: " + cliente);
        System.out.println("Producto: " + producto);
        System.out.println("Subtotal: S/ " + subtotal);
        System.out.println("IGV: S/ " + igv);
        System.out.println("Total: S/ " + total);
    }
}

public class TareaS10 {
    public static void main(String[] args) {
        
        PedidoFacade pedidoFacade = new PedidoFacade();
        pedidoFacade.procesarPedido("Martincito tu terror", "La play 4", 2, 1500.0);
        
    }
}
