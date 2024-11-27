package fxglapp.ui;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public class FloorFactory implements EntityFactory {

    private static final int TILE_SIZE = 64;
    private static final Color DARK_TEAL = Color.rgb(38, 70, 83);
    private static final Color LIGHT_TEAL = Color.rgb(42, 157, 143);
    private static final Color CONCRETE_GRAY = Color.rgb(128, 128, 128);
    private static final Color KITCHEN_FLOOR = Color.rgb(38, 70, 83);
    private static final Color LINE_COLOR = Color.rgb(45, 80, 93);


    @Spawns("floorTile")
    public Entity newFloorTile(SpawnData data) {
        boolean isDark = data.get("isDark");
        return entityBuilder(data)
                .view(new Rectangle(TILE_SIZE, TILE_SIZE,
                        isDark ? DARK_TEAL : LIGHT_TEAL))
                .zIndex(-1)
                .build();
    }

    @Spawns("kitchenTile")
    public Entity newKitchenTile(SpawnData data) {
        Group tileGroup = new Group();
        Rectangle base = new Rectangle(TILE_SIZE, TILE_SIZE, KITCHEN_FLOOR);
        tileGroup.getChildren().add(base);

        // Agregar líneas horizontales
        for (int i = 0; i < TILE_SIZE; i += 8) {
            Line line = new Line(0, i, TILE_SIZE, i);
            line.setStroke(LINE_COLOR);
            line.setStrokeWidth(1);
            tileGroup.getChildren().add(line);
        }

        return entityBuilder(data)
                .view(tileGroup)
                .zIndex(-1)
                .build();
    }

    @Spawns("concreteTile")
    public Entity newConcreteTile(SpawnData data) {
        Group tileGroup = new Group();
        Rectangle base = new Rectangle(TILE_SIZE, TILE_SIZE, CONCRETE_GRAY);

        // Agregar textura al concreto
        for (int i = 0; i < 5; i++) {
            double x = Math.random() * TILE_SIZE;
            double y = Math.random() * TILE_SIZE;
            double size = 2 + Math.random() * 4;

            Rectangle speckle = new Rectangle(x, y, size, size);
            speckle.setFill(Color.rgb(100, 100, 100));
            tileGroup.getChildren().add(speckle);
        }

        tileGroup.getChildren().add(0, base);

        return entityBuilder(data)
                .view(tileGroup)
                .zIndex(-1)
                .build();
    }

    @Spawns("tableTile")
    public Entity newTableTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/table.png");
        tableImage.setFitWidth(TILE_SIZE * 1.5);
        tableImage.setFitHeight(TILE_SIZE * 1.5);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("tableCook")
    public Entity newCookTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/tableCook.png");
        tableImage.setFitWidth(TILE_SIZE * 1.5);
        tableImage.setFitHeight(TILE_SIZE * 1.5);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("cooker")
    public Entity newCookerTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/cooker.png");
        tableImage.setFitWidth(TILE_SIZE * 1.7);
        tableImage.setFitHeight(TILE_SIZE * 1.7);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("chair")
    public Entity newChairTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/chair.png");
        tableImage.setFitWidth(TILE_SIZE * 0.5);
        tableImage.setFitHeight(TILE_SIZE * 0.5);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("kitchenset")
    public Entity newKitchenSetTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/kitchenset.png");
        tableImage.setFitWidth(TILE_SIZE * 1.5);
        tableImage.setFitHeight(TILE_SIZE * 1.5);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("dishes")
    public Entity newDishesTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/dishes.png");
        tableImage.setFitWidth(TILE_SIZE * 1.5);
        tableImage.setFitHeight(TILE_SIZE * 1.5);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("drinkbox")
    public Entity newDrinkBoxTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/drinkbox.png");
        tableImage.setFitWidth(TILE_SIZE * 1.5);
        tableImage.setFitHeight(TILE_SIZE * 1.5);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("wall")
    public Entity newWallTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/wall.png");
        tableImage.setFitWidth(20);
        tableImage.setFitHeight(TILE_SIZE * 1.5);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("door")
    public Entity newDoorTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/door.png");
        tableImage.setFitWidth(20);
        tableImage.setFitHeight(TILE_SIZE * 1.9);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("tejado")
    public Entity newTejadoTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/tejado.png");
        tableImage.setFitWidth(35);
        tableImage.setFitHeight(TILE_SIZE * 3.5);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("bush")
    public Entity newBushTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/bush.png");
        tableImage.setFitWidth(55);
        tableImage.setFitHeight(64);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("trash")
    public Entity newTrashTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/trash.png");
        tableImage.setFitWidth(55);
        tableImage.setFitHeight(64);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("open")
    public Entity newOpenTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/open.png");
        tableImage.setFitWidth(75);
        tableImage.setFitHeight(64);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("lamp")
    public Entity newLampTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/lamp.png");
        tableImage.setFitWidth(35);
        tableImage.setFitHeight(64);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("waiter")
    public Entity newWaiterTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/waiter.png");
        tableImage.setFitWidth(TILE_SIZE * 1.7);
        tableImage.setFitHeight(TILE_SIZE * 1.7);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }

    @Spawns("waiter_order")
    public Entity newWaiterOrderTile(SpawnData data) {
        ImageView tableImage = new ImageView("assets/waiter_order.png");
        tableImage.setFitWidth(TILE_SIZE * 1.7);
        tableImage.setFitHeight(TILE_SIZE * 1.7);

        return entityBuilder(data)
                .view(tableImage)
                .zIndex(0)
                .build();
    }


}

