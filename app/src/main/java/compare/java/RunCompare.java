package compare.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RunCompare {

    public static void run(String fileURI1, String fileURI2) {
        try {
            compareJson(fileURI1,fileURI2);
        } catch (IOException e) {
            System.out.println("Error ao ler o arquivo!");
            e.printStackTrace();
        }
    }

    public static void compareJson(String fileURI1, String fileURI2) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node1 = mapper.readTree(new File(fileURI1));
        JsonNode node2 = mapper.readTree(new File(fileURI2));

        List<String> listOld = walk(node1, "");
        List<String> listNew = walk(node2, "");

        int length = Math.max(listNew.size(), listOld.size());

        Set<String> codTablesNew = new HashSet<>();
        Set<String> codColumnsNew = new HashSet<>();
        Set<String> nameTablesNew = new HashSet<>();
        Set<String> nameColumnsNew = new HashSet<>();

        for (String str : listNew) {
            String[] cods = getCod(str, true);
            String[] names = getCod(str, false);
            codTablesNew.add(cods[0]);
            codColumnsNew.add(cods[1]);
            nameTablesNew.add(names[0]);
            nameColumnsNew.add(names[1]);
        }

        for (int i = 0; i < length; i++) {
            String codTable = getCod(listOld.get(i),true)[0];
            String codColumn = getCod(listOld.get(i),true)[1];
            String nameTable = getCod(listOld.get(i),false)[0];
            String nameColumn = getCod(listOld.get(i),false)[1];

            String oldValue = listOld.get(i);
            String newValue = listNew.get(i);

            if (!codTablesNew.contains(codTable)) {
                System.out.println("Table com o id: " + codTable + " foi deletada");
            } else if (!codColumnsNew.contains(codColumn)) {
                System.out.println("Pai: " + codTable + ", column com o id: " + codColumn + " foi deletada");
            } else if (!nameTablesNew.contains(nameTable) && codTablesNew.contains(codTable)) {
                System.out.println("Table: " + nameTable + " foi modificada");
            } else if (!nameColumnsNew.contains(nameColumn) && codColumnsNew.contains(codColumn)) {
                System.out.println("Pai: " + codTable + ", column: " + nameColumn + " foi modificada para ");
            } else if (!oldValue.equals(newValue)) {
                System.out.println("mudança aqui mane " + oldValue + " para " + newValue);
            }
            break;
        }
    }

    private static String[] getCod(String string, boolean cod) {
        String[] array = new String[3];
        array[0] = "";array[1] = "";array[2] = "";
        if (!string.isEmpty()) {
            String[] strings = string.split("\\.");
            String table = strings[0].length()>0?strings[0]:null;
            String column = strings[1].length()>0?strings[1]:null;
            String option = strings[2].length()>0?strings[2]:null;

            int indexTable_ = table.indexOf("_");
            String codTable = table.substring(indexTable_ + 2);

            int indexColumn_ = column.indexOf("_");
            String codColumn = column.substring(indexColumn_ + 2);
            
            array[0] = cod ? codTable : table;
            array[1] = cod ? codColumn : column;
            array[2] = option;
            return array;
        } else {
            return array;
        }
    }

    private static List<String> walk(JsonNode node, String path) {
        List<String> paths = new ArrayList<>();

        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String currentPath = path.isEmpty() ? entry.getKey() : path + "." + entry.getKey();
                paths.addAll(walk(entry.getValue(), currentPath));
            }
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                String currentPath = path + "[" + i + "]";
                paths.addAll(walk(node.get(i), currentPath));
            }
        } else {
            paths.add(path + ":" + node.toString());
        }
        return paths;
    }


    private static String clearString(String s) {
        if (s.contains(":") && s.contains("{")) {
            return s.replaceAll(" ", "").replace("{", "").replace("}", "").replace(":", "").replaceAll("\"", "");
        } else {
            return s.replaceAll(" ", "").replace("{", "").replace("}", "").replaceAll("\"", "").replace(",", "");
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
            String line_old = i < listFileString1.size() ? listFileString1.get(i) : "";
            String line_new = i < listFileString2.size() ? listFileString2.get(i) : "";

            if (!line_old.equals(line_new)) {
                JsonObject object = new JsonObject();
                object.addProperty("line", i + 1);
                object.addProperty("value-old", line_old);
                object.addProperty("value-new", line_new);

                if (line_old.isEmpty()) {
                    object.addProperty("change", "Add");
                } else if (line_new.isEmpty()) {
                    object.addProperty("change", "Delete");
                } else {
                    object.addProperty("change", "Change");
                }
                array.add(object);
            }
        }
        return array;
    }
}
