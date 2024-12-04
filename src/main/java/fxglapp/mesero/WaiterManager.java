package fxglapp.mesero;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import java.util.*;
import static com.almasb.fxgl.dsl.FXGL.*;
import fxglapp.cliente.CustomerManager;
import fxglapp.ordenes.EstadoOrden;
import fxglapp.ordenes.Orden;
import fxglapp.ordenes.BufferComidas;
import fxglapp.ordenes.BufferOrdenes;

public class WaiterManager {

    private Queue<Entity> serveQueue = new LinkedList<>();
    private boolean isWaiterAvailable = true;
    private Entity waiter;
    private Entity waiterOrder;
    private Set<Entity> servedCustomers = new HashSet<>();
    private BufferOrdenes bufferOrdenes;
    private BufferComidas bufferComidas;
    private boolean[] tableOccupied;
    private CustomerManager customerManager;

    public WaiterManager(BufferOrdenes bufferOrdenes, BufferComidas bufferComidas, boolean[] tableOccupied) {
        this.bufferOrdenes = bufferOrdenes;
        this.bufferComidas = bufferComidas;
        this.tableOccupied = tableOccupied;
    }

    public void initWaiters() {
        waiter = spawn("waiter_order", 680, 90);
        waiterOrder = spawn("waiter", 680, 490);

        //procesarOrdenes();
        entregarOrdenes();
    }

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    public void customerSeated(Entity customer) {
        if (!servedCustomers.contains(customer)) {
            if (!serveQueue.contains(customer)) {
                serveQueue.offer(customer);
            }

            if (waiter != null) {
                System.out.println("Waiter encontrado: " + waiter);
                System.out.println("Waiter disponible: " + isWaiterAvailable);

                if (isWaiterAvailable) {
                    isWaiterAvailable = false;
                    moveWaiterToCustomer(waiter, customer);
                }
            } else {
                System.out.println("No se encontró waiter");
            }
        }
    }

    private void moveWaiterToCustomer(Entity waiter, Entity customer) {
        System.out.println("Moviendo waiter a cliente");
        System.out.println("Posición waiter: " + waiter.getPosition());
        System.out.println("Posición cliente: " + customer.getPosition());

        Point2D customerPos = customer.getPosition();

        double diffX = customerPos.getX() - waiter.getPosition().getX();
        double diffY = customerPos.getY() - waiter.getPosition().getY();

        int stepsX = (int) Math.abs(diffX / 25);

        if (stepsX > 0) {
            for (int i = 0; i <= stepsX; i++) {
                int finalI = i;
                runOnce(() -> {
                    waiter.translate(diffX > 0 ? 25 : -25, 0);

                    if (finalI == stepsX) {
                        moveWaiterVertical(waiter, customer, diffY);
                    }
                }, Duration.seconds(0.2 * i));
            }
        } else {
            moveWaiterVertical(waiter, customer, diffY);
        }
    }

    private void moveWaiterVertical(Entity waiter, Entity customer, double diffY) {
        int stepsY = (int) Math.abs(diffY / 25);
        for (int i = 0; i <= stepsY; i++) {
            int finalI = i;
            runOnce(() -> {
                waiter.translate(0, diffY > 0 ? 25 : -25);

                if (finalI == stepsY) {
                    takeOrder(waiter, customer);
                }
            }, Duration.seconds(0.2 * i));
        }
    }

    private void takeOrder(Entity waiter, Entity customer) {
        runOnce(() -> {
            servedCustomers.add(customer);

            Orden orden = new Orden(customer);
            bufferOrdenes.agregarOrden(orden);
            orden.setEstado(EstadoOrden.EN_PROCESO);
            System.out.println("[+] Estamos tomando la orden...");

            returnWaiterToOriginalPosition(waiter);
        }, Duration.seconds(2));
    }

    private void returnWaiterToOriginalPosition(Entity waiter) {
        if (waiter == null) return;

        Point2D originalPos = new Point2D(680, 90);
        Point2D currentPos = waiter.getPosition();

        double diffX = originalPos.getX() - currentPos.getX();
        double diffY = originalPos.getY() - currentPos.getY();

        int stepsX = (int) Math.abs(diffX / 25);

        if (stepsX > 0) {
            for (int i = 0; i <= stepsX; i++) {
                int finalI = i;
                runOnce(() -> {
                    if (waiter.isActive()) {
                        waiter.translate(diffX > 0 ? 25 : -25, 0);

                        if (finalI == stepsX) {
                            returnWaiterVertical(waiter, diffY);
                        }
                    }
                }, Duration.seconds(0.2 * i));
            }
        } else {
            returnWaiterVertical(waiter, diffY);
        }
    }

    private void returnWaiterVertical(Entity waiter, double diffY) {
        if (waiter == null || !waiter.isActive()) return;

        int stepsY = (int) Math.abs(diffY / 25);
        for (int i = 0; i <= stepsY; i++) {
            int finalI = i;
            runOnce(() -> {
                if (waiter.isActive()) {
                    waiter.translate(0, diffY > 0 ? 25 : -25);

                    if (finalI == stepsY) {
                        waiter.setPosition(680, 90);
                        checkAndServeNextCustomer();
                    }
                }
            }, Duration.seconds(0.2 * i));
        }
    }

    private void checkAndServeNextCustomer() {
        System.out.println("Verificando siguiente cliente para servir");

        Queue<Entity> unservedCustomers = new LinkedList<>();
        for (Entity customer : serveQueue) {
            if (!servedCustomers.contains(customer)) {
                unservedCustomers.offer(customer);
            }
        }
        serveQueue = unservedCustomers;

        if (!serveQueue.isEmpty() && waiter != null) {
            Entity nextCustomer = serveQueue.poll();

            if (nextCustomer != null) {
                isWaiterAvailable = false;
                moveWaiterToCustomer(waiter, nextCustomer);
            }
        } else {
            isWaiterAvailable = true;
            System.out.println("No hay más clientes en la cola");
        }
    }

    private void procesarOrdenes() {
        new Thread(() -> {
            while (true) {
                try {
                    Orden orden = bufferOrdenes.obtenerOrden();

                    // Simular tiempo de cocina
                    Thread.sleep(3000);

                    // Marcar orden como lista
                    orden.setEstado(EstadoOrden.LISTO);
                    System.out.println("[+] La ORDEN está lista!");

                    // Agregar al buffer de comidas
                    bufferComidas.agregarComida(orden);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    private void entregarOrdenes() {
        new Thread(() -> {
            while (true) {
                try {
                    Orden ordenLista = bufferComidas.verificarComidaLista(waiterOrder);

                    if (ordenLista != null) {
                        animarEntregaOrden(ordenLista);
                    } else {
                        waiterOrder.setPosition(450, 490);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    private void animarEntregaOrden(Orden orden) {
        Entity cliente = orden.getCliente();
        moveWaiterOrderToCustomer(waiterOrder, cliente);
    }

    private void moveWaiterOrderToCustomer(Entity waiterOrder, Entity cliente) {
        Point2D initialPos = new Point2D(680, 490);

        Point2D clientPos = cliente.getPosition();

        double diffX = clientPos.getX() - initialPos.getX();
        double diffY = clientPos.getY() - initialPos.getY();

        int stepsX = (int) Math.abs(diffX / 25);

        if (stepsX > 0) {
            for (int i = 0; i <= stepsX; i++) {
                int finalI = i;
                runOnce(() -> {
                    waiterOrder.translate(diffX > 0 ? 25 : -25, 0);

                    if (finalI == stepsX) {
                        moveWaiterOrderVertical(waiterOrder, cliente, initialPos);
                    }
                }, Duration.seconds(0.2 * i));
            }
        } else {
            moveWaiterOrderVertical(waiterOrder, cliente, initialPos);
        }
    }

    private void moveWaiterOrderVertical(Entity waiterOrder, Entity cliente, Point2D initialPos) {
        Point2D clientPos = cliente.getPosition();
        double diffY = clientPos.getY() - waiterOrder.getPosition().getY();

        int stepsY = (int) Math.abs(diffY / 25);
        for (int i = 0; i <= stepsY; i++) {
            int finalI = i;
            runOnce(() -> {
                waiterOrder.translate(0, diffY > 0 ? 25 : -25);

                if (finalI == stepsY) {
                    waiterOrder.setPosition(initialPos.getX(), initialPos.getY());
                    System.out.println("Orden entregada al cliente");

                    int tableIndex = cliente.getInt("assignedTable");
                    tableOccupied[tableIndex] = false;

                    runOnce(() -> {
                        customerManager.customerLeaveRestaurant(cliente);
                    }, Duration.seconds(5));
                }
            }, Duration.seconds(0.2 * i));
        }
    }
}
