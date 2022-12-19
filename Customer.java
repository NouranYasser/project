package sleepingbr;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Customer implements Runnable{
    
    private int CustomerId;
    private Hall hall;
    private Date inTime;

    public Customer(Hall hall) {
        this.hall = hall;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public void setCustomerId(int CustomerId) {
        this.CustomerId = CustomerId;
    }

    public Date getInTime() {
        return inTime;
    }

    public int getCustomerId() {
        return CustomerId;
    }
    
     
    @Override
    public void run(){
        try {
            GetRequest();
        } catch (InterruptedException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private synchronized void GetRequest() throws InterruptedException {							//customer is added to the list
       
        hall.EnterHall(this);
    }
    
}
