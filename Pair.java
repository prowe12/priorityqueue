package pq;

/**
 * A wrapper class for priority queue elements
 * 
 * @author americachambers
 *
 */
public class Pair<P, E> {
	
	// P and E are type variables, which will be set later
	// That way Pair can store two objects of any type
	// But why can't we just use a HashMap with key and value?
	public P priority;
	public E element;
	
	public Pair(P p, E e) {
		priority = p;
		element = e;
	}

}
