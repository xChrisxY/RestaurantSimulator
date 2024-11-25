package fxglapp;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import fxglapp.cliente.CustomerFactory;
import fxglapp.table.RestaurantManager;
import fxglapp.ui.FloorFactory;
import javafx.geometry.Point2D;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import javafx.animation.Interpolator;
import javafx.util.Duration;

public class FXGLGameApp extends GameApplication {

    private static final int TILE_SIZE = 64;
    private static final Queue<Entity> waitingCustomers = new ConcurrentLinkedQueue<>();
    private static final int MAX_WAITING_CUSTOMERS = 5;
    private boolean isSpawningPaused = false;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(850);
        settings.setHeight(670);
        settings.setTitle("Restaurant Game");
    }
    private static final int[][] TABLE_POSITIONS = {
            {3, 3}, {5, 3}, {7, 3}, {9, 3},
            {3, 5}, {5, 5}, {7, 5}, {9, 5},
            {3, 7}, {5, 7}, {7, 7}, {9, 7}
    };

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

        spawn("tableCook", new SpawnData(chefTableX1, chefTableY1)); // Primera mesa
        spawn("tableCook", new SpawnData(chefTableX2, chefTableY2)); // Segunda mesa

        spawn("cooker", new SpawnData(chefTableX1, chefTableY1-50)); // Primera mesa
        spawn("cooker", new SpawnData(chefTableX2, chefTableY2-50)); // Segunda mesa

        spawn("kitchenset", new SpawnData(500, 25)); // Segunda mesa
        spawn("kitchenset", new SpawnData(200, 25)); // Segunda mesa

        spawn("dishes", new SpawnData(730, 25)); // Segunda mesa
        spawn("drinkbox", new SpawnData(400, 25)); // Segunda mesa

        int yPositonWall = 0;
        for (int x = 0; x <= 5; x++) {
            spawn("wall", new SpawnData(120, yPositonWall));
            yPositonWall += 90;

        }

        spawn("door", new SpawnData(120, 540)); // Segunda mesa
        spawn("open", new SpawnData(65, 500));
        spawn("tejado", new SpawnData(105, 200)); // Segunda mesa
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

        // Inicializar el RestaurantManager con las posiciones de las mesas
        RestaurantManager.initialize(TILE_SIZE);

        // Generar un cliente inicial
        spawnCustomer();

        // Generar clientes periódicamente
        run(() -> {
            if (!isSpawningPaused) {
                spawnCustomer();
            }
        }, Duration.seconds(0.5));

        // Monitor de capacidad del restaurante
        run(() -> {
            isSpawningPaused = waitingCustomers.size() >= MAX_WAITING_CUSTOMERS;
        }, Duration.seconds(0.1));
    }

    private void spawnCustomer() {
        Entity customer = spawn("client_1", 65, 0);

        // Primera animación: mover hacia la entrada
        moveCustomerToEntrance(customer);
    }

    private void handleCustomerArrival(Entity customer) {
        if (RestaurantManager.hasAvailableTable()) {
            // Si hay mesa disponible, mover al cliente directamente
            Point2D tablePosition = RestaurantManager.assignTableToCustomer(customer);
            if (tablePosition != null) {
                moveCustomerToTable(customer, tablePosition);
            }
        } else if (waitingCustomers.size() < MAX_WAITING_CUSTOMERS) {
            // Si no hay mesa pero hay espacio para esperar
            waitingCustomers.offer(customer);
            moveCustomerToWaitingArea(customer, waitingCustomers.size());
        } else {
            // Si no hay espacio, el cliente se va
            moveCustomerOut(customer);
        }
    }


    private void moveCustomerToEntrance(Entity customer) {
        for (int x = 0; x <= 20; x++) {
            final int step = x;
            runOnce(() -> {
                entityBuilder()
                        .at(customer.getPosition())
                        .with(new ProjectileComponent(new Point2D(0, 1), 100))
                        .buildAndAttach();

                customer.translate(0, 25);

                // Cuando llegue a la entrada, intentar asignar mesa o esperar
                if (step == 20) {
                    handleCustomerArrival(customer);
                }
            }, Duration.seconds(0.2 * x));
        }
    }

    private void moveCustomerToWaitingArea(Entity customer, int position) {
        // Calcular posición en el área de espera basada en la posición en la cola
        double waitingX = 65;
        double waitingY = 300 + (position * 50); // Espaciado vertical entre clientes en espera

        animationBuilder()
                .duration(Duration.seconds(1))
                .interpolator(Interpolators.SMOOTH.EASE_OUT())
                .translate(customer)
                .to(new Point2D(waitingX, waitingY))
                .buildAndAttach();

        // Registrar el cliente para ser notificado cuando haya mesa disponible
        RestaurantManager.addWaitingCustomer(customer, () -> {
            Point2D tablePosition = RestaurantManager.assignTableToCustomer(customer);
            if (tablePosition != null) {
                waitingCustomers.remove(customer);
                moveCustomerToTable(customer, tablePosition);
            }
        });
    }

    private void moveCustomerToTable(Entity customer, Point2D tablePosition) {
        // Primero mover en X
        double diffX = tablePosition.getX() - customer.getX();
        double diffY = tablePosition.getY() - customer.getY();

        animationBuilder()
                .duration(Duration.seconds(1))
                .interpolator(Interpolators.SMOOTH.EASE_OUT())
                .translate(customer)
                .to(new Point2D(tablePosition.getX(), customer.getY()))
                .onFinished(() -> {
                    // Luego mover en Y
                    animationBuilder()
                            .duration(Duration.seconds(1))
                            .interpolator(Interpolators.SMOOTH.EASE_OUT())
                            .translate(customer)
                            .to(tablePosition)
                            .onFinished(() -> {
                                // Cuando el cliente llega a la mesa, programar su salida
                                RestaurantManager.handleCustomerSeated(customer, () -> {
                                    moveCustomerOut(customer);
                                });
                            })
                            .buildAndAttach();
                })
                .buildAndAttach();
    }

    private void moveCustomerOut(Entity customer) {
        Point2D exitPoint = new Point2D(65, getAppHeight() + 100);

        animationBuilder()
                .duration(Duration.seconds(2))
                .interpolator(Interpolators.SMOOTH.EASE_IN())
                .translate(customer)
                .to(exitPoint)
                .onFinished(() -> {
                    customer.removeFromWorld();
                    RestaurantManager.handleCustomerLeft();
                })
                .buildAndAttach();
    }



    public static void main(String[] args) {
        launch(args);
    }
}