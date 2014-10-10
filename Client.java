import java.util.Random;
public class Client extends Thread {
	
	private int nUnits;
	private int nRequests;
	private long minSleepMillis;
	private long maxSleepMillis;
	private Banker banker;

	public Client(String name, Banker banker, int nUnits, int nRequests, long minSleepMillis, long maxSleepMillis) {
		super(name);
		this.banker = banker;
		this.nUnits = nUnits;
		this.nRequests = nRequests;
		this.minSleepMillis = minSleepMillis;
		this.maxSleepMillis = maxSleepMillis;
	}

	public void run() {
	    Random rand = new Random();
		
		// set a claim with the banker for nUnits
		this.banker.setClaim(this.nUnits);

		for(int i = 0; i < this.nRequests; i++) {
			if (this.banker.remaining() == 0) {
				// release all resources
				if(this.banker.allocated() > 0)
					this.banker.release(this.banker.allocated());
			} else {
				// request some number of resources
				int tmpReqUnits = rand.nextInt(this.nUnits - this.banker.allocated()) + 1;
				this.banker.request(tmpReqUnits);
			}
			
			// wait some time
			long millis = (Math.abs(rand.nextLong()) % (this.maxSleepMillis - this.minSleepMillis)) + this.minSleepMillis;
			try {
				Thread.sleep(millis); 
			} catch(InterruptedException e){}
		}
		
		// release any remaining resources
		if(this.banker.allocated() > 0)
			this.banker.release(this.banker.allocated());
	}


}
