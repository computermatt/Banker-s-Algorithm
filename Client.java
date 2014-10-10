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
		int requestedUnits = 0;
		this.banker.setClaim(this.nUnits);

		for(int i = 0; i < this.nRequests; i++) {
			if (this.banker.remaining() == 0) {
				this.banker.release(this.banker.remaining());
			} else {
				int tmpReqUnits = rand.nextInt(this.nUnits - requestedUnits);
				this.banker.request(tmpReqUnits);
				requestedUnits += tmpReqUnits;
			}
			long millis = (rand.nextLong() % (this.maxSleepMillis - this.minSleepMillis)) + this.minSleepMillis;
			try {
				Thread.sleep(millis); 
			} catch(InterruptedException e){}
		}
		this.banker.release(requestedUnits);
	}


}
