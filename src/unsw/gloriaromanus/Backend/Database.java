package unsw.gloriaromanus.Backend;

import org.json.*;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.StackWalker.Option;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import com.esri.arcgisruntime.internal.security.Token;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class Database {

    
    private String loadAddress;

    private Map<String,List<Unit>> provinceUnit;
    private Map<String,List<Unit>> provinceTraining;

    private Map<String,Faction> provinceList;

    private Map<String, ArrayList<Province>> factionList;
    private String address;

    private ArrayList<Player> players;

    // assign default unit to each province 
    public Database() throws IOException {

        address = "/Users/eli/Desktop/t13a-oop/src/unsw/gloriaromanus/initial_province_ownership.json";

        provinceList = setProvinceToOwningFactionMap();
        provinceTraining = setProvinceTraining();

        provinceUnit = setOwningUnit();
        factionList = setOwningProvince();
        players = new ArrayList<Player>();
        
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public Map<String,List<Unit>> getProvinceTraining() {
        return provinceTraining;
    }

    public Map<String,List<Unit>> getProvinceUnit() {
        return provinceUnit;
    }

    public Map<String,Faction > getFactionProvince() {
        return provinceList;
    }


    public void addFaction(String faction) throws IOException {
        addtoFile(faction, "F", " ");
    }

    public void addProvince(String province, String faction) throws IOException {
        addtoFile(faction, "P", province);
    }

    /**
     * adds faction/province to the database
     * @param faction
     * @param option
     * @param province
     * @throws IOException
     */
    public void addtoFile(String faction, String option, String province) throws IOException {

        String content = Files.readString(Paths.get(address));
        JSONObject ownership = new JSONObject(content);

        if (option.equals("F")) {
            JSONArray empty = new JSONArray();
            ownership.put(faction, empty);
        }

        else {
            Object object = ownership.get(faction);
            JSONArray list = (JSONArray) object;
            list.put(province);
        }
            // Files.write(path, bytes, options)
            Files.writeString(Paths.get(address), ownership.toString());


        
    }

    /**
     * Function initilises the map of province to faction and set the province list for each faction 
     * @return map of province to faction 
     * @throws IOException
     */

    private Map<String, List<Unit>> setOwningUnit() {
        Map<String, List<Unit>> m = new HashMap<String, List<Unit>>();
        for (String provinceName : provinceList.keySet()) {
            ArrayList<Unit> u = new ArrayList<Unit> ();
            m.put(provinceName, u);
          }

          return m;
    }

    private Map<String,Faction> setProvinceToOwningFactionMap() throws IOException {
        ArrayList<Province> ps = new ArrayList<Province> ();
        Map<String,Faction> m = new HashMap<String,Faction>();
        String content = Files.readString(Paths.get(address));
        JSONObject ownership = new JSONObject(content);
        for (String key : ownership.keySet()) {
          // key will be the faction name
          JSONArray ja = ownership.getJSONArray(key);
          // value is province name
          for (int i = 0; i < ja.length(); i++) {
            String value = ja.getString(i);

            Faction f = new Faction(key);
            m.put(value,f);

          }
          
        }
        return m;
      }

      private Map<String,ArrayList<Province>> setOwningProvince() throws IOException {
        Map<String,ArrayList<Province>> m = new HashMap<String,ArrayList<Province>>();
        String content = Files.readString(Paths.get(address));
        JSONObject ownership = new JSONObject(content);
        for (String key : ownership.keySet()) {
            ArrayList<Province> ps = new ArrayList<Province> ();

          // key will be the faction name
          JSONArray ja = ownership.getJSONArray(key);
          // value is province name
          for (int i = 0; i < ja.length(); i++) {
            String value = ja.getString(i);

            Faction f = new Faction(key);
            ps.add(new Province(value,this));

          }
          m.put(key,ps);
          
        }
        return m;
      }
    public Map<String, List<Unit>> setProvinceTraining() {
        Map<String, List<Unit>> m = new HashMap<String,List<Unit>>();
        for (String p: provinceList.keySet()) {
            ArrayList<Unit> u = new ArrayList<Unit>();
            m.put(p,u);
        }
        return m;

    }

    

    public void saveGame() throws IOException {
        OutputStream os = new FileOutputStream("/Users/eli/Desktop/t13a-oop/src/unsw/gloriaromanus/Backend/configs/load.json");
        ObjectMapper om = new ObjectMapper();
        OutputStream os1 = new FileOutputStream("/Users/eli/Desktop/t13a-oop/src/unsw/gloriaromanus/Backend/configs/loadPlayer.json");
        JsonGenerator g = om.getFactory().createGenerator(os);
        JsonGenerator g1 = om.getFactory().createGenerator(os1);


        for (Player player:players) {
            om.writeValue(g1,player);
            
        }
        for (String f: factionList.keySet()) {
            for (Province p: factionList.get(f)) {
                saveProvince(f,p, om,g);
            }
        }
      
    }
    public void loadGame() throws IOException {
        
        loadPlayer();
       
        loadProvince();
        }

    /**
     * Will load the saved players
     */

    public void loadPlayer() throws IOException {
        FileReader fis = new FileReader("/Users/eli/Desktop/t13a-oop/src/unsw/gloriaromanus/Backend/configs/loadPlayer.json");
        JsonFactory jf = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper();
        Iterator<Player> value = mapper.readValues( jf.createParser(fis), Player.class);

        while(value.hasNext()) {
            Player p = value.next();
            p.setDatabase(this);
            addPlayer(p);

        }

    }

    public void loadProvince() throws IOException {

        FileReader fis = new FileReader("/Users/eli/Desktop/t13a-oop/src/unsw/gloriaromanus/Backend/configs/load.json");
        JsonFactory jf = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        Iterator<Province> value = mapper.readValues( jf.createParser(fis), Province.class);

        while(value.hasNext()) {
            Province p = value.next();
            List<Unit> u = p.getUnits();
            provinceUnit.put(p.getName(), u);
            provinceList.put(p.getName(), new Faction(p.getFaction()));
            factionList.get(p.getFaction()).add(p);
            provinceTraining.put(p.getName(), p.getUnitsTraining());
            // p.setDatabase(this);
            p.setDatabase(this);
            if (!p.getUnits().isEmpty()) System.out.println(p.getUnits().get(0).getCost());
        }


    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    


   
    
    public void saveProvince(String f, Province p, ObjectMapper om, JsonGenerator g) throws IOException {

        //TODO: save other features of the province in the file 
        om.writeValue(g,p);


    } 

   
   
  

    public String addUnit(String name, String province) throws IOException {

        var unitJSON = new JSONObject("{\r\n    \"soldier\": {\r\n        \"type\" : \"infantry\",\r\n        \"attackType\" : \"melee\",\r\n        \"numTroops\" : 10,\r\n        \"cost\" : 5,\r\n        \"trainTime\" : 1,\r\n        \"attack\" : 4,\r\n        \"morale\" : 5,\r\n        \"shield\" : 3,\r\n        \"defence\" : 6,\r\n        \"ability\" : \"noAbility\"\r\n\r\n    },\r\n    \"horseArcher\": {\r\n        \"type\" : \"cavalry\",\r\n        \"attackType\" : \"ranged\",\r\n        \"numTroops\" : 8,\r\n        \"cost\" : 5,\r\n        \"trainTime\" : 2,\r\n        \"attack\" : 6,\r\n        \"morale\" : 4,\r\n        \"shield\" : 2,\r\n        \"charge\" : 5,\r\n        \"ability\" : \"noAbility\"\r\n    }\r\n}");
        var abilityJSON = new JSONObject("{\r\n    \"noAbility\" : {\r\n        \"friendly\" : [],\r\n        \"enemy\" : []\r\n    },\r\n    \"phalanx\" : {\r\n        \"friendly\" : [\r\n            {\r\n                \"type\" : \"defence\",\r\n                \"value\" : 2,\r\n                \"strategy\" : \"multiply\"\r\n                \r\n            },\r\n            {\r\n                \"type\" : \"speed\",\r\n                \"value\" : 0.5,\r\n                \"strategy\" : \"multiply\"\r\n            }\r\n        ],\r\n        \"enemy\" : []\r\n    }\r\n}");
        Unit u = new Unit(name,unitJSON,abilityJSON);
        Province p = new Province(province,this);
        return p.getUnit(u);

    }


    /**
     * This function will keep track of the training time of different troops and add them to the units when training is over
     */
    public void update(Unit u, String p) {
        provinceTraining.get(p).remove(u);

    }

    public void endTurn() {
        for (Iterator pI = provinceTraining.keySet().iterator(); pI.hasNext();){
            String p = (String)pI.next();
            for (Iterator<Unit> iterator =  provinceTraining.get(p).iterator(); iterator.hasNext();) {
                Unit u = iterator.next();
                u.endTurn();
                System.out.println(u.getName());

                if (u.getTrainTime() == 0) {
                    provinceUnit.get(p).add(u);
                    iterator.remove();
                }
            }
        }
    }
    
}








