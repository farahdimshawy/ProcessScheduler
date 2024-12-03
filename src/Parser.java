import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static List<Process> parseInput(String filename) {
        List<Process> processes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");

                String pn = parts[0];
                int BT = Integer.parseInt(parts[1]);
                int arrivalTime = Integer.parseInt(parts[2]);
                int p = Integer.parseInt(parts[3]);
                int q = Integer.parseInt(parts[4]);

                Process process= new Process(pn, arrivalTime, BT, p, q);
                processes.add(process);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return processes;
    }
}
