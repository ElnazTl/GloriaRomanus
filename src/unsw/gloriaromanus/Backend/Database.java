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

    

    private Map<String,List<Unit>> provinceUnit;
    private Map<String,List<Unit>> provinceTraining;

    private Map<String,Faction> provinceList;

    private Map<String, ArrayList<Province>> factionList;
    private Map<String, Faction> factions;
    private Map<Player, Faction> playerFactions;


    private String address;
    private String loadProvince;
    private String loadPlayer;
    private String loadFaction;


    private ArrayList<Player> players;

    // assign default unit to each province 
    public Database(String a) throws IOException {
        setAddress(a);
        
        provinceUnit = new HashMap<String,List<Unit>>();
        provinceTraining = new HashMap<String,List<Unit>>();

        
        factions = setFaction();
        provinceList = setProvinceToOwningFactionMap();

        factionList = setOwningProvince();
        players = new ArrayList<Player>();



        playerFactions = new HashMap<Player, Faction>();

        
    }

    public void setAddress(String a) {
        if (a.equals("test"))  {
            address = "bin/unsw/gloriaromanus/initial_province_ownership.json";
            loadProvince = "bin/unsw/gloriaromanus/Backend/configs/load.json";
            loadPlayer = "bin/unsw/gloriaromanus/Backend/configs/loadPlayer.json";
            loadFaction = "bin/unsw/gloriaromanus/Backend/configs/loadFaction.json";

           
            
        }
        else {
            address = "src/unsw/gloriaromanus/initial_province_ownership.json";
            loadProvince = "src/unsw/gloriaromanus/Backend/configs/load.json";
            loadPlayer = "src/unsw/gloriaromanus/Backend/configs/loadPlayer.json";
            loadFaction = "src/unsw/gloriaromanus/Backend/configs/loadFaction.json";

        }
    }
    public List<String> availableFactions() {
        List<String> list = new ArrayList<String>();
        for (Faction f : factions.values()) {
            if (!playerFactions.containsValue(f)) {
                list.add(f.getName());
            }
        }
        return list;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player p) throws IOException {
        players.add(p);
        // Faction f = new Faction(this,p.getFaction(),0,factionList.get(p.getFaction()));
        // playerFactions.put(p,f);
    }


    

    public Province findProvince(String name) {
        Province p = null;
        for (Faction f : playerFactions.values()) {
            p = f.findProvince(name);
            if (p != null) break;
        }
        return p;
    }

    public int invade(Province attacker, String enemy) {
        Province defender = findProvince(enemy);
        int result = BattleResolver.battle(attacker, defender);
        if (result != -1) {
            Faction aFaction = getFactionOfProvince(attacker);
            Faction dFaction = getFactionOfProvince(defender);

            if (result == 1) {
                // Attacker conquered province
                aFaction.addConqueredProvince(defender);
                dFaction.removeProvince(defender);
                return 1;
            }
            // else defender won
            return 0;
        }
        return -1;
    }


    public Faction getFactionOfProvince(Province p) {
        for (Faction f : playerFactions.values()) {
            if (f.findProvince(p.getName()) != null) return f;
        }
        return null;
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

            m.put(value,factions.get(key));

          }
          
        }
        return m;
      }

    private Map<String,Faction> setFaction() throws IOException {
        Map<String,Faction> m = new HashMap<String,Faction>();
        String content = Files.readString(Paths.get(address));
        JSONObject ownership = new JSONObject(content);
        for (String key : ownership.keySet()) {
            JSONArray ja = ownership.getJSONArray(key);
            ArrayList<Province> province = new ArrayList<Province>();
            for (int i = 0; i < ja.length(); i++) {
                ArrayList<Unit> u= new ArrayList<Unit>();
                ArrayList<Unit> t= new ArrayList<Unit>();

                provinceUnit.put(ja.getString(i),u);
                provinceTraining.put(ja.getString(i),t);
                province.add(new Province(ja.getString(i),this));
            }
            m.put(key,new Faction(this,key,0,province));
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

        OutputStream os = new FileOutputStream(loadProvince);
        ObjectMapper om = new ObjectMapper();
        OutputStream os1 = new FileOutputStream(loadPlayer);
        OutputStream os2 = new FileOutputStream(loadFaction);

        JsonGenerator g = om.getFactory().createGenerator(os);
        JsonGenerator g1 = om.getFactory().createGenerator(os1);
        JsonGenerator g2 = om.getFactory().createGenerator(os2);


        for (Player player:players) {

            om.writeValue(g1,player);

            
        }
        for (String f: factionList.keySet()) {
            for (Province p: factionList.get(f)) {
                saveProvince(p, om,g);
            }
        }

        for (String f: factions.keySet()) {
            saveFaction(factions.get(f),om,g2);
        }
      
    }
    public void loadGame() throws IOException {

        loadFaction();

        loadPlayer();
       
        // loadProvince();
        }

    /**
     * Will load the saved players
     */

    public void loadFaction() throws IOException {

        FileReader fis = new FileReader(loadFaction);
        JsonFactory jf = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper();
        Iterator<Faction> value = mapper.readValues( jf.createParser(fis), Faction.class);

        while(value.hasNext()) {
            Faction f = value.next();
            factions.put(f.getName(),f);
            factionList.put(f.getName(),(ArrayList)f.getProvinces());
            f.setDatabase(this);
            System.out.println(((ArrayList<Province>)f.getProvinces()).get(0).getUnits());
            ArrayList<Province> p= (ArrayList<Province>)f.getProvinces();
            for (int i = 0;i < p.size();i++) {
                List<Unit> u = p.get(i).getUnits();
                List<Unit> u1 = p.get(i).getUnitsTraining();
                provinceTraining.put(p.get(i).getName(), u1);
                provinceUnit.put(p.get(i).getName(), u);

                p.get(i);

            }
            // for (Province p: f.getProvinces()) {
            //     List<Unit> u = p.getUnits();
            //     List<Unit> u1 = p.getUnitsTraining();
            //     provinceTraining.put(p.getName(), u1);
            //     provinceUnit.put(p.getName(), u);
            //     p.setDatabase(this);


            //     provinceList.put(p.getName(), f);
            //     factionList.get(f.getName()).add(p);
            // }

        }

    }
    

    public void loadPlayer() throws IOException {
        FileReader fis = new FileReader(loadPlayer);
        JsonFactory jf = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper();
        Iterator<Player> value = mapper.readValues( jf.createParser(fis), Player.class);

        while(value.hasNext()) {

            Player p = value.next();
            p.setDatabase(this);
            addPlayer(p);

        }
    }
    public void loadProvince() {
        for (Faction f: factions.values()) {
            for (Province p: f.getProvinces()) {
                List<Unit> u = p.getUnits();
                List<Unit> u1 = p.getUnitsTraining();
                provinceTraining.put(p.getName(), u1);
                provinceUnit.put(p.getName(), u);
                p.setDatabase(this);


                provinceList.put(p.getName(), f);
                factionList.get(f.getName()).add(p);
            }
        }

    }

    // public void loadProvince() throws IOException {

    //     FileReader fis = new FileReader(loadProvince);
    //     JsonFactory jf = new JsonFactory();
    //     ObjectMapper mapper = new ObjectMapper();
    //     mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
    //     Iterator<Province> value = mapper.readValues( jf.createParser(fis), Province.class);

    //     while(value.hasNext()) {
    //         Province p = value.next();
    //         List<Unit> u = p.getUnits();
    //         provinceUnit.put(p.getName(), u);
    //         // System.out.println("heyyyyy"+p.getFaction());

    //         factionList.get(p.getFaction()).add(p);
    //         provinceTraining.put(p.getName(), p.getUnitsTraining());
    //         // p.setDatabase(this);
    //         p.setDatabase(this);
    //         if (!p.getUnits().isEmpty()) System.out.println(p.getUnits().get(0).getCost());
    //     }


    // }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    


   
    public void saveFaction(Faction f,ObjectMapper om, JsonGenerator g ) throws IOException{
        om.writeValue(g,f);
    }
    public void saveProvince( Province p, ObjectMapper om, JsonGenerator g) throws IOException {

        om.writeValue(g,p);


    } 

   
   
  

   
    private boolean belongs(Map<String,Faction> li,String faction, String province) {
        Faction f = li.get(province);
        return f.getName().equals(faction);
    }


    /**
     * This function will keep track of the training time of different troops and add them to the units when training is over
     */
    public void update(Unit u, String p) {
        provinceTraining.get(p).remove(u);

    }

    public void endTurn() {
    
        for(String p: provinceList.keySet()) {
            Province province = new Province(p,this);
            province.newTurn();
        }
    }

    public Boolean addUnit(String unit, String faction, String province) throws IOException {
        Province p = new Province(province, this);
        return factions.get(faction).trainUnit(p, unit);
    }
    
}








