package compare.java;

import com.google.gson.JsonElement;

public class Main {
    public static void main(String[] args) {
        String fileURI1 = "src/main/resources/values/arquivo1.json";
        String fileURI2 = "src/main/resources/values/arquivo2.json";

        RunCompare.run(fileURI1,fileURI2);
        // for(JsonElement e : RunCompare.run(fileURI1,fileURI2)) {
        //     System.out.println(e);
        // }
    }
}
