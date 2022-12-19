package sleepingbr;

public class Barbers implements Runnable{
    
    private Hall hall;
    private int BR_ID;

    public Barbers(Hall hall, int BR_ID) {
        this.hall = hall;
        this.BR_ID = BR_ID;
    }
    
    @Override
    public void run(){
        while (true) {            
            hall.ApproveRequest(BR_ID);
        }
    }
    
}
