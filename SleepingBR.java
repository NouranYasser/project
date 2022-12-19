package sleepingbr;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SleepingBR {
    
    private int noOfCutomers;
    private int noOfChairs;
    private int noOfBR;

    public SleepingBR(int noOfCutomers, int noOfChairs, int noOfBR) {
        this.noOfCutomers = noOfCutomers;
        this.noOfChairs = noOfChairs;
        this.noOfBR = noOfBR;
    }
    
    
    
    public void Start(Session form) throws InterruptedException{
        ExecutorService exec = Executors.newFixedThreadPool(12);
        Hall hall = new Hall(noOfChairs, noOfBR, noOfCutomers, form);
        Random r = new Random();
        
        System.out.println("Hall is opened with "+noOfBR+" Barbers");
        
        long startTime  = System.currentTimeMillis();
        
        for (int i = 1; i <= noOfBR; i++) {
            Barbers BR = new Barbers(hall, i);
            Thread thBR = new Thread(BR);
            exec.execute(thBR);
        }
        
        for (int i = 1; i <= noOfCutomers; i++) {
            try {
                Customer customer = new Customer(hall);
                customer.setInTime(new Date());
                customer.setCustomerId(i);
                Thread thBR = new Thread(customer);
                exec.execute(thBR);
                
                double val = r.nextGaussian() * 2000 + 2000;			
                int Delay = Math.abs((int) Math.round(val));		
                Thread.sleep(Delay);
                
                
            } catch (InterruptedException ex) {
                Logger.getLogger(SleepingBR.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        List<Customer> backLater = hall.Backlater();
        if (backLater.size() > 0 ) {
            for (int i = 0; i < backLater.size(); i++) {
            try {
                Customer customer = backLater.get(i);
                customer.setInTime(new Date());
                Thread thCustomer = new Thread(customer);
                exec.execute(thCustomer);
                
                double val = r.nextGaussian() * 2000 + 2000;			
                int Delay = Math.abs((int) Math.round(val));		
                Thread.sleep(Delay);
                
                
            } catch (InterruptedException ex) {
                Logger.getLogger(SleepingBR.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        }
        
        exec.awaitTermination(12, SECONDS);
        exec.shutdown();
        
        long elapsedTime = (System.currentTimeMillis() - startTime)/1000;
        
        System.out.println("Hall is closed");
        System.out.println("\nTotal time elapsed in seconds"
        		+ " for Answering "+noOfCutomers+" customers' Requests "
        		+noOfBR+" Barbers with "+noOfChairs+
        		" chairs in the waiting room is: "
        		+elapsedTime);
        System.out.println("\nTotal Customers: "+noOfCutomers+
        		"\nTotal Customers served: "+hall.getTotalAcceptedRequests()
        		+"\nTotal Customers returned: "+hall.getBackLaterCounter());
    }
    
}