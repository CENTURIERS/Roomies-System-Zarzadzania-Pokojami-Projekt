package com.roomies;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class RoomiesApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RoomiesApp.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 940, 600);

        stage.setTitle("Roomies");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            com.roomies.util.HibernateUtil.getSessionFactory();
            System.out.println("Hibernate SessionFactory zainicjalizowany pomyślnie.");
        } catch (Throwable ex) {
            System.err.println("Krytyczny błąd inicjalizacji Hibernate: " + ex);
            ex.printStackTrace();
        }
        launch(args);
    }
}