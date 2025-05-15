package compare.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
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
        Set<String> optionValueNew = new HashSet<>();

        for (String str : listNew) {
            String[] cods = getCod(str, true);
            String[] names = getCod(str, false);
            codTablesNew.add(cods[0]);
            codColumnsNew.add(cods[1]);
            nameTablesNew.add(names[0]);
            nameColumnsNew.add(names[1]);
            optionValueNew.add(cods[1]+"."+names[2]);
        }

        Map<String, String> changeMap = new HashMap<>();
        Set<String> deleted = new HashSet<>();
        for (int i = 0; i < listOld.size(); i++) {
            String codTable = getCod(listOld.get(i),true)[0];
            String codColumn = getCod(listOld.get(i),true)[1];
            String nameTable = getCod(listOld.get(i),false)[0];
            String nameColumn = getCod(listOld.get(i),false)[1];
            String nameOption = codColumn+"."+getCod(listOld.get(i),false)[2];

            String oldValue = listOld.get(i);
            String newValue = i < listNew.size() ? listNew.get(i) : "";

            // Diferença
            if (!oldValue.equals(newValue)) {
                // System.out.println("=> " + oldValue + " - " + newValue);
                // Delete
                if (!codTablesNew.contains(codTable) && !deleted.contains(codTable)) {
                    deleted.add(codTable);
                    // changeMap.put("deleted.table."+codTable, codTable);
                    continue; // pula o restante, pois a tabela foi deletada
                }

                if (!codColumnsNew.contains(codColumn) && !deleted.contains(codColumn)) {
                    deleted.add(codColumn);
                    // changeMap.put("deleted.column."+codTable+"."+codColumn, codColumn);
                    continue; // pula o restante, pois a coluna foi deletada
                }

                if (deleted.contains(codTable) || deleted.contains(codColumn)) {
                    continue;
                }

                if (!nameTablesNew.contains(nameTable) && codTablesNew.contains(codTable) && !deleted.contains(codTable)) {
                    // changeMap.put("change.table."+codTable, getCod(newValue,false)[0]);
                }

                if (!nameColumnsNew.contains(nameColumn) && codColumnsNew.contains(codColumn) && !deleted.contains(codColumn)) {
                    // changeMap.put("change.column."+codTable+"."+codColumn, getCod(newValue,false)[1]);
                }

                // System.out.println(nameOption + " - " + optionValueNew);
                // System.out.println();

                if (!optionValueNew.contains(nameOption)) {
                    // changeMap.put("change.value."+codTable+"."+codColumn, getCod(newValue,false)[2]);

                    System.out.println(optionValueNew);
                    // for (String item : optionValueNew) {
                    //     String string = nameOption.split("\\.")[0];
                    //     if (nameOption.contains(string) && !nameOption.contains(item)) {
                    //         System.out.println(nameOption + " - " + item);
                    //         // System.out.println("=========================================> " + item);
                    //     }
                    // }
                }

                // String[] oldParts = getCod(oldValue, true)[2].split(":", 2);
                // String[] newParts = getCod(newValue, true)[2].split(":", 2);

                // if (oldParts.length == 2 && newParts.length == 2) {
                //     String oldKey = oldParts[0];
                //     String oldVal = oldParts[1];
                //     String newKey = newParts[0];
                //     String newVal = newParts[1];

                //     // System.out.println(oldKey + ":" + oldVal + " <-> " + newKey + ":" + newVal);

                //     if (oldKey.equals(newKey) && !oldVal.equals(newVal)) {
                //         changeMap.put("change.value." + codTable + "." + codColumn + "." + oldKey, newVal);
                //     }
                // }

            }

            // if (!codTablesNew.contains(codTable) && !deleted.contains(codTable)) {
            //     System.out.println("Table com o id: " + codTable + " foi deletada");
            //     deleted.add(codTable);
            // }
            // if (!codColumnsNew.contains(codColumn) && (!deleted.contains(codTable) || !deleted.contains(codColumn))) {
            //     System.out.println("Pai: " + codTable + ", column com o id: " + codColumn + " foi deletada");
            //     deleted.add(codColumn);
            // }
            // if (!nameTablesNew.contains(nameTable) && codTablesNew.contains(codTable) && (!deleted.contains(codTable) || !deleted.contains(codColumn))) {
            //     System.out.println("Table: " + nameTable + " foi modificada");
            // }
            // if (!nameColumnsNew.contains(nameColumn) && codColumnsNew.contains(codColumn) && (!deleted.contains(codTable) || !deleted.contains(codColumn))) {
            //     System.out.println("Pai: " + codTable + ", column: " + nameColumn + " foi modificada para ");
            // }
            // if (!oldValue.equals(newValue) && (!deleted.contains(codTable) || !deleted.contains(codColumn))) {
            //     System.out.println(nameTable + " - " + nameColumn + " mudanca aqui mane " + getCod(oldValue,true)[2] + " para " + getCod(newValue,true)[2]);
            // }
            // if (!oldValue.isEmpty() && newValue.isEmpty() && !oldValue.equals(newValue) && (!deleted.contains(codTable) || !deleted.contains(codColumn))){
            //     System.out.println("item deletado");
            // }
            // break;
        }
        for (Map.Entry<String, String> entry : changeMap.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
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
