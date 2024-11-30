package fxglapp.cliente;

import com.almasb.fxgl.entity.Entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CustomerEntity extends Entity {

    public CustomerEntity() {
        ImageView imageView = new ImageView(new Image("assets/client_1.png"));

        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        getViewComponent().addChild(imageView);
    }

}


