package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.util.DatabaseUtil;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseUtil.initDatabase(); // 👈 cria a BD se não existir

        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/main_view.fxml")
        );

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/mainview.css");

        stage.setTitle("Gestor de Despesas");
        stage.setScene(scene);
        stage.show();
    }
}