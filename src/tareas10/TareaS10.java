package tareas10;

public class TareaS10 {
    public static void main(String[] args) {
        String cliente="Juan Pérez";
        String producto="Laptop";
        int cantidad=2;
        double precioUnitario=2500.0;

        if (cantidad<=0){
            System.out.println("Error: Cantidad inválida.");
            return;
        }

        double subtotal=precioUnitario*cantidad;
        double igv=subtotal*0.18;
        double total=subtotal+ igv;

        System.out.println("=== Pedido ===");
        System.out.println("Cliente: " + cliente);
        System.out.println("Producto: " + producto);
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Subtotal: S/ " + subtotal);
        System.out.println("IGV (18%): S/ " + igv);
        System.out.println("Total: S/ " + total);
    }
    
}
