class Process implements Comparable{
    String name;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int priority;
    int waitingTime;
    int turnaroundTime;
    int quantum;
    boolean completed;
    int FF;

    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;
        this.completed = false;
    }
    @Override
    public int compareTo(Object o) {
        Process p = (Process) o;
        return Integer.compare(this.FF, p.FF);
    }
    void setFF(int ff) {
        this.FF = ff;
    }
}