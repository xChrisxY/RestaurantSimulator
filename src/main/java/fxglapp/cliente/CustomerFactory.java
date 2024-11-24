package fxglapp.cliente;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class CustomerFactory implements EntityFactory {

    public enum EntityType {
        CUSTOMER
    }

    private static final int TILE_SIZE = 64;

    @Spawns("customer")
    public Entity newCustomer(SpawnData data) {
        ImageView customerView = new ImageView("assets/customer.png");
        customerView.setFitWidth(TILE_SIZE);
        customerView.setFitHeight(TILE_SIZE);

        return entityBuilder(data)
                .type(EntityType.CUSTOMER)
                .viewWithBBox(customerView)
                .collidable()
                .zIndex(1)
                .build();
    }
}