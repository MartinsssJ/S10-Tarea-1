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

    public Pedido(String cliente, String producto, int cantidad, double subtotal, double igv, double total){
        this.cliente = cliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
    }
    
    @Override
    public String toString() {
        return "Pedido{" + "cliente=" + cliente + ", producto=" + producto + ", cantidad=" + cantidad + ", subtotal=" + subtotal + ", igv=" + igv + ", total=" + total + '}';
    }  
}

class PedidoRepository{
    private List<Pedido> almacen;
    
    public PedidoRepository(){
        this.almacen = new ArrayList<>();
    }
    
    public void save(Pedido pedido) {
        almacen.add(pedido);
    }

    public List<Pedido> findAll(){
        return new ArrayList<>(almacen); 
    }
}

class LegacyBillingSystem {
    public void generateLegacyInvoice(double total, String cliente) {
        System.out.println("-----[LegacyBillingSystem]-----");
        System.out.println("Factura generada para: " + cliente);
        System.out.println("Total: S/ " + total);
        System.out.println();
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

interface ImpuestoStrategy{
    double calcular(double subtotal);
}

class IGV18Strategy implements ImpuestoStrategy{
    @Override
    public double calcular(double subtotal){
        return 0.18*subtotal;
    }
}

class ExoneradoStrategy implements ImpuestoStrategy{
    @Override
    public double calcular(double subtotal){
        return 0;
    }
}

class ServicioPedido{
    private PedidoRepository repository;
    
    public ServicioPedido(PedidoRepository repository) {
        this.repository = repository;
    }
    
    public void registrarPedido(Pedido pedido){
        repository.save(pedido);
    }
}

class PedidoFacade {
    
    private ServicioStock serviciostock;       
    private ServicioPedido serviciopedido;
    private FacturaService facturaservice;
    private ImpuestoStrategy impuestostrategy;
    private PedidoRepository pedidorepository;

    public PedidoFacade() {
        this.pedidorepository = new PedidoRepository();
        this.serviciostock = new ServicioStock();
        this.serviciopedido = new ServicioPedido(pedidorepository);
        this.facturaservice = new FacturaAdapter(new LegacyBillingSystem());
    }
    
    public void setImpuestoStrategy(ImpuestoStrategy strategy){
        this.impuestostrategy=strategy;
    }
    
    public PedidoRepository getPedidoRepository(){
        return this.pedidorepository;
    }
    
    public void procesarPedido(String cliente, String producto, int cantidad, double precioUnitario) {
        System.out.println("----- Procesando pedido -----");

        if (!serviciostock.validarStock(producto, cantidad)) {
            System.out.println("Error: sin stock disponible.");
            return;
        }

        double subtotal=precioUnitario*cantidad;
        double igv=impuestostrategy.calcular(subtotal);
        double total=subtotal+igv;

        Pedido nuevoPedido = new Pedido(cliente, producto, cantidad, subtotal, igv, total);
        serviciopedido.registrarPedido(nuevoPedido);
        System.out.println("Pedido guardado y registrado correctamente");

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
        
        PedidoFacade pf = new PedidoFacade();
        
        pf.setImpuestoStrategy(new IGV18Strategy());
        pf.procesarPedido("Manolito Sanchez", "Iphone 16", 1, 3200);
        
        pf.setImpuestoStrategy(new ExoneradoStrategy());
        pf.procesarPedido("Martin Jaime", "La play 4", 2, 1500.0);
        
        System.out.println("Pedidos en repositorio:");
        for (Pedido pedido : pf.getPedidoRepository().findAll()){
            System.out.println(pedido);
        }
    }
}
