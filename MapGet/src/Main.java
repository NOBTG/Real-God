import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            List<String> lines1 = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\NOBTG\\Documents\\GitHub\\Real-God\\build\\createSrgToMcp\\output.srg"));
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("CL")) {
                    String[] a = line.split(" ");
                    if (line.startsWith("FD")) {
                        String a1 = a[1];
                        String a2 = a[2];

                        String[] b = a1.split("/");
                        String[] c = a2.split("/");

                        String tmpValue0 = b[b.length - 1];
                        String key = a1.replace("/" + tmpValue0, "").replace("/", ".") + ";" + tmpValue0;

                        String value = c[c.length - 1];
                        if (!key.equals(value)) {
                            lines1.add(key + ":" + value + ",");
                        }
                    }
                }
            }

            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt"));

            int size = lines1.size();
            for (int i = 0; i < size; i++) {
                String target = lines1.get(i);
                if (i == size - 1) {
                    target = target.substring(0, target.length() - 1);
                }

                writer.write(target);
            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}