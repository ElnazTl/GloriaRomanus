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
  @Override
  public void stop() {
    controller.terminate();
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }
}

// package unsw.dungeon;

// import java.io.File;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;

// import javafx.animation.KeyFrame;
// import javafx.animation.Timeline;
// import javafx.beans.value.ChangeListener;
// import javafx.beans.value.ObservableValue;
// import javafx.collections.FXCollections;
// import javafx.event.ActionEvent;
// import javafx.fxml.FXML;
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.control.ChoiceBox;
// import javafx.scene.control.Label;
// import javafx.scene.control.TextArea;
// import javafx.scene.input.KeyEvent;
// import javafx.scene.layout.GridPane;
// import javafx.stage.Stage;
// import javafx.util.Duration;
// import unsw.dungeon.entities.Enemy;
// import unsw.dungeon.entities.Entity;

// /**
//  * A JavaFX controller for the dungeon.
//  * 
//  * @author Robert Clifton-Everest
//  *
//  */
// public class StartMenuController {

// 	@FXML
// 	private Button startButton;

// 	@FXML
// 	private ChoiceBox<String> levelSelect;

// 	int levelIndex = 0;
//     List<String> levels = new ArrayList<String>();

// 	@FXML
// 	public void initialize() {
		
// 		File dir = new File("dungeons/");
// 		if (dir.isDirectory()) {
// 			for (File file : dir.listFiles()) {
// 				if (file.getName().endsWith(".json")) {
// 					levels.add(file.getName());
// 				}
// 			}
// 		}

// 		levelSelect.setItems(FXCollections.observableArrayList(levels));

// 		levelSelect.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
// 			@Override
// 			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
// 				levelIndex = newValue.intValue();
// 			}
// 		});
// 		levelSelect.getSelectionModel().select(4);
// 	}

// 	@FXML
// 	public void handleStartButton(ActionEvent event) {
//         try {
//             String level = levels.get(levelIndex);
// 			Stage stage = new Stage();
//             stage.setTitle(level);
			
// 			DungeonControllerLoader dungeonLoader = new DungeonControllerLoader(level);

// 			DungeonController controller = dungeonLoader.loadController();
			
// 			FXMLLoader loader = new FXMLLoader(getClass().getResource("DungeonView.fxml"));
// 			loader.setController(controller);
// 			Parent root = loader.load();
// 			Scene scene = new Scene(root);
// 			root.requestFocus();
// 			stage.setScene(scene);
// 			stage.show();
// 		} catch (Exception ex) {
// 			System.out.println(ex.getStackTrace().toString());
// 		}
// 	}

// }
