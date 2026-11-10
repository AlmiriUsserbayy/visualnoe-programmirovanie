package kz.atu.lab11;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Точка входа приложения «Учёт финансов». */
public class FinanceApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                FinanceApplication.class.getResource("finance-view.fxml"));
        Scene scene = new Scene(loader.load(), 960, 620);
        stage.setTitle("Лабораторная работа №11 — Учёт финансов");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
