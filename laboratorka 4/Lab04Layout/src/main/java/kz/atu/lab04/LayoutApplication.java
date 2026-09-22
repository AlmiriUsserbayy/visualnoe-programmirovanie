package kz.atu.lab04;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LayoutApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LayoutApplication.class.getResource("layout-view.fxml"));
        // Ставим минимальный размер по методичке (Пункт 13)
        stage.setMinWidth(760);
        stage.setMinHeight(520);

        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Панель управления студентами");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
