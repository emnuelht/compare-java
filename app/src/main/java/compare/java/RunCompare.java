package compare.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RunCompare {

    public static void run(String fileURI1, String fileURI2) {
        try {
            compareJson(fileURI1,fileURI2);
            // return array;
        } catch (IOException e) {
            System.out.println("Error ao ler o arquivo!");
            e.printStackTrace();
            // return new JsonArray();
        }
    }

    public static void compareJson(String fileURI1, String fileURI2) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> json_old = mapper.readValue(new File(fileURI1), Map.class);
        Map<String, Object> json_new = mapper.readValue(new File(fileURI2), Map.class);

        compareMaps("", json_old, json_new);
    }

    private static void compareMaps(String path, Map<String, Object> oldMap, Map<String, Object> newMap) {
        for (String key : newMap.keySet()) {
            String fullPath = path.isEmpty() ? key : path + "." + key;

            if (!oldMap.containsKey(key)) {
                System.out.println("ADDED: " + fullPath + " => " + newMap.get(key));
            } else {
                Object oldVal = oldMap.get(key);
                Object newVal = newMap.get(key);

                if (oldVal instanceof Map && newVal instanceof Map) {
                    // Recursive comparison
                    compareMaps(fullPath, (Map<String, Object>) oldVal, (Map<String, Object>) newVal);
                } else if (!Objects.equals(oldVal, newVal)) {
                    System.out.println("MODIFIED: " + fullPath + " | Old: " + oldVal + " | New: " + newVal);
                }
            }
        }

        for (String key : oldMap.keySet()) {
            if (!newMap.containsKey(key)) {
                String fullPath = path.isEmpty() ? key : path + "." + key;
                System.out.println("REMOVED: " + fullPath + " => " + oldMap.get(key));
            }
        }
    }

    private static JsonArray compareFiles(String fileURI1, String fileURI2) throws IOException {
        String fileString1 = Files.readString(Paths.get(fileURI1));
        String fileString2 = Files.readString(Paths.get(fileURI2));

        List<String> listFileString1 = List.of(fileString1.split("\\R"));
        List<String> listFileString2 = List.of(fileString2.split("\\R"));

        int length = Math.max(listFileString1.size(), listFileString2.size());

        JsonArray array = new JsonArray();

        for (int i = 0; i < length; i++) {
            String line_old = i < listFileString1.size() ? listFileString1.get(i) : ".";
            String line_new = i < listFileString2.size() ? listFileString2.get(i) : ".";

            if (!line_old.equals(line_new)) {
                JsonObject object = new JsonObject();
                object.addProperty("line", i + 1);
                object.addProperty("value-old", line_old);
                object.addProperty("value-new", line_new);

                if (line_old.isEmpty()) {
                    object.addProperty("change", "add");
                } else if (line_new.isEmpty()) {
                    object.addProperty("change", "delete-value");
                } else {
                    object.addProperty("change", "change");
                }

                array.add(object);
            }
        }

        return array;
    }
}
