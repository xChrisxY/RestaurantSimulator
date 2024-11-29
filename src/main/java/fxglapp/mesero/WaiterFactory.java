package fxglapp.mesero;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGL.*;

public class WaiterFactory implements EntityFactory {

    private static final int TILE_SIZE = 64;

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
