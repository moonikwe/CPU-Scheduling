import java.io.*;
import java.util.*;

class Job{
	int process;
	int arrival;
	int burst;
	int priority;
}
public class lab2{
	public static ArrayList <Job> jobs = new ArrayList <Job>();
    public static ArrayList <Job> original = new ArrayList <Job>();
	public static ArrayList <Job> newJobs = new ArrayList <Job>();
	public static ArrayList <Integer> waitingTime = new ArrayList <Integer>();
	public static ArrayList <Float> results = new ArrayList <Float>();
	public static int wait = 0;
	public static float totalWait = 0;
	public static float awt = 0;
    public static int totalBurst = 0;
	public static void main (String[] args) throws IOException{
		String[] arrayOfValues;
		
		int i = 0;
	      try{
	            FileInputStream fstream = new FileInputStream("process2.txt");
	            DataInputStream in = new DataInputStream(fstream);
	            BufferedReader br = new BufferedReader(new InputStreamReader(in));
	            String strLine = "";
	            while ((strLine = br.readLine()) != null) {
	            	Job job = new Job();
                    Job j = new Job();
		            if( i > 0){
		            	arrayOfValues = strLine.split("\\s+");

		            	//the next lines create the attributes of the new Job
		            	job.process = Integer.parseInt(arrayOfValues[0]);
		            	job.arrival = Integer.parseInt(arrayOfValues[1]);
		            	job.burst = Integer.parseInt(arrayOfValues[2]);
                        totalBurst+= job.burst;
		            	job.priority = Integer.parseInt(arrayOfValues[3]);
		            	jobs.add(job);

                        j.process = Integer.parseInt(arrayOfValues[0]);
                        j.arrival = Integer.parseInt(arrayOfValues[1]);
                        j.burst = Integer.parseInt(arrayOfValues[2]);
                        j.priority = Integer.parseInt(arrayOfValues[3]);
                        original.add(j);
		            }
		            i++;
	            }
	        in.close();
	        }catch (Exception e)	{
	                System.err.println("Error: " + e.getMessage());
	        }

	        fcfs();
	        sjf();
	        priority();
            srpt();
    	    roundRobin();
	        evaluate();
    }

    public static void fcfs(){
		waitingTime.add(wait);
		copyJobs();
    	//this line sorts the arraylist of jobs based on arrival time
    	Collections.sort(newJobs,(job1, job2) -> job1.arrival - job2.arrival);
    	
    	for(int i = 0; i < newJobs.size() -1; i++){
    		wait = wait + newJobs.get(i).burst;
    		totalWait = totalWait + wait;
    		waitingTime.add(wait);
    	}
    	printOutput("FCFS");	
    }

    public static void sjf(){
		copyJobs();

    	Collections.sort(newJobs,(job1, job2) -> job1.burst - job2.burst);

    	for(int i = 0; i < newJobs.size() -1; i++){
    		wait = wait + newJobs.get(i).burst;
    		totalWait = totalWait + wait;
    		waitingTime.add(wait);
    	}
		printOutput("SJF");
    
    }
    public static void priority(){
		copyJobs();
		
    	Collections.sort(newJobs,(job1, job2) -> job1.priority - job2.priority);
    	
    	for(int i = 0; i < newJobs.size() -1; i++){
    		wait = wait + newJobs.get(i).burst;
    		totalWait = totalWait + wait;
    		waitingTime.add(wait);
    	}
    	awt = totalWait / newJobs.size();
		printOutput("PRIORITY");

    }

    public static void srpt(){
        copyJobs();
        int[][] waitingData = new int[newJobs.size()][2];

        Job current = newJobs.get(0);
        int processTime = 0;
        for(int i = 0; i < totalBurst; i++){//i is the time
            for(int j = 0; j < newJobs.size() && newJobs.get(j).arrival <= i; j++){
                if((current.burst == 0 || newJobs.get(j).burst < current.burst) && newJobs.get(j).burst > 0){
                    if(current.burst > 0){
                        waitingData[current.process-1][1] += processTime;
                    }

                    waitingData[j][0] = i;
                    current = newJobs.get(j);
                    processTime = 0;
                }
            }
            processTime++;
            current.burst--;
        }
        
        for(int i = 0; i < newJobs.size(); i++){
            waitingTime.add(waitingData[i][0] - newJobs.get(i).arrival - waitingData[i][1]);
            totalWait += (waitingData[i][0] - newJobs.get(i).arrival - waitingData[i][1]);
        }

        printOutput("SRPT");
    }

    public static void roundRobin(){
  
    	ArrayList <Job> successJobs = new ArrayList <Job>();
    	ArrayList <Job> queueJobs = new ArrayList <Job>();
		int i = 0, count = 0, max = 0, fin = 0;
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    	System.out.println("ROUND ROBIN" + "\n");	
        System.out.println(original.size());
		while(original.size() > 0 ){
			if (i == original.size()){
				i = 0;
			}
			if (original.get(i).burst <= 0){
				successJobs.add(original.get(i));
				original.remove(i);
			}
			else if(original.get(i).burst > 0){
				queueJobs.add(original.get(i));
				if(original.get(i).burst >= 4){
					wait += 4;
				}
				else if(original.get(i).burst < 4 && original.get(i).burst > 0){
					wait += original.get(i).burst;
				}
				else if (original.get(i).burst < 0){
					wait += (original.get(i).burst + 4);
				}

				waitingTime.add(wait);
				original.get(i).burst -= 4;
				i++;
			}	
		}    	
		for(int m = 0; m <successJobs.size(); m++){
        	for(int j = 0; j < queueJobs.size(); j++){
        		if(successJobs.get(m).process == queueJobs.get(j).process){
        			count++;
        			max = waitingTime.get(j);
        		}
        	}
        	fin = (max - ((count-1) *4));
        	totalWait = totalWait + fin;
        	System.out.print(successJobs.get(m).process + "[" + fin+ "]" + "   " );
        	count = 0;
        	max = 0;
        }

        System.out.println("\n\nTotal Waiting Time: " +totalWait + " ms");
    	awt = totalWait / jobs.size();
    	System.out.println("Average Waiting Time: " + awt + " ms");
        awt = 0;
        totalWait = 0;
    }
    
    public static void copyJobs(){
    	for(int i = 0; i < jobs.size(); i++){
    		newJobs.add(jobs.get(i));
    	}
    }
    
    public static void printOutput(String s){
    	System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    	System.out.println(s + "\n");
		for(int i = 0; i < newJobs.size(); i++){
			System.out.print(newJobs.get(i).process + "[" + waitingTime.get(i) + "]" + "   " );
		}
    	awt = totalWait / newJobs.size();
    	results.add(awt);
    	System.out.println("\n\nTotal Waiting Time: " +totalWait + " ms");
    	System.out.println("Average Waiting Time: " + awt + " ms");
    	wait = 0;
    	totalWait = 0;
    	awt = 0;
    	newJobs.clear();
    	waitingTime.clear();
		waitingTime.add(wait);
    }
    public static void evaluate(){
    	float current = results.get(0);
    	int lowest = 0;
    	for(int i = 1; i < results.size(); i++){
    		if (results.get(i) < current){
    			lowest = i;
    		}
    	}
    	System.out.println("\n\nMost efficient CPU scheduling algorithm is: ");
    	switch(lowest){
    		case 0:{
    			System.out.print("FCFS");
    			break;
    		}
    		case 1:{
    			System.out.print("SJF");
    			break;
    		}
    		case 2:{
    			System.out.print("PRIORITY");
    			break;
    		}
            case 3:{
                System.out.print("SRPT");
                break;
            }
    		case 4:{
    			System.out.print("ROUND ROBIN");
    			break;
    		}
    	}
        System.out.println();
    }
}
