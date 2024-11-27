package fxglapp;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import fxglapp.cliente.CustomerFactory;
import fxglapp.models.EstadoOrden;
import fxglapp.models.Orden;
import fxglapp.ordenes.BufferComidas;
import fxglapp.ordenes.BufferOrdenes;
import fxglapp.ui.FloorFactory;
import javafx.geometry.Point2D;

import java.util.LinkedList;
import java.util.Queue;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import javafx.animation.Interpolator;
import javafx.util.Duration;

public class FXGLGameApp extends GameApplication {

    private static final int TILE_SIZE = 64;
    private static final int[][] TABLE_POSITIONS = {
            {3, 3}, {5, 3}, {7, 3}, {9, 3},
            {3, 5}, {5, 5}, {7, 5}, {9, 5},
            {3, 7}, {5, 7}, {7, 7}, {9, 7}
    };
    // para las mesas disponibles
    private boolean[] tableOccupied = new boolean[TABLE_POSITIONS.length];
    // [+] Para que la mesera atienda
    private Queue<Entity> serveQueue = new LinkedList<>(); // Cola para rastrear los clientes a ser atendidos
    private boolean isWaiterAvailable = true;
    private Entity waiter;
    private Entity waiterOrder;

    // llegada de clientes
    private Queue<Entity> waitingCustomers = new LinkedList<>();
    private static final int MAX_RESTAURANT_CUSTOMERS = 12;
    private static final int TOTAL_CUSTOMERS = 20;

    private BufferOrdenes bufferOrdenes = new BufferOrdenes();
    private BufferComidas bufferComidas = new BufferComidas();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(850);
        settings.setHeight(670);
        settings.setTitle("Restaurant Game");
    }

    public void createFloors() {

        int TILE_SIZE = 64;

        for (int x = 0; x < getAppWidth(); x += TILE_SIZE) {
            for (int y = 0; y < TILE_SIZE * 2; y += TILE_SIZE) {
                spawn("kitchenTile", new SpawnData(x, y));
            }
        }

        for (int x = 0; x < getAppWidth(); x += TILE_SIZE) {
            for (int y = 0; y < TILE_SIZE * 2; y += TILE_SIZE) {
                spawn("kitchenTile", new SpawnData(x, y));
            }
        }

        for (int x = 0; x < getAppWidth(); x += TILE_SIZE) {
            for (int y = 0; y < TILE_SIZE * 2; y += TILE_SIZE) {
                spawn("kitchenTile", new SpawnData(x, y));
            }
        }

        for (int y = 0; y < getAppHeight(); y += TILE_SIZE) {
            spawn("concreteTile", new SpawnData(0, y));
            spawn("concreteTile", new SpawnData(TILE_SIZE, y));
        }

        for (int x = TILE_SIZE * 2; x < getAppWidth(); x += TILE_SIZE) {
            for (int y = TILE_SIZE * 2; y < getAppHeight(); y += TILE_SIZE) {
                spawn("floorTile",
                        new SpawnData(x, y)
                                .put("isDark", ((x / TILE_SIZE) + (y / TILE_SIZE)) % 2 == 0));
            }
        }

        int[][] tablePositions = {
                {3, 3}, {5, 3}, {7, 3}, {9, 3},
                {3, 5}, {5, 5}, {7, 5}, {9, 5},
                {3, 7}, {5, 7}, {7, 7}, {9, 7},
        };

        for (int[] pos : tablePositions) {
            int x = pos[0] * TILE_SIZE;
            int y = pos[1] * TILE_SIZE;
            spawn("tableTile", new SpawnData(x, y));
            spawn("chair", new SpawnData(x+30, y+80));
        }

        int chefTableX1 = 300;
        int chefTableY1 = TILE_SIZE / 2;

        int chefTableX2 = 600;
        int chefTableY2 = (TILE_SIZE / 2);

        spawn("tableCook", new SpawnData(chefTableX1, chefTableY1));
        spawn("tableCook", new SpawnData(chefTableX2, chefTableY2));

        spawn("cooker", new SpawnData(chefTableX1, chefTableY1-50));
        spawn("cooker", new SpawnData(chefTableX2, chefTableY2-50));

        spawn("kitchenset", new SpawnData(500, 25));
        spawn("kitchenset", new SpawnData(200, 25));

        spawn("dishes", new SpawnData(730, 25));
        spawn("drinkbox", new SpawnData(400, 25));

        //spawn("waiter", new SpawnData(680, 90));

        int yPositonWall = 0;
        for (int x = 0; x <= 5; x++) {
            spawn("wall", new SpawnData(120, yPositonWall));
            yPositonWall += 90;

        }

        spawn("door", new SpawnData(120, 540));
        spawn("open", new SpawnData(65, 500));
        spawn("tejado", new SpawnData(105, 200));
        spawn("bush", new SpawnData(200, 600));
        spawn("trash", new SpawnData(770, 590));
        spawn("lamp", new SpawnData(140, 80));
        spawn("lamp", new SpawnData(140, 435));

        int xPositonWall = 200;
        yPositonWall = 130;
        for (int x = 0; x <= 3; x++) {
            spawn("bush", new SpawnData(xPositonWall, 600));
            spawn("bush", new SpawnData(780, yPositonWall));
            yPositonWall += 110;
            xPositonWall += 130;
        }
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new FloorFactory());
        getGameWorld().addEntityFactory(new CustomerFactory());
        createFloors();

        waiter = spawn("waiter_order", new SpawnData(680, 90));
        waiterOrder = spawn("waiter", new SpawnData(680, 490));
        spawnCustomersSequence();

        // iniciamos los hilos de procesamiento de órdenes
        procesarOrdenes();
        entregarOrdenes();

    }

    private void spawnCustomersSequence() {
        for (int i = 1; i <= TOTAL_CUSTOMERS; i++) {
            final int customerNumber = i;
            runOnce(() -> {
                Entity customer = spawn("client_1", 65, 0);

                if (getGameWorld().getEntities().stream().filter(e -> e.getType().toString().contains("client")).count() < TOTAL_CUSTOMERS) {

                    moveCustomerInside(customer);

                } else {

                    waitingCustomers.offer(customer);
                    positionCustomerInWaitingLine(customer);
                }
            }, Duration.seconds(customerNumber * 2)); // Espaciar la llegada
        }
    }

    private void moveCustomerInside(Entity customer) {
        // Tu lógica de movimiento existente
        for (int x = 0; x <= 20; x++) {
            final int step = x;
            runOnce(() -> {
                entityBuilder()
                        .at(customer.getPosition())
                        .with(new ProjectileComponent(new Point2D(0, 1), 100))
                        .buildAndAttach();

                customer.translate(0, 25);

                if (step == 20) {
                    moveToRandomTable(customer);

                    // Si hay clientes esperando, mover el siguiente
                    checkAndMoveWaitingCustomer();
                }
            }, Duration.seconds(0.2 * x));
        }
    }

    private void positionCustomerInWaitingLine(Entity customer) {
        int waitingLineX = 10;  // Posición X de la fila de espera
        int waitingLineY = 25 + (waitingCustomers.size() * 65);  // Espaciar clientes verticalmente

        customer.setPosition(waitingLineX, waitingLineY);
    }

    private void checkAndMoveWaitingCustomer() {
        if (!waitingCustomers.isEmpty()) {
            Entity waitingCustomer = waitingCustomers.poll();
            moveToRandomTable(waitingCustomer);
        }
    }

    private void spawnCustomer() {
        Entity customer = spawn("client_1", 65, 0);

        for (int x = 0; x <= 20; x++) {
            final int step = x;
            runOnce(() -> {
                entityBuilder()
                        .at(customer.getPosition())
                        .with(new ProjectileComponent(new Point2D(0, 1), 100))
                        .buildAndAttach();

                customer.translate(0, 25);

                if (step == 20) {
                    moveToRandomTable(customer);
                }
            }, Duration.seconds(0.2 * x));
        }
    }


    private void moveToRandomTable(Entity customer) {
        // Buscar una mesa disponible
        int availableTableIndex = findAvailableTable();

        if (availableTableIndex == -1) {
            // No hay mesas disponibles, poner al cliente en espera
            waitingCustomers.offer(customer);
            positionCustomerInWaitingLine(customer);
            return;
        }

        // Marcar la mesa como ocupada
        tableOccupied[availableTableIndex] = true;

        int[] targetTable = TABLE_POSITIONS[availableTableIndex];

        double targetX = targetTable[0] * TILE_SIZE;
        double targetY = targetTable[1] * TILE_SIZE;

        Point2D currentPos = customer.getPosition();

        double diffX = targetX - currentPos.getX();
        double diffY = targetY - currentPos.getY();

        int stepsX = (int)Math.abs(diffX / 25);
        if (stepsX > 0) {
            for (int i = 0; i <= stepsX; i++) {
                final int step = i;
                final int tableIndex = availableTableIndex;
                runOnce(() -> {
                    entityBuilder()
                            .at(customer.getPosition())
                            .with(new ProjectileComponent(new Point2D(diffX > 0 ? 1 : -1, 0), 100))
                            .buildAndAttach();

                    customer.translate(diffX > 0 ? 25 : -25, 0);

                    if (step == stepsX) {
                        moveCustomerY(customer, diffY, tableIndex);
                    }
                }, Duration.seconds(0.2 * i));
            }
        } else {
            moveCustomerY(customer, diffY, availableTableIndex);
        }
    }

    private void moveCustomerY(Entity customer, double diffY, int tableIndex) {
        int stepsY = (int)Math.abs(diffY / 25);
        for (int i = 0; i <= stepsY; i++) {
            final int step = i;
            runOnce(() -> {
                entityBuilder()
                        .at(customer.getPosition())
                        .with(new ProjectileComponent(new Point2D(0, diffY > 0 ? 1 : -1), 100))
                        .buildAndAttach();

                customer.translate(0, diffY > 0 ? 25 : -25);

                if (step == stepsY) {
                    System.out.println("Cliente llegó a mesa: " + tableIndex);
                    System.out.println("Clientes en la cola de servicio: " + serveQueue.size());
                    customer.setProperty("assignedTable", tableIndex);

                    // Añadir el cliente a la cola solo si no está ya en ella
                    if (!serveQueue.contains(customer)) {
                        serveQueue.offer(customer);

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
            }, Duration.seconds(0.2 * i));
        }
    }

    private int findAvailableTable() {
        for (int i = 0; i < tableOccupied.length; i++) {
            if (!tableOccupied[i]) {
                return i;
            }
        }
        return -1;
    }

    private void moveWaiterToCustomer(Entity waiter, Entity customer) {

        System.out.println("Moviendo waiter a cliente");
        System.out.println("Posición waiter: " + waiter.getPosition());
        System.out.println("Posición cliente: " + customer.getPosition());

        Point2D customerPos = customer.getPosition();

        double diffX = customerPos.getX() - waiter.getPosition().getX();
        double diffY = customerPos.getY() - waiter.getPosition().getY();

        int stepsX = (int)Math.abs(diffX / 25);

        if (stepsX > 0) {
            for (int i = 0; i <= stepsX; i++) {
                final int step = i;
                runOnce(() -> {
                    waiter.translate(diffX > 0 ? 25 : -25, 0);

                    // Cuando termina el movimiento horizontal, comenzar movimiento vertical
                    if (step == stepsX) {
                        moveWaiterVertical(waiter, customer, diffY);
                    }
                }, Duration.seconds(0.2 * i));
            }
        } else {
            // Si no hay movimiento horizontal, ir directo al movimiento vertical
            moveWaiterVertical(waiter, customer, diffY);
        }
    }

    private void moveWaiterVertical(Entity waiter, Entity customer, double diffY) {
        int stepsY = (int)Math.abs(diffY / 25);
        for (int i = 0; i <= stepsY; i++) {
            final int step = i;
            runOnce(() -> {
                waiter.translate(0, diffY > 0 ? 25 : -25);

                // Cuando llega a la mesa, simular tomar la orden
                if (step == stepsY) {
                    takeOrder(waiter, customer);
                }
            }, Duration.seconds(0.2 * i));
        }
    }

    private void takeOrder(Entity waiter, Entity customer) {

        runOnce(() -> {

            // Creamos la orden
            Orden orden = new Orden(customer);

            // agregar la orden al buffer
            bufferOrdenes.agregarOrden(orden);

            // Cambiar estado a la orden
            orden.setEstado(EstadoOrden.EN_PROCESO);
            System.out.println("[+] Estamos tomando la orden...");
            // Regresa a su posición
            returnWaiterToOriginalPosition(waiter, customer);

        }, Duration.seconds(2));
    }

    private void returnWaiterToOriginalPosition(Entity waiter, Entity customer) {
        // Agregar verificaciones de nulidad
        if (waiter == null) return;

        Point2D originalPos = new Point2D(600, 90);
        Point2D currentPos = waiter.getPosition();

        double diffX = originalPos.getX() - currentPos.getX();
        double diffY = originalPos.getY() - currentPos.getY();

        int stepsX = (int)Math.abs(diffX / 25);

        // Movimiento de regreso horizontal
        if (stepsX > 0) {
            for (int i = 0; i <= stepsX; i++) {
                final int step = i;
                runOnce(() -> {
                    // Verificar que el waiter aún exista
                    if (waiter.isActive()) {
                        waiter.translate(diffX > 0 ? 25 : -25, 0);

                        // Cuando termina el movimiento horizontal, comenzar movimiento vertical
                        if (step == stepsX) {
                            returnWaiterVertical(waiter, diffY);
                        }
                    }
                }, Duration.seconds(0.2 * i));
            }
        } else {
            // Si no hay movimiento horizontal, ir directo al movimiento vertical
            returnWaiterVertical(waiter, diffY);
        }
    }

    private void returnWaiterVertical(Entity waiter, double diffY) {
        // Agregar verificaciones de nulidad y actividad
        if (waiter == null || !waiter.isActive()) return;

        int stepsY = (int)Math.abs(diffY / 25);
        for (int i = 0; i <= stepsY; i++) {
            final int step = i;
            runOnce(() -> {
                // Verificar que el waiter aún exista
                if (waiter.isActive()) {
                    waiter.translate(0, diffY > 0 ? 25 : -25);

                    // Cuando termina el desplazamiento, volver exactamente a la posición original
                    if (step == stepsY) {
                        waiter.setPosition(680, 90);
                        checkAndServeNextCustomer();
                    }
                }
            }, Duration.seconds(0.2 * i));
        }
    }
    private void checkAndServeNextCustomer() {
        System.out.println("Verificando siguiente cliente para servir");

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
                        // Animar waiterOrder para entregar la orden
                        animarEntregaOrden(ordenLista);
                    } else {
                        // Si no hay órdenes, volver a posición inicial
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
        Point2D clientePos = cliente.getPosition();

        // Lógica de animación similar a tus métodos de movimiento existentes
        moveWaiterOrderToCustomer(waiterOrder, cliente);
    }

    private void moveWaiterOrderToCustomer(Entity waiterOrder, Entity cliente) {
        // Initial position of waiterOrder
        Point2D initialPos = new Point2D(680, 490);

        // Customer's position
        Point2D clientPos = cliente.getPosition();

        double diffX = clientPos.getX() - initialPos.getX();
        double diffY = clientPos.getY() - initialPos.getY();

        int stepsX = (int)Math.abs(diffX / 25);

        // Horizontal movement
        if (stepsX > 0) {
            for (int i = 0; i <= stepsX; i++) {
                final int step = i;
                runOnce(() -> {
                    waiterOrder.translate(diffX > 0 ? 25 : -25, 0);

                    // When horizontal movement is complete, start vertical movement
                    if (step == stepsX) {
                        moveWaiterOrderVertical(waiterOrder, cliente, initialPos);
                    }
                }, Duration.seconds(0.2 * i));
            }
        } else {
            // If no horizontal movement, go directly to vertical movement
            moveWaiterOrderVertical(waiterOrder, cliente, initialPos);
        }
    }

    private void moveWaiterOrderVertical(Entity waiterOrder, Entity cliente, Point2D initialPos) {
        Point2D clientPos = cliente.getPosition();
        double diffY = clientPos.getY() - waiterOrder.getPosition().getY();

        int stepsY = (int)Math.abs(diffY / 25);
        for (int i = 0; i <= stepsY; i++) {
            final int step = i;
            runOnce(() -> {
                waiterOrder.translate(0, diffY > 0 ? 25 : -25);

                // When it reaches the table, return to initial position
                if (step == stepsY) {
                    waiterOrder.setPosition(initialPos.getX(), initialPos.getY());
                    System.out.println("Orden entregada al cliente");
                }
            }, Duration.seconds(0.2 * i));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}