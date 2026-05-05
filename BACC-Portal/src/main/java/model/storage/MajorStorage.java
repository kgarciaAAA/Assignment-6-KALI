package storage;

import academics.MajorCatalog;
import java.util.HashMap;
import java.util.Map;

public class MajorStorage {
    private final Map<String, MajorCatalog> catalogByMajor;

    public MajorStorage() {
        this.catalogByMajor = new HashMap<>();
    }

    public Map<String, MajorCatalog> getAllMajorCatalogs() {
        return Map.copyOf(catalogByMajor);
    }

    public void addMajorCatalog(MajorCatalog majorCatalog) {
        catalogByMajor.put(majorCatalog.getMajorName(), majorCatalog);
    }

    public MajorCatalog getMajorCatalog(String majorName) {
        return catalogByMajor.get(majorName);
    }
}
