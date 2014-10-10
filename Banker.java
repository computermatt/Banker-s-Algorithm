import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class Banker {

	private static final String CLAIM = "Thread %s sets a claim for %d units.";
	private static final String REQUEST = "Thread %s requests %d units.";
	private static final String ALLOCATE = "Thread %s has %d units allocated.";
	private static final String WAIT = "Thread %s waits.";
	private static final String WAKE = "Thread %s awakened.";
	private static final String RELEASE = "Thread %s releases %d units.";

	private int nResources;

	private Map<Thread, Integer> current = Collections.synchronizedMap( new HashMap<Thread, Integer>());
	private Map<Thread, Integer> remaining = Collections.synchronizedMap( new HashMap<Thread, Integer>());
	
	public Banker( int nUnits ) {
		nResources = nUnits;
	}
	
	public synchronized void setClaim( int nUnits ) {
		Thread currentThread = Thread.currentThread();
		if( nUnits <= 0 || nUnits > nResources || hasClaim( currentThread ) )
			System.exit(1);
		current.put( currentThread, 0 );
		remaining.put( currentThread, nUnits );
		System.out.println( String.format( CLAIM, currentThread.getName(), nUnits ) );
	}
	
	public synchronized boolean request( int nUnits ) {
		//TODO
		// print message: Thread /name/ requests /nUnits/ units.
		// If allocating the resources results in a safe state,
		// -print message: Thread /name/ has /nUnits/ units allocated.
		// update banker's state and return to the caller.
		// Otherwise enter a loop that:
		// -prints message: Thread /name/ waits.
		// -waits for notification of a change
		// -when reawakened, prints message: Thread /name/ awakened.
		// -if allocating resource results in safe state, prints message:
		// --Thread /name/ has /nUnits/ units allocated.
		// --updates banker's state and returns
		Thread currentT = Thread.currentThread();
		if(nUnits <= 0 || !this.hasClaim(currentT) || this.current.get(currentT))
			System.exit(1);
		return false;
	}
	
	public synchronized void release( int nUnits ) {
		Thread currentT = Thread.currentThread();
		if(nUnits <= 0 || !this.hasClaim(currentT) || nUnits > this.current.get(currentT))
			System.exit(1);
		System.out.println(String.format(RELEASE, currentT.getName(), nUnits));
		this.current.put(currentT, this.current.get(currentT) - nUnits);
		notifyAll();
	}
	
	public synchronized int allocated() {
		return current.get( Thread.currentThread() );
	}
	
	public synchronized int remaining() {
		return remaining.get( Thread.currentThread() );
	}
	
	private boolean hasClaim( Thread t ) {
		return current.containsKey( t ) && remaining.containsKey( t ) && current.get( t ) + remaining.get( t ) > 0;
	}
}
