package compare.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RunCompare {

    public static void run() {
        try {

            String fileURI1 = "src/main/resources/values/arquivo1.txt";
            String fileURI2 = "src/main/resources/values/arquivo2.txt";

            JsonArray array = compareFiles(fileURI1,fileURI2);
            for (JsonElement item : array) {
                JsonObject object = item.getAsJsonObject();
                System.out.println(object);
            }
            
            System.out.println();
            System.out.println("=================");
            System.out.println();

            JsonArray array2 = compareNewFiles(fileURI1,fileURI2);
            for (JsonElement item : array2) {
                JsonObject object = item.getAsJsonObject();
                System.out.println(object);
            }
            

        } catch (IOException e) {
            System.out.println("Error ao ler o arquivo!");
            e.printStackTrace();
        }
    }

    private static JsonArray compareFiles(String fileURI1, String fileURI2) throws IOException {
        String fileString1 = Files.readString(Paths.get(fileURI1));
        String fileString2 = Files.readString(Paths.get(fileURI2));

        List<String> listFileString1 = List.of(fileString1.split("\\R"));
        List<String> listFileString2 = List.of(fileString2.split("\\R"));

        JsonArray array = new JsonArray();
        for (int i = 0; i < listFileString1.size(); i++) {
            JsonObject object = new JsonObject();
            if (i < listFileString2.size()) {
                String line_old = listFileString1.get(i);
                String line_new = listFileString2.get(i);

                if (line_old.equals(line_new)) {
                    object.addProperty("change", "none");
                    object.addProperty("line", (i + 1));
                    object.addProperty("value-old", line_old);
                    object.addProperty("value-new", line_new);
                } else {
                    if (line_new.isEmpty()) {
                        object.addProperty("change", "delete");
                        object.addProperty("line", (i + 1));
                        object.addProperty("value-old", line_old);
                        object.addProperty("value-new", line_new);
                    } else {
                        object.addProperty("change", "change");
                        object.addProperty("line", (i + 1));
                        object.addProperty("value-old", line_old);
                        object.addProperty("value-new", line_new);
                    }
                }
            } else {
                object.addProperty("change", "delete");
                object.addProperty("line", (i + 1));
                object.addProperty("value-old", listFileString1.get(i));
                object.addProperty("value-new", "");
            }
            array.add(object);
        }
        return array;
    }

    private static JsonArray compareNewFiles(String fileURI1, String fileURI2) throws IOException {
        String fileString1 = Files.readString(Paths.get(fileURI1));
        String fileString2 = Files.readString(Paths.get(fileURI2));

        List<String> listFileString1 = List.of(fileString1.split("\\R"));
        List<String> listFileString2 = List.of(fileString2.split("\\R"));
        JsonArray array = new JsonArray();
        if (listFileString2.size() >= listFileString1.size()) {
            for (int i = listFileString1.size(); i < listFileString2.size(); i++) {
                JsonObject object = new JsonObject();
                object.addProperty("line", (i + 1));
                object.addProperty("value-new", listFileString2.get(i));
                array.add(object);
            }
        }
        return array;
    }
}
