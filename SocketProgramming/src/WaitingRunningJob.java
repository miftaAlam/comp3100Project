public class WaitingRunningJob {    
    int jobID;
    int jobState;
    int jobCore;
    int jobMemory;
    int jobDisk;

    public WaitingRunningJob(String[] LSTJJobInfoArray){
        this.jobID = Integer.parseInt(LSTJJobInfoArray[0]);
        this.jobState = Integer.parseInt(LSTJJobInfoArray[1]);
        this.jobCore = Integer.parseInt(LSTJJobInfoArray[5]);
        this.jobMemory = Integer.parseInt(LSTJJobInfoArray[6]);
        this.jobDisk = Integer.parseInt(LSTJJobInfoArray[7]);

    }
}


