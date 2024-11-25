package fxglapp.table;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.animation.Interpolators;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import static com.almasb.fxgl.dsl.FXGL.*;
import static java.util.concurrent.CompletableFuture.runAsync;

class Table {
    private final Point2D position;
    private boolean isOccupied;
    private final int tableNumber;
    private Entity customerOccupying;

    public Table(Point2D position, int tableNumber) {
        this.position = position;
        this.isOccupied = false;
        this.tableNumber = tableNumber;
    }

    public Point2D getPosition() { return position; }
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; }
    public void setCustomer(Entity customer) { this.customerOccupying = customer; }
    public Entity getCustomer() { return customerOccupying; }
    public int getTableNumber() { return tableNumber; }
}

public class RestaurantManager {
    private static final List<Table> tables = new ArrayList<>();
    private static final Random random = new Random();
    private static final int CUSTOMER_STAY_TIME_MIN = 10;
    private static final int CUSTOMER_STAY_TIME_MAX = 20;

    // Monitor components
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition tableAvailable = lock.newCondition();
    private static final int MAX_WAITING_CUSTOMERS = 5;
    private static int waitingCustomers = 0;

    private static final Point2D ENTRY_POINT = new Point2D(65, 0);
    private static final Point2D MIDDLE_POINT = new Point2D(65, 500);
    private static final Point2D WAITING_AREA = new Point2D(65, 300);

    static {
        initializeTables();
    }

    private static void initializeTables() {
        int TILE_SIZE = 64;
        int[][] tablePositions = {
                {3, 3}, {5, 3}, {7, 3}, {9, 3},
                {3, 5}, {5, 5}, {7, 5}, {9, 5},
                {3, 7}, {5, 7}, {7, 7}, {9, 7}
        };

        for (int i = 0; i < tablePositions.length; i++) {
            Point2D tablePos = new Point2D(tablePositions[i][0] * TILE_SIZE, tablePositions[i][1] * TILE_SIZE);
            tables.add(new Table(tablePos, i + 1));
        }
    }

    public static void handleNewCustomer(Entity customer) {
        lock.lock();
        try {
            if (waitingCustomers >= MAX_WAITING_CUSTOMERS) {
                // Restaurante lleno, el cliente se va
                moveCustomerOut(customer);
                return;
            }

            Table availableTable = findAvailableTable();
            if (availableTable != null) {
                moveCustomerToTable(customer, availableTable);
            } else {
                // No hay mesas disponibles, cliente espera
                waitingCustomers++;
                moveCustomerToWaitingArea(customer);

                // Esperar hasta que haya una mesa disponible
                runAsync(() -> {
                    lock.lock();
                    try {
                        while (findAvailableTable() == null) {
                            tableAvailable.await();
                        }
                        // Mesa disponible, mover al cliente
                        Table table = findAvailableTable();
                        if (table != null) {
                            waitingCustomers--;
                            moveCustomerFromWaitingToTable(customer, table);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                });
            }
        } finally {
            lock.unlock();
        }
    }

    private static void moveCustomerToWaitingArea(Entity customer) {
        animationBuilder()
                .duration(Duration.seconds(2))
                .interpolator(Interpolators.SMOOTH.EASE_OUT())
                .translate(customer)
                .from(ENTRY_POINT)
                .to(WAITING_AREA)
                .build()
                .start();
    }

    private static void moveCustomerFromWaitingToTable(Entity customer, Table table) {
        table.setOccupied(true);
        table.setCustomer(customer);

        Point2D tablePosition = new Point2D(
                table.getPosition().getX() + 30,
                table.getPosition().getY() + 80
        );

        animationBuilder()
                .duration(Duration.seconds(2))
                .interpolator(Interpolators.SMOOTH.EASE_OUT())
                .onFinished(() -> handleCustomerAtTable(customer, table))
                .translate(customer)
                .from(WAITING_AREA)
                .to(tablePosition)
                .build()
                .start();
    }

    private static Table findAvailableTable() {
        for (Table table : tables) {
            if (!table.isOccupied()) {
                return table;
            }
        }
        return null;
    }

    private static void moveCustomerToTable(Entity customer, Table table) {
        table.setOccupied(true);
        table.setCustomer(customer);

        animationBuilder()
                .duration(Duration.seconds(2))
                .interpolator(Interpolators.SMOOTH.EASE_OUT())
                .onFinished(() -> moveCustomerFromMiddleToTable(customer, table))
                .translate(customer)
                .from(ENTRY_POINT)
                .to(MIDDLE_POINT)
                .build()
                .start();
    }

    private static void moveCustomerFromMiddleToTable(Entity customer, Table table) {
        Point2D tablePosition = new Point2D(
                table.getPosition().getX() + 30,
                table.getPosition().getY() + 80
        );

        animationBuilder()
                .duration(Duration.seconds(1.5))
                .interpolator(Interpolators.SMOOTH.EASE_OUT())
                .onFinished(() -> handleCustomerAtTable(customer, table))
                .translate(customer)
                .from(MIDDLE_POINT)
                .to(tablePosition)
                .build()
                .start();
    }

    private static void handleCustomerAtTable(Entity customer, Table table) {
        int stayTime = CUSTOMER_STAY_TIME_MIN +
                random.nextInt(CUSTOMER_STAY_TIME_MAX - CUSTOMER_STAY_TIME_MIN);

        runOnce(() -> {
            lock.lock();
            try {
                moveCustomerOut(customer);
                table.setOccupied(false);
                table.setCustomer(null);
                tableAvailable.signalAll(); // Notificar que hay una mesa disponible
            } finally {
                lock.unlock();
            }
        }, Duration.seconds(stayTime));
    }

    private static void moveCustomerOut(Entity customer) {
        Point2D exitPoint = new Point2D(65, getAppHeight() + 100);

        animationBuilder()
                .duration(Duration.seconds(2))
                .interpolator(Interpolators.SMOOTH.EASE_IN())
                .onFinished(() -> customer.removeFromWorld())
                .translate(customer)
                .to(exitPoint)
                .build()
                .start();
    }
}