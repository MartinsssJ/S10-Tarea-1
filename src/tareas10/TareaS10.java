package tareas10;

class servicioStock{
    public boolean validarStock(String producto, int cantidad){
        return cantidad>0 && cantidad<=10;
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

public class TareaS10 {
    public static void main(String[] args) {
        
        servicioStock stock = new servicioStock();
        servicioImpuesto impuesto = new servicioImpuesto();
        servicioPedido pedido = new servicioPedido();
        
        String cliente="Juan Pérez";
        String producto="Laptop";
        int cantidad=2;
        double precioUnitario=2500.0;

        if (!stock.validarStock(producto, cantidad)){
            System.out.println("Error: sin stock.");
            return;
        }

        double subtotal=precioUnitario*cantidad;
        double igv=subtotal*0.18;
        double total=subtotal+ igv;
        
        pedido.registrarPedido(cliente, producto, cantidad);

        System.out.println("=== Pedido ===");
        System.out.println("Cliente: " + cliente);
        System.out.println("Producto: " + producto);
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Subtotal: S/ " + subtotal);
        System.out.println("IGV (18%): S/ " + igv);
        System.out.println("Total: S/ " + total);
    }
    
}
