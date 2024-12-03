import java.util.*;

import static java.lang.Math.ceil;

public class FF{
    List<Process> processes = new ArrayList<>();
    int maxArrivalTime;
    int maxBurstTime;
    double V1;
    double V2;
    Map<String, List<int[]>> ganttChart = new HashMap<>();
    List<String> executionOrder = new ArrayList<>();
    Map<String, List<Integer>> processQuantumHistory = new HashMap<>();  //each time quantum is updated
    List<Integer> waitingTimes = new ArrayList<>();
    List<Integer> turnaroundTimes = new ArrayList<>();

    int currentTime = 0;
    int completedProcesses = 0;

    FF(List<Process> processes){
        this.processes = processes;
        this.maxArrivalTime = processes.stream().mapToInt(p -> p.arrivalTime).max().orElse(0);
        this.maxBurstTime = processes.stream().mapToInt(p -> p.burstTime).max().orElse(0);
        V1 = maxArrivalTime / 10.0;
        V2 = maxBurstTime / 10.0;
        calcFF();
    }
    void updateQuantum(Process p){
        int execTime = p.burstTime - p.remainingTime;
        if(p.quantum-execTime>0){
            p.quantum -= execTime;
        }
        else p.quantum += 2;
        processQuantumHistory.putIfAbsent(p.name, new ArrayList<>());
        processQuantumHistory.get(p.name).add(p.quantum);
    }
    void updateFF(Process p){
        p.FF = (int) ceil((double)(10-p.priority)+((double) p.arrivalTime / V1)+((double) p.remainingTime / V2));;
    }
    void print(){
        for(Process p : processes){
            System.out.println(p.name+ " FCAI Factor: " + p.FF);
        }
    }
    void calcFF(){
        for(Process p : processes){
            updateFF(p);
        }
    }
    void sort(){
        processes.sort(Comparator.comparingInt((Process p) -> p.FF).reversed()
                   .thenComparingInt(p -> p.arrivalTime));
    }

    void execute() {
        while (completedProcesses < processes.size()) {
            List<Process> readyQueue = new ArrayList<>();
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingTime > 0) {
                    readyQueue.add(p);
                }
            }

            for (Process p : readyQueue) {
                updateFF(p);
            }

            readyQueue.sort(Comparator.comparingInt((Process p) -> p.FF).reversed()
                    .thenComparingInt(p -> p.arrivalTime));

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process currentProcess = readyQueue.get(0);
            int timeSlice = (int) ceil(0.4 * currentProcess.quantum);
            int executionTime = Math.min(timeSlice, currentProcess.remainingTime);
            currentTime += executionTime;
            currentProcess.remainingTime -= executionTime;

            int currentFF = currentProcess.FF;

            // Add the process's execution period with FF to the Gantt chart
            ganttChart.putIfAbsent(currentProcess.name, new ArrayList<>());
            ganttChart.get(currentProcess.name).add(new int[]{currentTime - executionTime, currentTime, currentFF});

            // Add the process to the execution order
            executionOrder.add(currentProcess.name);

            if (currentProcess.remainingTime == 0) {
                completedProcesses++;
                int waitingTime = currentTime - currentProcess.arrivalTime - currentProcess.burstTime;
                int turnaroundTime = currentTime - currentProcess.arrivalTime;
                waitingTimes.add(waitingTime);
                turnaroundTimes.add(turnaroundTime);
            } else {
                updateQuantum(currentProcess);
            }
        }
    }


    void printResults() {
        System.out.println("Execution Order: " + executionOrder);
        System.out.println("Waiting Times: " + waitingTimes);
        System.out.println("Turnaround Times: " + turnaroundTimes);
        // Collect all intervals with process names and FF value
        List<Map.Entry<String, Object>> allIntervals = new ArrayList<>();

        for (Map.Entry<String, List<int[]>> entry : ganttChart.entrySet()) {
            String processName = entry.getKey();
            for (int[] period : entry.getValue()) {
                allIntervals.add(new AbstractMap.SimpleEntry<>(processName, period));
            }
        }

        // Sort intervals by the start time (period[0])
        allIntervals.sort(Comparator.comparingInt(entry -> ((int[]) entry.getValue())[0])); // Sort by start time

        // Print the sorted Gantt chart with FF value
        System.out.println("Sorted Gantt Chart:");
        for (Map.Entry<String, Object> entry : allIntervals) {
            String processName = entry.getKey();
            int[] period = (int[]) entry.getValue();
            System.out.println(processName + ": " + period[0] + " to " + period[1] + ", FF before preemption: " + period[2]);
        }


        // Print history of updated quantum values
        System.out.println("History of Quantum Updates:");
        for (Map.Entry<String, List<Integer>> entry : processQuantumHistory.entrySet()) {
            String processName = entry.getKey();
            List<Integer> quantumHistory = entry.getValue();
            System.out.print(processName + ": ");
            for (int quantum : quantumHistory) {
                System.out.print(quantum + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        List<Process> p = Parser.parseInput("/Users/farah/IdeaProjects/Scheduler/src/input.txt");
        FF ff = new FF(p);
        ff.execute();
        ff.printResults();
    }
}
