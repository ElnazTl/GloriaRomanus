package unsw.gloriaromanus.Backend;

import org.json.*;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.StackWalker.Option;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;


import java.util.Map;
import java.util.HashMap;


public class Database {

    public Map<Province,Unit> provinceUnit;
    public Map<Province,Faction> provinceList;
    public String address;

    // assign default unit to each province 
    public Database() throws IOException {

        provinceUnit = new HashMap<Province,Unit>();
        provinceList = new HashMap<Province,Faction>();

        address = "src/unsw/gloriaromanus/initial_province_ownership.json";

        setProvinceToOwningFactionMap();

        for (Province provinceName : provinceList.keySet()) {
            Unit unit = new Unit();
            provinceUnit.put(provinceName, unit);
          }
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
     * Function initilises the map of province to faction 
     * @return map of province to faction 
     * @throws IOException
     */

    private Map<Province, Faction> setProvinceToOwningFactionMap() throws IOException {
        String content = Files.readString(Paths.get(address));
        JSONObject ownership = new JSONObject(content);
        Map<Province, Faction> m = new HashMap<Province, Faction>();
        for (String key : ownership.keySet()) {
          // key will be the faction name
          JSONArray ja = ownership.getJSONArray(key);
          // value is province name
          for (int i = 0; i < ja.length(); i++) {
            String value = ja.getString(i);
            Province p = new Province(value);
            Faction f = new Faction(key);
            m.put(p, f);
          }
        }
        return m;
      }

    
}
