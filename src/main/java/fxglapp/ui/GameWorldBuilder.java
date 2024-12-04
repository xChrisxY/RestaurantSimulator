package fxglapp.ui;

import com.almasb.fxgl.entity.SpawnData;
import static com.almasb.fxgl.dsl.FXGL.*;

public class GameWorldBuilder {

    private static final int TILE_SIZE = 64;

    public static void createWorld(boolean[] tableOccupied) {
        createKitchenTiles();
        createConcreteTiles();
        createFloorTiles();
        createTablesAndChairs();
        createKitchenEquipment();
        createWallsAndDecorations();
    }

    private static void createKitchenTiles() {
        for (int x = 0; x < getAppWidth(); x += TILE_SIZE) {
            for (int y = 0; y < TILE_SIZE * 2; y += TILE_SIZE) {
                spawn("kitchenTile", x, y);
            }
        }
    }

    private static void createConcreteTiles() {
        for (int y = 0; y < getAppHeight(); y += TILE_SIZE) {
            spawn("concreteTile", 0, y);
            spawn("concreteTile", TILE_SIZE, y);
        }
    }

    private static void createFloorTiles() {
        for (int x = TILE_SIZE * 2; x < getAppWidth(); x += TILE_SIZE) {
            for (int y = TILE_SIZE * 2; y < getAppHeight(); y += TILE_SIZE) {
                spawn("floorTile",
                        new SpawnData(x, y)
                                .put("isDark", ((x / TILE_SIZE) + (y / TILE_SIZE)) % 2 == 0));
            }
        }
    }

    private static void createTablesAndChairs() {
        int[][] tablePositions = {
                {3, 3}, {5, 3}, {7, 3}, {9, 3},
                {3, 5}, {5, 5}, {7, 5}, {9, 5},
                {3, 7}, {5, 7}, {7, 7}, {9, 7},
        };

        for (int[] pos : tablePositions) {
            int x = pos[0] * TILE_SIZE;
            int y = pos[1] * TILE_SIZE;
            spawn("tableTile", x, y);
            spawn("chair", x + 30, y + 80);
        }
    }

    private static void createKitchenEquipment() {
        int chefTableX1 = 300;
        int chefTableY1 = TILE_SIZE / 2;

        int chefTableX2 = 600;
        int chefTableY2 = TILE_SIZE / 2;

        spawn("tableCook", chefTableX1, chefTableY1);
        spawn("tableCook", chefTableX2, chefTableY2);

        //spawn("cooker", chefTableX1, chefTableY1 - 50);
        //spawn("cooker", chefTableX2, chefTableY2 - 50);

        spawn("kitchenset", 500, 25);
        spawn("kitchenset", 200, 25);

        spawn("dishes", 730, 25);
        spawn("drinkbox", 400, 25);
    }

    private static void createWallsAndDecorations() {
        int yPositonWall = 0;
        for (int x = 0; x <= 5; x++) {
            spawn("wall", 120, yPositonWall);
            yPositonWall += 90;
        }

        spawn("door", 120, 540);
        spawn("open", 65, 500);
        spawn("tejado", 105, 200);
        spawn("bush", 200, 600);
        spawn("trash", 770, 590);
        spawn("lamp", 140, 80);
        spawn("lamp", 140, 435);

        int xPositonWall = 200;
        yPositonWall = 130;
        for (int x = 0; x <= 3; x++) {
            spawn("bush", xPositonWall, 600);
            spawn("bush", 780, yPositonWall);
            yPositonWall += 110;
            xPositonWall += 130;
        }
    }
}
