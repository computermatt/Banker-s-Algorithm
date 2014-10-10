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

	private Map<Thread, Integer> allocated = Collections.synchronizedMap( new HashMap<Thread, Integer>());
	private Map<Thread, Integer> remaining = Collections.synchronizedMap( new HashMap<Thread, Integer>());
	
	public Banker( int nUnits ) {
		nResources = nUnits;
	}
	
	public synchronized void setClaim( int nUnits ) {
		Thread currentThread = Thread.currentThread();
		if( nUnits <= 0 || nUnits > nResources || hasClaim( currentThread ) )
			System.exit(1);
		allocated.put( currentThread, 0 );
		remaining.put( currentThread, nUnits );
		System.out.println( String.format( CLAIM, currentThread.getName(), nUnits ) );
	}
	
	public synchronized boolean request( int nUnits ) {
		Thread currentT = Thread.currentThread();
		if(nUnits <= 0 || !this.hasClaim(currentT) || nUnits > this.remaining.get(currentT))
			System.exit(1);
		
		System.out.println( String.format( REQUEST, currentT.getName(), nUnits ));

		// check potential state
		int[] allocatedArray = new int[allocated.size()];
		int[] remainingArray = new int[remaining.size()];
		fillArraysForPotentialState( allocatedArray, remainingArray, currentT, nUnits );
		
		while( !safe( nResources - nUnits, allocatedArray, remainingArray ) ) {
			// wait
			System.out.println( String.format( WAIT, currentT.getName() ) );
			try {
				wait();
			} catch( InterruptedException e ) {
				e.printStackTrace();
			}
			System.out.println( String.format( WAKE, currentT.getName() ) );
			
			// check again with new state
			fillArraysForPotentialState( allocatedArray, remainingArray, currentT, nUnits );
		}
		
		// allocate resources
		System.out.println( String.format( ALLOCATE, currentT.getName(), nUnits ) );
		allocated.put( currentT, allocated.get( currentT ) + nUnits );
		remaining.put( currentT, remaining.get( currentT ) - nUnits );
		nResources -= nUnits;
		return true;
	}
	
	public synchronized void release( int nUnits ) {
		Thread currentT = Thread.currentThread();
		if(nUnits <= 0 || !this.hasClaim(currentT) || nUnits > this.allocated.get(currentT))
			System.exit(1);
		System.out.println(String.format(RELEASE, currentT.getName(), nUnits));
		this.allocated.put(currentT, this.allocated.get(currentT) - nUnits);
		nResources += nUnits;
		notifyAll();
	}
	
	public synchronized int allocated() {
		return allocated.get( Thread.currentThread() );
	}
	
	public synchronized int remaining() {
		return remaining.get( Thread.currentThread() );
	}
	
	private boolean hasClaim( Thread t ) {
		return allocated.containsKey( t ) && remaining.containsKey( t ) && allocated.get( t ) + remaining.get( t ) > 0;
	}
	
	private void fillArraysForPotentialState( int[] allocated, int[] remaining, Thread currentThread, int nUnits ) {
		int i = 0;
		for( Thread t : this.allocated.keySet() ) {
			if( t.equals( currentThread ) ) {
				allocated[i] = this.allocated.get( t ) + nUnits;
				remaining[i++] = this.remaining.get( t ) - nUnits;
			} else {
				allocated[i] = this.allocated.get( t );
				remaining[i++] = this.remaining.get( t );
			}
		}
	}
	
	private boolean safe( int resources, int[] allocated, int[] remaining ) {
		return false;
	}
}
