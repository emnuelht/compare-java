package compare.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RunCompare {

    public static void run() {
        try {
            String arquivo1 = Files.readString(Paths.get("src/main/resources/values/arquivo1.txt"));
            String arquivo2 = Files.readString(Paths.get("src/main/resources/values/arquivo2.txt"));

            List<String> listAntigo = List.of(arquivo1.split("\\R"));
            List<String> listNovo = List.of(arquivo2.split("\\R"));

            for (int i = 0; i < listAntigo.size(); i++) {
                if (i < listNovo.size()) {
                    String lineAntigo = listAntigo.get(i);
                    String lineNovo = listNovo.get(i);

                    if (lineAntigo.equals(lineNovo)) {
                        System.out.println("As linhas sao iguais");
                    } else {
                        if (lineNovo.isEmpty()) {
                            System.out.println("Linha apagada");
                        } else {
                            System.out.println("As informacoes sao diferentes");
                            System.out.println(lineAntigo);
                            System.out.println(lineNovo);
                        }
                    }
                } else {
                    System.out.println("linha apagada");
                    System.out.println(listAntigo.get(i));
                }

                System.out.println();
            }

            System.out.println();
            System.out.println("=================");
            System.out.println();

            for (int i = 0; i < listAntigo.size(); i++) {
                System.out.println("line: " + listNovo.get(i));
                // if (i < listAntigo.size()) {
                //     String lineNovo = listNovo.get(i);
                //     String lineAntigo= listAntigo.get(i);
    
                //     if (lineNovo.equals(lineAntigo)) {
                //         System.out.println("As linhas sao iguais");
                //     } else {
                //         System.out.println("As informacoes sao diferentes");
                //         System.out.println(lineNovo);
                //         System.out.println(lineAntigo);
                //     }
                // } else {
                //     System.out.println("tem linha nova");
                //     System.out.println(listNovo.get(i));
                // }

                System.out.println();
            }

        } catch (IOException e) {
            System.out.println("Error ao ler o arquivo!");
            e.printStackTrace();
        }
    }
}
