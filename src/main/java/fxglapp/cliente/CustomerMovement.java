package fxglapp.cliente;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.animation.Interpolators;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

public class CustomerMovement {
    private static final List<Point2D> tables = new ArrayList<>();
    private static final List<Boolean> tableOccupied = new ArrayList<>();
    private static final Random random = new Random();

    // Inicializar las posiciones de las mesas
    static {
        int TILE_SIZE = 64;
        int[][] tablePositions = {
                {3, 3}, {5, 3}, {7, 3}, {9, 3},
                {3, 5}, {5, 5}, {7, 5}, {9, 5},
                {3, 7}, {5, 7}, {7, 7}, {9, 7}
        };

        for (int[] pos : tablePositions) {
            tables.add(new Point2D(pos[0] * TILE_SIZE, pos[1] * TILE_SIZE));
            tableOccupied.add(false);
        }
    }

    public static void moveCustomerToTable(Entity customer) {
        // Punto de entrada (donde aparece el cliente)
        Point2D entryPoint = new Point2D(65, 0);

        // Punto intermedio (donde el cliente gira hacia la mesa)
        Point2D middlePoint = new Point2D(65, 500);

        // Encontrar una mesa disponible
        int tableIndex = findAvailableTable();
        if (tableIndex == -1) {
            // No hay mesas disponibles, el cliente se va
            animateCustomerExit(customer);
            return;
        }

        Point2D tablePos = tables.get(tableIndex);
        tableOccupied.set(tableIndex, true);

        // Secuencia de movimientos
        animateCustomerMovement(customer, entryPoint, middlePoint, tablePos, tableIndex);
    }

    private static void animateCustomerMovement(Entity customer, Point2D start, Point2D middle, Point2D end, int tableIndex) {
        // Movimiento hacia abajo
        animationBuilder()
                .duration(Duration.seconds(2))
                .interpolator(Interpolators.SMOOTH.EASE_OUT())
                .onFinished(() -> moveToTable(customer, middle, end, tableIndex))
                .translate(customer)
                .from(start)
                .to(middle)
                .build()
                .start();
    }

    private static void moveToTable(Entity customer, Point2D current, Point2D table, int tableIndex) {
        // Movimiento hacia la mesa
        animationBuilder()
                .duration(Duration.seconds(1.5))
                .interpolator(Interpolators.SMOOTH.EASE_OUT())
                .onFinished(() -> handleCustomerAtTable(customer, tableIndex))
                .translate(customer)
                .from(current)
                .to(new Point2D(table.getX() + 30, table.getY() + 80)) // Ajuste para que se siente en la silla
                .build()
                .start();
    }

    private static void handleCustomerAtTable(Entity customer, int tableIndex) {
        // El cliente permanece en la mesa por un tiempo aleatorio
        runOnce(() -> {
            animateCustomerExit(customer);
            tableOccupied.set(tableIndex, false);
        }, Duration.seconds(10 + random.nextInt(10)));
    }

    private static void animateCustomerExit(Entity customer) {
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

    private static int findAvailableTable() {
        List<Integer> availableTables = new ArrayList<>();
        for (int i = 0; i < tableOccupied.size(); i++) {
            if (!tableOccupied.get(i)) {
                availableTables.add(i);
            }
        }

        if (availableTables.isEmpty()) {
            return -1;
        }

        return availableTables.get(random.nextInt(availableTables.size()));
    }
}