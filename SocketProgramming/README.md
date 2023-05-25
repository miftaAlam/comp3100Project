Server being scheduled is not included in the Waiting Time
Waiting time is just, time between job submitted and job started, which includes the time taken by the server to boot etc
On our client side the scheduling stuff does not count, only when we learn
# Questions to ask:
1.	Why LSTJ all the booting (but not active) servers (that are capable), and not make use of any of them? Even though they have sufficient resources
2.	Eg: SENT JOBN 60 1 597 1 700 1400 //requires 1 core
3.	Medium 0 is currently booting, and is included in the GETS Capable 
4. LSTJ Medium 0, has 1 core left
5. Are we not choosing any server that has a waiting OR running job? - or are we ignoring the ones that are active (running a job) but accepting the ones that have a waiting job as a possible candidate

EJWT


Job created/submission
The last two fields (#wJobs and #rJobs) are the number of waiting jobs and the number of running
jobs. Their details, such as job ID, state, start time, estimated runtime and resource requirements can be found
by the LSTJ command. In the above example, although all servers are capable of running the job (job 3), juju
0, juju 1 and joon 0 are currently not immediately available since each of the first two has a waiting job and
joon 0 is running a job.

time isnt self increasing
end time is calculated by the server using the job attribute -- execution time for a job
Main thing is the waiting time --> booting time 
submitted is considered scheduled
diff between submitted and start time is server booting time
also, waiting for the local queue of server to finish
so try to put to servers with shortest queues (LSTJ)
incase all servers are busy -
PUTTING it in a list of booting servers
u can assign to server with booting, but NOT with running 

Ideas:
JCPL: 
  gets all 
 if u find a idle or inactive server, continue, if not go through all of them again and find the best fit job from the local queues to do migration to the freed server 


If there are some idle servers but they aren't capable, you have to make the job queue on some capable severe which is busy:

That's what it is doing, if there is no idle AND capable server found,

found is false, then u go through all the CAPABLE servers and assign the job to the one with the shortest queue/ least number of waiting jobs