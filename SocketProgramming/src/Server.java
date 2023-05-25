public class Server {
    String serverType;
    int serverID;
    String serverState;
    int serverCore;
    int serverMemory;
    int serverDisk;
    int waitingJobs;
    int runningJobs;
    int totalJobs;

    public Server(String[] serverInfoArray){
        this.serverType = serverInfoArray[0];
        this.serverID = Integer.parseInt(serverInfoArray[1]);
        this.serverState = serverInfoArray[2];
        this.serverCore = Integer.parseInt(serverInfoArray[4]); //No of Cores is in the 4th index (5th position) in the whole message
        this.serverMemory = Integer.parseInt(serverInfoArray[5]);
        this.serverDisk = Integer.parseInt(serverInfoArray[6]);
        this.waitingJobs = Integer.parseInt(serverInfoArray[7]);
        this.runningJobs = Integer.parseInt(serverInfoArray[8]);
        this.totalJobs = 0;   
    }

    public Server(){
        
    }


    public boolean hasEnoughResources(NormalJob currJob){
        if(this.serverCore>= currJob.jobCore  && this.serverMemory >= currJob.jobMemory && this.serverDisk >= currJob.jobDisk){
            return true;
        }
        return false;
    }

    public boolean isInactiveOrIdle(){
        // as we want to prioritise scheduling to the servers with no local queues first
        // only if we cannot find a capable + inactive server, we start choosing a booting/running server with the shortest queue
        if(this.serverState.equals("inactive")|| this.serverState.equals("idle")){
            return true;
        }
        return false;
    }
}

