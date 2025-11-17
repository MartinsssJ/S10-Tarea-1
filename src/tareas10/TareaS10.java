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
    
    public String getCliente(){ return cliente; }
    public String getProducto(){ return producto; }
    public int getCantidad(){ return cantidad; }
    public double getTotal(){ return total; }
    
    @Override
    public String toString() {
        return "Pedido{" + "cliente=" + cliente + ", producto=" + producto + ", cantidad=" + cantidad + ", subtotal=" + subtotal + ", igv=" + igv + ", total=" + total + '}';
    }  
}

interface Observer {
    void notificar(Pedido pedido);
}

class ClienteObserver implements Observer{
    @Override
    public void notificar(Pedido pedido){
        System.out.println("[Cliente] Pedido recibido para: " + pedido.getCliente());
    }
}

class InventarioObserver implements Observer{
    @Override
    public void notificar(Pedido pedido){
        System.out.println("[Inventario] Actualizando stock de: " + pedido.getProducto());
    }
}

class LogObserver implements Observer{
    @Override
    public void notificar(Pedido pedido){
        System.out.println("[Log] Registrado: " + pedido);
    }
}

class PedidoRepository{
    private List<Pedido> almacen = new ArrayList<>();
    
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

class ServicioStock{
    public boolean validarStock(String producto, int cantidad){
        return cantidad>0 && cantidad<=15;
    }
}


class HiloProcesador extends Thread{
    private String cliente;
    private String producto;
    private int cantidad;
    private double precio;
    private PedidoFacade facade;
    
    public HiloProcesador(String cliente, String producto, int cantidad, double precio, PedidoFacade facade){
        this.cliente = cliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.facade = facade;
    }
    
    @Override
    public void run(){
        System.out.println("[Hilo-" + getName() + "] Iniciando procesamiento...");
        facade.procesarPedido(cliente, producto, cantidad, precio);
        System.out.println("[Hilo-" + getName() + "] Completado");
        System.out.println();
    }
}

class PedidoFacade {
    
    private ServicioStock serviciostock;       
    private FacturaService facturaservice;
    private ImpuestoStrategy impuestostrategy;
    private PedidoRepository pedidorepository;
    private List<Observer> observers;

    public PedidoFacade(ServicioStock stock, PedidoRepository repo, FacturaService factura) {
        this.serviciostock = stock;
        this.pedidorepository = repo;
        this.facturaservice = factura;
        this.observers = new ArrayList<>();
    }
    
    public PedidoFacade() {
        this(new ServicioStock(),
             new PedidoRepository(),
             new FacturaAdapter(new LegacyBillingSystem()));
    }
    
    public void agregarObserver(Observer observer) {
        observers.add(observer);
    }
    
    public void notificarObservers(Pedido pedido) {
        for (Observer observer : observers) {
            observer.notificar(pedido);
        }
    }
    
    public void setImpuestoStrategy(ImpuestoStrategy strategy){
        this.impuestostrategy=strategy;
    }
    
    public PedidoRepository getPedidoRepository(){
        return pedidorepository;
    }
    
    public void procesarPedido(String cliente, String producto, int cantidad, double precioUnitario) {
        System.out.println("----- Procesando pedido -----");

        if (!serviciostock.validarStock(producto, cantidad)) {
            System.out.println("Error: sin stock disponible.");
            return;
        }
        
        if (impuestostrategy == null) {
            throw new IllegalStateException("Error: no se configuró la estrategia de impuesto.");
        }
        
        double subtotal=precioUnitario*cantidad;
        double igv=impuestostrategy.calcular(subtotal);
        double total=subtotal+igv;

        Pedido nuevoPedido = new Pedido(cliente, producto, cantidad, subtotal, igv, total);
        pedidorepository.save(nuevoPedido);

        System.out.println("----- COMPROBANTE DE COMPRA -----");
        System.out.println("Cliente: " + cliente);
        System.out.println("Producto: " + producto);
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Subtotal: S/ " + subtotal);
        System.out.println("IGV: S/ " + igv);
        System.out.println("Total: S/ " + total);
        
        facturaservice.generarFactura(cliente, producto, total);
        notificarObservers(nuevoPedido); //notificar observadores
    }
}

public class TareaS10 {
    public static void main(String[] args) throws InterruptedException{
        
        PedidoFacade pf = new PedidoFacade();
        
        pf.agregarObserver(new ClienteObserver());
        pf.agregarObserver(new InventarioObserver());
        pf.agregarObserver(new LogObserver());
        
        pf.setImpuestoStrategy(new IGV18Strategy());
        HiloProcesador hilo1 = new HiloProcesador("Manolito Sanchez", "Iphone 16", 1, 3200, pf);
        hilo1.start();
        hilo1.join();
        
        pf.setImpuestoStrategy(new ExoneradoStrategy());
        HiloProcesador hilo2 = new HiloProcesador("Martin Jaime", "La play 4", 2, 1500.0, pf);
        hilo2.start();
        hilo2.join();
        
        System.out.println("\n--- Pedidos guardados ---");
        for (Pedido pedido : pf.getPedidoRepository().findAll()){
            System.out.println(pedido);
        }
    }
}
