package unsw.gloriaromanus;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GloriaRomanusApplication  {

  private static GloriaRomanusController controller;

  private Stage stage;
  private Scene scene;

  
  public GloriaRomanusApplication(Stage stage) throws IOException {
    this.stage = stage;
    // set up the scene
    FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
    Parent root = loader.load();
    controller = loader.getController();
    scene = new Scene(root);
  }
  
  public void start() {
    // set up the stage
    stage.setTitle("Gloria Romanus");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();

  }

  /**
   * Stops and releases all resources used in application.
   */
  // @Override
  // public void stop() {
  //   controller.terminate();
  // }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {
    Application.launch(args);
  }
}
