public class NormalJob {
    int jobID;
    int jobCore;
    int jobMemory;
    int jobDisk;

    public NormalJob(String [] jobInfoArray){   
        this.jobID = Integer.parseInt(jobInfoArray[2]);
        this.jobCore = Integer.parseInt(jobInfoArray[4]);
        this.jobMemory = Integer.parseInt(jobInfoArray[5]);
        this.jobDisk = Integer.parseInt(jobInfoArray[6]);
    }
}

