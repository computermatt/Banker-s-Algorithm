import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Banker {

	// print statements
	private static final String CLAIM = "Thread %s sets a claim for %d units.";
	private static final String REQUEST = "Thread %s requests %d units.";
	private static final String ALLOCATE = "Thread %s has %d units allocated.";
	private static final String WAIT = "Thread %s waits.";
	private static final String WAKE = "Thread %s awakened.";
	private static final String RELEASE = "Thread %s releases %d units.";

	// currently available resources
	private int nResources;
	
	// map of clients to allocated-remaining pairs
	private Map<Thread, Pair> claims = Collections.synchronizedMap( new HashMap<Thread, Pair>());
	
	// make a banker with nUnits total resources
	public Banker( int nUnits ) {
		nResources = nUnits;
	}
	
	// set a claim for nUnits units, making the calling client's 'remaining' = nUnits
	public synchronized void setClaim( int nUnits ) {
		Thread currentThread = Thread.currentThread();
		if( nUnits <= 0 || nUnits > nResources || hasClaim( currentThread ) )
			System.exit(1);
			
		claims.put( currentThread, new Pair( 0, nUnits ) );
		System.out.println( String.format( CLAIM, currentThread.getName(), nUnits ) );
	}
	
	// request nUnits resources for the current thread
	// if the request would result in an unsafe state, the thread waits until the banker has more resources
	public synchronized boolean request( int nUnits ) {
		Thread currentT = Thread.currentThread();
		if(nUnits <= 0 || !this.hasClaim(currentT) || nUnits > this.claims.get(currentT).remaining)
			System.exit(1);
		
		System.out.println( String.format( REQUEST, currentT.getName(), nUnits ));

		// wait until safe state
		while( !safe( nResources - nUnits, buildPotentialStateList( currentT, nUnits ) ) ) {
			System.out.println( String.format( WAIT, currentT.getName() ) );
			try {
				wait();
			} catch( InterruptedException e ) {
				e.printStackTrace();
			}
			System.out.println( String.format( WAKE, currentT.getName() ) );
		}
		
		// allocate resources
		System.out.println( String.format( ALLOCATE, currentT.getName(), nUnits ) );
		claims.get( currentT ).allocated += nUnits;
		claims.get( currentT ).remaining -= nUnits;
		nResources -= nUnits;
		return true;
	}
	
	// release nUnits resources
	public synchronized void release( int nUnits ) {
		Thread currentT = Thread.currentThread();
		if(nUnits <= 0 || !this.hasClaim(currentT) || nUnits > this.claims.get(currentT).allocated)
			System.exit(1);
			
		System.out.println(String.format(RELEASE, currentT.getName(), nUnits));
		this.claims.get(currentT).allocated -= nUnits;
		nResources += nUnits;
		
		// notify all waiting threads that the banker has more resources so their requests might be able to complete
		notifyAll();
	}

	// get number of resources allocated to current thread
	public synchronized int allocated() {
		return claims.get( Thread.currentThread() ).allocated;
	}
	
	// get number of resources remaining in current thread's claim
	public synchronized int remaining() {
		return claims.get( Thread.currentThread() ).remaining;
	}
	
	// whether or not the given thread has a claim
	private boolean hasClaim( Thread t ) {
		return claims.containsKey( t ) && claims.get( t ).allocated + claims.get( t ).remaining > 0;
	}
	
	// builds a list representing a potential state, for use in the banker's algorithm
	// the resulting list reflects nUnits being allocated to currentThread, and is sorted by increasing remaining resources
	// this must not modify banker's state (map is deep-copied into list)
	private List<Pair> buildPotentialStateList( Thread currentThread, int nUnits ) {
		List<Pair> list = new ArrayList<Pair>(claims.size());
		
		// fill the list with values copied from map, with resources allocated to the currentThread
		for( Map.Entry<Thread, Pair> entry : claims.entrySet() ) {
			if( entry.getKey().equals( currentThread ) )
				list.add( new Pair( entry.getValue().allocated + nUnits, entry.getValue().remaining - nUnits ) );
			else
				list.add( new Pair( entry.getValue().allocated, entry.getValue().remaining ) );
		}
		
		// sort the list by ascending remaining
		Collections.sort( list, new Comparator<Pair>() {
				public int compare( Pair p1, Pair p2 ) {
					return p1.remaining - p2.remaining;
				}
			} );
		
		return list;
	}
	
	// the banker's algorithm - returns true iff given state is "safe"
	private boolean safe( int resources, List<Pair> state ) {
		for( Pair p : state ) {
			if( p.remaining > resources ) return false;
			resources += p.allocated;
		}
		return true;
	}
	
	// data structure containing both allocated and remaining resources
	private static class Pair {
		public int allocated;
		public int remaining;
		public Pair(int a, int r) {
			allocated = a;
			remaining = r;
		}
	}
}
