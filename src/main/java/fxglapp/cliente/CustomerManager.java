package fxglapp.cliente;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import java.util.*;
import static com.almasb.fxgl.dsl.FXGL.*;
import fxglapp.mesero.WaiterManager;

public class CustomerManager {

    private static final int TILE_SIZE = 64;
    private static final int[][] TABLE_POSITIONS = {
            {3, 3}, {5, 3}, {7, 3}, {9, 3},
            {3, 5}, {5, 5}, {7, 5}, {9, 5},
            {3, 7}, {5, 7}, {7, 7}, {9, 7}
    };

    private boolean[] tableOccupied;
    private Queue<Entity> waitingCustomers = new LinkedList<>();
    private static final int MAX_RESTAURANT_CUSTOMERS = 12;
    private static final int TOTAL_CUSTOMERS = 20;
    private static final int WAITING_LINE_X = 10;
    private static final int WAITING_LINE_SPACING = 50;

    private WaiterManager waiterManager;

    public CustomerManager(boolean[] tableOccupied, WaiterManager waiterManager) {
        this.tableOccupied = tableOccupied;
        this.waiterManager = waiterManager;
    }

    public void spawnCustomersSequence() {
        double lambda = 0.5;
        Random random = new Random();

        for (int i = 1; i <= TOTAL_CUSTOMERS; i++) {
            final int customerNumber = i;
            double timeBetweenArrivals = -Math.log(1.0 - random.nextDouble()) / lambda;

            runOnce(() -> {
                Entity customer = spawn("client_1", 65, 0);

                if (getGameWorld().getEntities().stream()
                        .filter(e -> e.getType().toString().contains("client")).count() < MAX_RESTAURANT_CUSTOMERS) {

                    if (!waitingCustomers.isEmpty()) {
                        waitingCustomers.offer(customer);
                        positionCustomerInWaitingLine(customer);
                    } else {
                        moveCustomerInside(customer);
                    }
                } else {
                    waitingCustomers.offer(customer);
                    positionCustomerInWaitingLine(customer);
                }
            }, Duration.seconds(timeBetweenArrivals * customerNumber));
        }
    }

    private void moveCustomerInside(Entity customer) {
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
                    checkAndMoveWaitingCustomer();
                }
            }, Duration.seconds(0.2 * x));
        }
    }

    private void positionCustomerInWaitingLine(Entity customer) {
        int queuePosition = waitingCustomers.size() - 1;
        int waitingLineY = 500 - (queuePosition * WAITING_LINE_SPACING);

        customer.setPosition(WAITING_LINE_X, waitingLineY);
    }

    private void checkAndMoveWaitingCustomer() {
        if (!waitingCustomers.isEmpty()) {
            int availableTableIndex = findAvailableTable();

            if (availableTableIndex != -1) {
                Entity nextWaitingCustomer = findNearestCustomerToEntrance();

                if (nextWaitingCustomer != null) {
                    waitingCustomers.remove(nextWaitingCustomer);
                    moveToRandomTable(nextWaitingCustomer);
                    repositionWaitingCustomers();
                }
            }
        }
    }

    private Entity findNearestCustomerToEntrance() {
        return waitingCustomers.stream()
                .max(Comparator.comparingDouble(customer -> customer.getPosition().getY()))
                .orElse(null);
    }

    private void repositionWaitingCustomers() {
        List<Entity> currentWaitingCustomers = new ArrayList<>(waitingCustomers);

        for (int i = 0; i < currentWaitingCustomers.size(); i++) {
            Entity customer = currentWaitingCustomers.get(i);
            int waitingLineY = 500 - (i * WAITING_LINE_SPACING);
            moveCustomerInQueue(customer, waitingLineY);
        }
    }

    private void moveCustomerInQueue(Entity customer, int targetY) {
        Point2D currentPos = customer.getPosition();
        double diffY = targetY - currentPos.getY();

        int stepsY = (int)Math.abs(diffY / 25);
        for (int i = 0; i <= stepsY; i++) {
            runOnce(() -> {
                customer.translate(0, diffY > 0 ? 25 : -25);
            }, Duration.seconds(0.2 * i));
        }
    }

    private void moveToRandomTable(Entity customer) {
        int availableTableIndex = findAvailableTable();

        if (availableTableIndex == -1) {
            waitingCustomers.offer(customer);
            positionCustomerInWaitingLine(customer);
            return;
        }

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
            int finalI = i;
            runOnce(() -> {
                entityBuilder()
                        .at(customer.getPosition())
                        .with(new ProjectileComponent(new Point2D(0, diffY > 0 ? 1 : -1), 100))
                        .buildAndAttach();

                customer.translate(0, diffY > 0 ? 25 : -25);

                if (finalI == stepsY) {
                    System.out.println("Cliente llegó a mesa: " + tableIndex);
                    customer.setProperty("assignedTable", tableIndex);

                    // Notificar al WaiterManager que el cliente está listo para ser atendido
                    waiterManager.customerSeated(customer);
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

    public void customerLeaveRestaurant(Entity customer) {
        Point2D currentPos = customer.getPosition();
        Point2D doorPos = new Point2D(120, 540);
        Point2D exitPos = new Point2D(65, 0);

        double diffXToDoor = doorPos.getX() - currentPos.getX();
        double diffYToDoor = doorPos.getY() - currentPos.getY();

        int stepsXToDoor = (int)Math.abs(diffXToDoor / 25);

        if (stepsXToDoor > 0) {
            for (int i = 0; i <= stepsXToDoor; i++) {
                int finalI = i;
                runOnce(() -> {
                    customer.translate(diffXToDoor > 0 ? 25 : -25, 0);

                    if (finalI == stepsXToDoor) {
                        customerMoveToDoorVertical(customer, diffYToDoor, exitPos);
                    }
                }, Duration.seconds(0.2 * i));
            }
        } else {
            customerMoveToDoorVertical(customer, diffYToDoor, exitPos);
        }
    }

    private void customerMoveToDoorVertical(Entity customer, double diffYToDoor, Point2D exitPos) {
        int stepsYToDoor = (int)Math.abs(diffYToDoor / 25);
        for (int i = 0; i <= stepsYToDoor; i++) {
            int finalI = i;
            runOnce(() -> {
                customer.translate(0, diffYToDoor > 0 ? 25 : -25);

                if (finalI == stepsYToDoor) {
                    customerFinalExit(customer, exitPos);
                }
            }, Duration.seconds(0.2 * i));
        }
    }

    private void customerFinalExit(Entity customer, Point2D exitPos) {
        Point2D doorPos = new Point2D(120, 540);
        double diffX = exitPos.getX() - doorPos.getX();
        double diffY = exitPos.getY() - doorPos.getY();

        int stepsX = (int)Math.abs(diffX / 25);

        if (stepsX > 0) {
            for (int i = 0; i <= stepsX; i++) {
                int finalI = i;
                runOnce(() -> {
                    customer.translate(diffX > 0 ? 25 : -25, 0);

                    if (finalI == stepsX) {
                        customerLeaveVertical(customer, diffY);
                    }
                }, Duration.seconds(0.2 * i));
            }
        } else {
            customerLeaveVertical(customer, diffY);
        }
    }

    private void customerLeaveVertical(Entity customer, double diffY) {
        int stepsY = (int)Math.abs(diffY / 25);
        for (int i = 0; i <= stepsY; i++) {
            int finalI = i;
            runOnce(() -> {
                customer.translate(0, diffY > 0 ? 25 : -25);

                if (finalI == stepsY) {
                    customer.removeFromWorld();
                    checkAndMoveWaitingCustomer();
                }
            }, Duration.seconds(0.2 * i));
        }
    }
}
