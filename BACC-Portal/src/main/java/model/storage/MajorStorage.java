package storage;

import java.util.HashMap;
import java.util.Map;

import academics.MajorCatalog;

public class MajorStorage {
    private final Map<String, MajorCatalog> catalogByMajor;

    //constructor
    public MajorStorage() {
        this.catalogByMajor = new HashMap<>();
    }

    //getters
    public Map<String, MajorCatalog> getAllMajorCatalogs() {
        return Map.copyOf(catalogByMajor);
    }

    public MajorCatalog getMajorCatalog(String majorName) {
        return catalogByMajor.get(majorName);
    }    

    //controlled updates
    public void addMajorCatalog(MajorCatalog majorCatalog) {
        catalogByMajor.put(majorCatalog.getMajor().getMajorName(), majorCatalog);
    }
}
