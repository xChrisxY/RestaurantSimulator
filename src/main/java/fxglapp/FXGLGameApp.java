package fxglapp;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import fxglapp.cliente.CustomerFactory;
import fxglapp.ui.FloorFactory;
import javafx.geometry.Point2D;
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

        //generateCustomers();
        spawnCustomer();
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

        int randomTableIndex = (int)(Math.random() * TABLE_POSITIONS.length);
        int[] targetTable = TABLE_POSITIONS[randomTableIndex];

        double targetX = targetTable[0] * TILE_SIZE;
        double targetY = targetTable[1] * TILE_SIZE;

        Point2D currentPos = customer.getPosition();

        double diffX = targetX - currentPos.getX();
        double diffY = targetY - currentPos.getY();

        int stepsX = (int)Math.abs(diffX / 25);
        if (stepsX > 0) {
            for (int i = 0; i <= stepsX; i++) {
                final int step = i;
                runOnce(() -> {
                    entityBuilder()
                            .at(customer.getPosition())
                            .with(new ProjectileComponent(new Point2D(diffX > 0 ? 1 : -1, 0), 100))
                            .buildAndAttach();

                    customer.translate(diffX > 0 ? 25 : -25, 0);

                    if (step == stepsX) {
                        moveCustomerY(customer, diffY);
                    }
                }, Duration.seconds(0.2 * i));
            }
        } else {

            moveCustomerY(customer, diffY);
        }
    }

    private void moveCustomerY(Entity customer, double diffY) {
        int stepsY = (int)Math.abs(diffY / 25);
        for (int i = 0; i <= stepsY; i++) {
            runOnce(() -> {
                entityBuilder()
                        .at(customer.getPosition())
                        .with(new ProjectileComponent(new Point2D(0, diffY > 0 ? 1 : -1), 100))
                        .buildAndAttach();

                customer.translate(0, diffY > 0 ? 25 : -25);
            }, Duration.seconds(0.2 * i));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}