public class Banker {

	private int nUnits;

	public Banker( int nUnits ) {
		this.nUnits = nUnits;
	}
	
	public void setClaim( int nUnits ) {
		//TODO
		// current thread is accessible via Thread.currentThread()
		// System.exit(1) if:
		// -thread already has a claim registered, or
		// -nUnits is not strictly positive, or
		// -nUnits exceeds number resources in the system
		// Associate thread with current claim = nUnits and current allocation = 0
		// print message: Thread /name/ sets a claim for /nUnits/ units.
		// -/name/ = Thread.currentThread().getName()
		// -nUnits is number resources claimed
		// return
	}
	
	public boolean request( int nUnits ) {
		//TODO
		// System.exit(1) if:
		// -current thread has no claim registered, or
		// -nUnits is not strictly positive, or
		// -nUnits exceeds invoking thread's remaining claim
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
		return false;
	}
	
	public void release( int nUnits) {
		//TODO
		// System.exit(1) if:
		// -current thread has no claim registered, or
		// -nUnits is not strictly positive, or
		// -nUnits exceeds number of units allocated to the current thread.
		// print message: Thread /name/ releases nUnits units.
		// release nUnits of the units allocated to the current thread
		// notify all waiting threads
		// return
	}
	
	public int allocated() {
		//TODO return # units allocated to current thread
		return 0;
	}
	
	public int remaining() {
		//TODO return # units remaining in current thread's claim
		return 0;
	}
}