package sleepingbr;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Hall {
    private final ReentrantLock mutex = new ReentrantLock();
    private int waitingChairs, noOfBR, availableBR;
    private int TotalAcceptedRequests, BackLaterCounter;
    private List<Customer> CustomerList;
    private List<Customer> CustomerBackLater;
    private Semaphore Availabe;
    private Random r = new Random();
    private Session form;

    public Hall(int nChairs, int nBR, int nCustomer, Session form) {
        this.waitingChairs = nChairs;
        this.noOfBR = nBR;
        this.availableBR = nBR;
        this.form = form;
        Availabe = new Semaphore(availableBR);
        this.CustomerList = new LinkedList<Customer>();
        this.CustomerBackLater = new ArrayList<Customer>(nCustomer);
    }

    

    public int getTotalAcceptedRequests() {
        return TotalAcceptedRequests;
    }

    public int getBackLaterCounter() {
        return BackLaterCounter;
    }
    
    public void ApproveRequest(int BR_ID){
        Customer customer;
        
        
        synchronized(CustomerList){
            while (CustomerList.size() == 0) {
                form.SleepBR(BR_ID);
                System.out.println("\nBarber"+BR_ID+" is waiting "
                		+ "for the customers and sleeps in his Chair");
                try {
                    CustomerList.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Hall.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        
            customer = (Customer)((LinkedList<?>)CustomerList).poll();
            System.out.println("customer "+customer.getCustomerId()+
            		" finds Barber available "
            		+ "the Barber "+BR_ID);
        }
            int Delay;
            try {
                if (Availabe.tryAcquire() && CustomerList.size() == waitingChairs){
                Availabe.acquire();
                }
                form.BusyBR(BR_ID);
                System.out.println("Barber "+BR_ID+" Accept Customer's Request"+
            		customer.getCustomerId());
                
                double val = r.nextGaussian() * 2000 + 4000;				
        	Delay = Math.abs((int) Math.round(val));				
        	Thread.sleep(Delay);
                
                System.out.println("\nCompleted Acceptance Requests "+
        			customer.getCustomerId()+" by Barber " + 
        			BR_ID +" in "+(int)(Delay/1000)+ " seconds.");
                mutex.lock();
                try {
                    TotalAcceptedRequests++;
                } finally {
                    mutex.unlock();
                }
                
                if (CustomerList.size() > 0) {
                    System.out.println("Barber "+BR_ID+					
            			" Calls a Customer to enter Hall ");
                    form.ReturnChair(BR_ID);
                }
                Availabe.release();
                
            } catch (InterruptedException e) {
            }
            
            
            
        }   
    
    
    public void EnterHall(Customer customer){
        System.out.println("\nCustomers "+customer.getCustomerId()+
        		" tries to enter hall to shave  "
        		+customer.getInTime());
        
        synchronized(CustomerList){
            if (CustomerList.size() == waitingChairs) {
                
                System.out.println("\nNo chair available "
                		+ "for Customer "+customer.getCustomerId()+
                		" so Customer leaves and will come back later");
                
                CustomerBackLater.add(customer);
                mutex.lock();
                try {
                    BackLaterCounter++;
                } finally {
                    mutex.unlock();
                }
                return;
            }
            else if (Availabe.availablePermits() > 0 ) {
                ((LinkedList<Customer>)CustomerList).offer(customer);
                CustomerList.notify();
            }
            else{
                try {
                    ((LinkedList<Customer>)CustomerList).offer(customer);
                    form.TakeChair();
                    System.out.println("All Barbers are busy so Customers "+
                            customer.getCustomerId()+
                            " takes a chair in the waiting room");
                    
                    if (CustomerList.size() == 1) {
                        CustomerList.notify();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Hall.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
        }
    }
    
    public List<Customer> Backlater(){
        return CustomerBackLater;
    }
    
    
    
}
