package fxglapp.cocinero;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class CookerFactory implements EntityFactory {

    private static final int TILE_SIZE = 64;

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

}
