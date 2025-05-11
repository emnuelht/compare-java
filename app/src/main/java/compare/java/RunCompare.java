package compare.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RunCompare {

    public static JsonArray run(String fileURI1, String fileURI2) {
        try {
            JsonArray array = compareFiles(fileURI1,fileURI2);
            return array;
        } catch (IOException e) {
            System.out.println("Error ao ler o arquivo!");
            e.printStackTrace();
            return new JsonArray();
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
