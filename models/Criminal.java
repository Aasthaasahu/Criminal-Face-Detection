package models;
import java.util.HashMap;
import java.util.Map;

public class Criminal {
    private Map<Integer, String> fallback = new HashMap<>();

    public Criminal() {
        // Fallback data matching criminal_database.sql
        fallback.put(1, "Aastha Sahu");
    }

    public String getCriminalName(int id) {
        return fallback.getOrDefault(id, "Unknown");
    }
}
