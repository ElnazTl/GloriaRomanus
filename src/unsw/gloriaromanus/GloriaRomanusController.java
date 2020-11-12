

package unsw.gloriaromanus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;
import com.esri.arcgisruntime.data.Feature;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.util.Pair;
import unsw.gloriaromanus.Backend.*;

public class GloriaRomanusController{

  @FXML
  private MapView mapView;

  @FXML
  private StackPane stackPaneMain;

  // could use ControllerFactory?
  private ArrayList<Pair<MenuController, VBox>> controllerParentPairs;

  private ArcGISMap map;

  private Map<String, String> provinceToOwningFactionMap;

  private Map<String, Integer> provinceToNumberTroopsMap;

  private String humanFaction;

  private Feature currentlySelectedHumanProvince;
  private Feature currentlySelectedEnemyProvince;

  private FeatureLayer featureLayer_provinces;

  private Database db;

  private Player player;

  private currentStatusController status;

  private Map<String,MenuController> menusList;

  @FXML
  private void initialize() throws JsonParseException, JsonMappingException, IOException, InterruptedException {
    // TODO = you should rely on an object oriented design to determine ownership
    provinceToOwningFactionMap = getProvinceToOwningFactionMap();

    provinceToNumberTroopsMap = new HashMap<String, Integer>();

    // Random r = new Random();
    for (String provinceName : provinceToOwningFactionMap.keySet()) {
      provinceToNumberTroopsMap.put(provinceName, 0);
    }
    /**
     * set up list of observers in provinces
     */
   
    db = new Database();


    currentlySelectedHumanProvince = null;
    currentlySelectedEnemyProvince = null;

    String []menus = {"signupPane.fxml","currentStatusController","Action.fxml","invasion_menu.fxml", "basic_menu.fxml"};
    controllerParentPairs = new ArrayList<Pair<MenuController, VBox>>();

    menusList = new HashMap<String,MenuController>();


    setMenu();

    stackPaneMain.getChildren().add(controllerParentPairs.get(0).getValue());

    initializeProvinceLayers();

  }

  /**
   * setting vbox menus
   * TODO: add more menus 
   */
  private void setMenu() throws IOException {

    String []menus = {"signupPane.fxml","currentStatus.fxml","Action.fxml","invasion_menu.fxml", "basic_menu.fxml"};

    for (String fxmlName: menus){
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
      VBox root = (VBox)loader.load();
      MenuController menuController = (MenuController)loader.getController();
      menuController.setParent(this);
      controllerParentPairs.add(new Pair<MenuController, VBox>(menuController, root));

      menusList.put(menuController.getClass().getName(),menuController);
    }
    status = (currentStatusController)controllerParentPairs.get(1).getKey();



  }
  /**
   * TODO: Player selecting units in the province to attack
   */
  public void clickedInvadeButton(ActionEvent e) throws IOException {
    if (currentlySelectedHumanProvince != null && currentlySelectedEnemyProvince != null){
      String humanProvince = (String)currentlySelectedHumanProvince.getAttributes().get("name");
      String enemyProvince = (String)currentlySelectedEnemyProvince.getAttributes().get("name");
      player.selectProvince(humanProvince);
      player.trainUnit("soldier");
      player.selectUnit(0L);
      int result = player.invade(enemyProvince);
      if ( result == -1) printMessageToTerminal("You lost the battle");
      if (result == 0) printMessageToTerminal("It's a tie!");
      if (result == 1) printMessageToTerminal("Congradulation you won the battle");
     
        resetSelections();  // reset selections in UI
        addAllPointGraphics(); // reset graphics
      }
     

    }
    //selecting to provinces from the same faction to move too
    public void MoveUnit(ActionEvent e) throws IOException {
      if (currentlySelectedHumanProvince != null && currentlySelectedEnemyProvince != null){
        String humanProvince = (String)currentlySelectedHumanProvince.getAttributes().get("name");
        String enemyProvince = (String)currentlySelectedEnemyProvince.getAttributes().get("name");
      }
    }
  

  /**
   * run this initially to update province owner, change feature in each
   * FeatureLayer to be visible/invisible depending on owner. Can also update
   * graphics initially
   */
  private void initializeProvinceLayers() throws JsonParseException, JsonMappingException, IOException {

    Basemap myBasemap = Basemap.createImagery();
    // myBasemap.getReferenceLayers().remove(0);
    map = new ArcGISMap(myBasemap);
    mapView.setMap(map);

    // note - tried having different FeatureLayers for AI and human provinces to
    // allow different selection colors, but deprecated setSelectionColor method
    // does nothing
    // so forced to only have 1 selection color (unless construct graphics overlays
    // to give color highlighting)
    GeoPackage gpkg_provinces = new GeoPackage("src/unsw/gloriaromanus/provinces_right_hand_fixed.gpkg");
    gpkg_provinces.loadAsync();
    gpkg_provinces.addDoneLoadingListener(() -> {
      if (gpkg_provinces.getLoadStatus() == LoadStatus.LOADED) {
        // create province border feature
        featureLayer_provinces = createFeatureLayer(gpkg_provinces);
        map.getOperationalLayers().add(featureLayer_provinces);

      } else {
        System.out.println("load failure");
      }
    });

    addAllPointGraphics();
  }

  private void addAllPointGraphics() throws JsonParseException, JsonMappingException, IOException {
    mapView.getGraphicsOverlays().clear();

    InputStream inputStream = new FileInputStream(new File("src/unsw/gloriaromanus/provinces_label.geojson"));
    FeatureCollection fc = new ObjectMapper().readValue(inputStream, FeatureCollection.class);

    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

    for (org.geojson.Feature f : fc.getFeatures()) {
      if (f.getGeometry() instanceof org.geojson.Point) {
        org.geojson.Point p = (org.geojson.Point) f.getGeometry();
        LngLatAlt coor = p.getCoordinates();
        Point curPoint = new Point(coor.getLongitude(), coor.getLatitude(), SpatialReferences.getWgs84());
        PictureMarkerSymbol s = null;
        String province = (String) f.getProperty("name");
        String faction = provinceToOwningFactionMap.get(province);

        TextSymbol t = new TextSymbol(10,
            faction + "\n" + province + "\n" + provinceToNumberTroopsMap.get(province), 0xFFFF0000,
            HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);

        switch (faction) {
          case "Gaul":
            // note can instantiate a PictureMarkerSymbol using the JavaFX Image class - so could
            // construct it with custom-produced BufferedImages stored in Ram
            // http://jens-na.github.io/2013/11/06/java-how-to-concat-buffered-images/
            // then you could convert it to JavaFX image https://stackoverflow.com/a/30970114

            // you can pass in a filename to create a PictureMarkerSymbol...
            s = new PictureMarkerSymbol(new Image((new File("images/Celtic_Druid.png")).toURI().toString()));
            break;
          case "Rome":
            // you can also pass in a javafx Image to create a PictureMarkerSymbol (different to BufferedImage)
            s = new PictureMarkerSymbol("images/legionary.png");
            break;
          // TODO = handle all faction names, and find a better structure...
        }
        t.setHaloColor(0xFFFFFFFF);
        t.setHaloWidth(2);
        // Graphic gPic = new Graphic(curPoint, s);
        Graphic gText = new Graphic(curPoint, t);
        // graphicsOverlay.getGraphics().add(gPic);
        graphicsOverlay.getGraphics().add(gText);
      } else {
        System.out.println("Non-point geo json object in file");
      }

    }

    inputStream.close();
    mapView.getGraphicsOverlays().add(graphicsOverlay);
  }

  private FeatureLayer createFeatureLayer(GeoPackage gpkg_provinces) {
    FeatureTable geoPackageTable_provinces = gpkg_provinces.getGeoPackageFeatureTables().get(0);

    // Make sure a feature table was found in the package
    if (geoPackageTable_provinces == null) {
      System.out.println("no geoPackageTable found");
      return null;
    }

    // Create a layer to show the feature table
    FeatureLayer flp = new FeatureLayer(geoPackageTable_provinces);

    // https://developers.arcgis.com/java/latest/guide/identify-features.htm
    // listen to the mouse clicked event on the map view
    mapView.setOnMouseClicked(e -> {
      // was the main button pressed?
      if (e.getButton() == MouseButton.PRIMARY) {
        // get the screen point where the user clicked or tapped
        Point2D screenPoint = new Point2D(e.getX(), e.getY());

        // specifying the layer to identify, where to identify, tolerance around point,
        // to return pop-ups only, and
        // maximum results
        // note - if select right on border, even with 0 tolerance, can select multiple
        // features - so have to check length of result when handling it
        final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(flp,
            screenPoint, 0, false, 25);

        // add a listener to the future
        identifyFuture.addDoneListener(() -> {
          try {
            // get the identify results from the future - returns when the operation is
            // complete
            IdentifyLayerResult identifyLayerResult = identifyFuture.get();
            // a reference to the feature layer can be used, for example, to select
            // identified features
            if (identifyLayerResult.getLayerContent() instanceof FeatureLayer) {
              FeatureLayer featureLayer = (FeatureLayer) identifyLayerResult.getLayerContent();
              // select all features that were identified
              List<Feature> features = identifyLayerResult.getElements().stream().map(f -> (Feature) f).collect(Collectors.toList());

              if (features.size() > 1){
                printMessageToTerminal("Have more than 1 element - you might have clicked on boundary!");
              }
              else if (features.size() == 1){
                // note maybe best to track whether selected...
                Feature f = features.get(0);
                String province = (String)f.getAttributes().get("name");

                if (provinceToOwningFactionMap.get(province).equals(humanFaction)){
                  // province owned by human
                  if (currentlySelectedHumanProvince != null){
                    featureLayer.unselectFeature(currentlySelectedHumanProvince);
                  }
                  currentlySelectedHumanProvince = f;
                  if (controllerParentPairs.get(1).getKey() instanceof InvasionMenuController){
                    ((InvasionMenuController)controllerParentPairs.get(1).getKey()).setInvadingProvince(province);
                  }

                }
                else{
                  if (currentlySelectedEnemyProvince != null){
                    featureLayer.unselectFeature(currentlySelectedEnemyProvince);
                  }
                  currentlySelectedEnemyProvince = f;
                  if (controllerParentPairs.get(1).getKey() instanceof InvasionMenuController){
                    ((InvasionMenuController)controllerParentPairs.get(1).getKey()).setOpponentProvince(province);
                  }
                }

                featureLayer.selectFeature(f);                
              }

              
            }
          } catch (InterruptedException | ExecutionException ex) {
            // ... must deal with checked exceptions thrown from the async identify
            // operation
            System.out.println("InterruptedException occurred");
          }
        });
      }
    });
    return flp;
  }

  private Map<String, String> getProvinceToOwningFactionMap() throws IOException {
    String content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership.json"));
    JSONObject ownership = new JSONObject(content);
    Map<String, String> m = new HashMap<String, String>();
    for (String key : ownership.keySet()) {
      // key will be the faction name
      JSONArray ja = ownership.getJSONArray(key);
      // value is province name
      for (int i = 0; i < ja.length(); i++) {
        String value = ja.getString(i);
        m.put(value, key);
      }
    }
    return m;
  }

  private ArrayList<String> getHumanProvincesList() throws IOException {
    // https://developers.arcgis.com/labs/java/query-a-feature-layer/

    String content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership.json"));
    JSONObject ownership = new JSONObject(content);
    return ArrayUtil.convert(ownership.getJSONArray(humanFaction));
  }

  /**
   * returns query for arcgis to get features representing human provinces can
   * apply this to FeatureTable.queryFeaturesAsync() pass string to
   * QueryParameters.setWhereClause() as the query string
   */
  private String getHumanProvincesQuery() throws IOException {
    LinkedList<String> l = new LinkedList<String>();
    for (String hp : getHumanProvincesList()) {
      l.add("name='" + hp + "'");
    }
    return "(" + String.join(" OR ", l) + ")";
  }

  private boolean confirmIfProvincesConnected(String province1, String province2) throws IOException {
    String content = Files.readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
    JSONObject provinceAdjacencyMatrix = new JSONObject(content);
    return provinceAdjacencyMatrix.getJSONObject(province1).getBoolean(province2);
  }

  private void resetSelections(){
    featureLayer_provinces.unselectFeatures(Arrays.asList(currentlySelectedEnemyProvince, currentlySelectedHumanProvince));
    currentlySelectedEnemyProvince = null;
    currentlySelectedHumanProvince = null;
    if (controllerParentPairs.get(0).getKey() instanceof InvasionMenuController){
      ((InvasionMenuController)controllerParentPairs.get(0).getKey()).setInvadingProvince("");
      ((InvasionMenuController)controllerParentPairs.get(0).getKey()).setOpponentProvince("");
    }
  }

  private void printMessageToTerminal(String message){
    if (controllerParentPairs.get(0).getKey() instanceof InvasionMenuController){
      ((InvasionMenuController)controllerParentPairs.get(0).getKey()).appendToTerminal(message);
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {
    if (mapView != null) {
      mapView.dispose();
    }
  }
  // test commend
  public void switchMenu() throws JsonParseException, JsonMappingException, IOException {
    System.out.println("trying to switch menu");
    stackPaneMain.getChildren().remove(controllerParentPairs.get(0).getValue());
    Collections.reverse(controllerParentPairs);
    stackPaneMain.getChildren().add(controllerParentPairs.get(0).getValue());
  }

  public void nextMenu(String current, String next) throws JsonParseException, JsonMappingException, IOException {
    
    MenuController mcr = menusList.get(current);
    MenuController mca = menusList.get(next);
    int indexRemove = 0;
    int indexAdd = 0;
    for (int i  = 0; i < controllerParentPairs.size();i++) {

      if (controllerParentPairs.get(i).getKey().equals(mcr)) {
        indexRemove = i;
      }
      if (controllerParentPairs.get(i).getKey().equals(mca)) indexAdd = i;
    }
    System.out.println("loooooook"+ controllerParentPairs.get(indexAdd).getKey().getClass().getName()+" "+mca.getClass().getName() +" ");

    stackPaneMain.getChildren().removeAll(controllerParentPairs.get(indexRemove).getValue(),controllerParentPairs.get(1).getValue());
    stackPaneMain.getChildren().addAll(controllerParentPairs.get(indexAdd).getValue(),controllerParentPairs.get(1).getValue());
  }

  /**
   * register user and add it to the database
   * @param user_name
   * @param faction
   */

  public void registerUser (String user_name, String faction) {
    

    Player p = db.addNewPlayer(user_name, faction);
    if (p==null) ((SignupPaneController)controllerParentPairs.get(0).getKey()).appendToTerminal("invalid user name or faction");
    else ((SignupPaneController)controllerParentPairs.get(0).getKey()).appendToTerminal("successfully joined");
   
    
  }
  /**
   * starting the game and assigning the current player of the game 
   */
  public void startGame() throws IOException {
    //TODO: add UI feature for this event handler 
    if (db.startGame().equals("start")) {
      nextMenu("unsw.gloriaromanus.SignupPaneController","unsw.gloriaromanus.ActionController");
      player = db.getCurrentPlayer();
      humanFaction = player.getFaction().getName();
      ((SignupPaneController)controllerParentPairs.get(0).getKey()).appendToTerminal("successfully started the game");
      subscribe();
      status.setName(player.getUsername());
      status.setYear(db.getGameYear());
      
    }
    else ((SignupPaneController)controllerParentPairs.get(0).getKey()).appendToTerminal(db.startGame());
  }

  private Observer observer;
  private FactionObserver factionObserver;
  /**
   * subscribing to provinces that the players are playing with
   */
  public void subscribe() throws JsonParseException, JsonMappingException, IOException{
    observer = (province) -> {
      provinceToNumberTroopsMap.put(province.getName(),province.getNTroops());
      addAllPointGraphics();
    };

    factionObserver = (faction) -> {
      List<Province> pro = faction.getProvinces();
      for (Province p: pro) {
        provinceToOwningFactionMap.put(p.getName(),faction.getName());
      
      }
      addAllPointGraphics();

    };
    for (Player p: db.getPlayers()) {
      p.getFaction().subscribe(factionObserver);
      for (Province pro: p.getFaction().getProvinces()) {
        pro.subscribe(observer);
      }
    }
  }
 /**
  * given the unit, train the unit for selected province
  * TODO: price implementation
  * TODO: fix the messages for display --> UI
  * @param unit
  */
  public void trainUnit(String unit) throws IOException {

    String humanProvince = (String)currentlySelectedHumanProvince.getAttributes().get("name");
    player.selectProvince(humanProvince);
    if(player.trainUnit(unit) == -1) System.out.println("could not add the unit you alraedy have two units training");
    else System.out.println("successfull! currently training the units they will be available from the next round");

  }

  /**
   * player finishing their turn
   */

  public void endTurn()  throws JsonParseException, JsonMappingException, IOException {
    player.endTurn();
    player = db.getCurrentPlayer();
    humanFaction = player.getFaction().getName();
    status.setName(player.getUsername());
    status.setYear(db.getGameYear());
  }
  public String setName() {
    return player.getUsername();
  }

  
}
