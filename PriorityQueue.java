package pq;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implement a min priority queue. The priority queue stores pairs (p,e),  
 * where p is the priority and e is an element. The priority cannot be 
 * negative. It is implemented as a min heap implemented as an array. In a
 * min priority queue, the node with the minimum priority is at the top.
 * All child nodes have larger priorities than their parent nodes. Thus
 * the maximum priorities are in the leaf nodes. To add to the queue, the new
 * node is added at the end (corresponding to the next location filling top to 
 * bottom then left to right in the tree), then swapped with parent if parent is 
 * bigger etc. To pop the mimimum priority node from the queue, the root node is
 * swapped with the last node. Then compare the new root with its children and, if
 * larger, swap with the largest child, etc.
 * 	 
 * The nodes are the Pair objects as described in the Pair class.
 * Suppose the nodes are numbered 0-5 with priorities 5-0, respectively.
 * We could then have:
 * 
 *                      <0,5>
 *                <2,3>        <1,4>
 *             <5,0> <3,2>  <4,1>
 * 
 *  then the heap is organized as: 
 * 
 *  heap = [<0,5>, <2,3>, <1,4>, <5,0>, <3,2>, <4,1>]
 *  
 *  the heap is an ArrayList so that we can modify the size dynamically.
 *  
 *  Elements must all be unique, but priorities can be duplicated.
 *  To efficiently pop off a node, we need to know its location
 *  Thus we also store the locations, which we store in a HashMap
 *  so that they can be accessed in constant time. These have the form 
 *  (element, location), where element is the key. They can be in any 
 *  order, here they are shown in the same order as the heap, for ease
 *  of comparison.
 *  
 *               e  location in heap
 *               |  /
 *  location = [(5,0), (3,1) (4,2) (0,3) (2,4) (1,5) ]
 *  
 *  Accessing the map (location):
 *    location.get(element);           <- get the index for a given element
 *	  location.put(element, index);    <- put a new index for a given element
 *	  location.replace(element, j);    <- replace an index for an element
 *
 *
 * 
 * @author Penny Rowe 
 * (using instructions and method docstrings from Prof. America Chambers, CS361, Fall 2021)
 *
 * @version 2021/09/27
 */
public class PriorityQueue {
	
	// Each pair (p,e) in the priority queue will be called a node. We will 
	// store the nodes in a HashMap so they can be added or removed in
	// constant time. The superclass will be Map.
	protected Map<Integer, Integer> location;
	
	
	
	// Pair is the wrapper class provided by Prof. Chambers. In Pair,
	// variables are used for the types of the two objects. Here 
	// we declare the types for the Pair as Integer and Integer and create
	// a List of Pair objects assigned the identifier heap. List will be the
	// superclass, while the subclass will be ArrayList so that we can 
	// resize. Thus heap is a reference of type List that will point to an 
	// object of type ArrayList (see below).
	protected List<Pair<Integer, Integer>> heap; 
	
	
	
	/**
	 * Construct an empty priority queue.
	 * 
	 * 	
	 *  Use an ArrayList so that we can resize it when we push or pop.
	 */
	public PriorityQueue() {
		// Initialize variables in the constructor because this is necessary if
		// they take inputs; better to have it together so nothing is forgotten.
		heap = new ArrayList<Pair<Integer, Integer>>();
		location = new HashMap<Integer, Integer>();
	}
	
	
	/**
	 *  Add an element to the priority queue with the given priority. 
	 *  Elements are added at the bottom and then move up to their 
	 *  correct place based on their priority.
	 * 
	 *  @param priority priority of the element to be inserted
	 *  @param element  element to be inserted
	 * 
	 *  given
	 *    <br><br>
	 *    <b> Preconditions:</b>
	 *    <ul>
	 *    <li> The element does not already appear in the priority queue.
	 *    <li> The priority is non-negative. </li>
	 *    </ul>
	 */
	public void push(int priority, int element) {
		
		// Throw assertion error if the element already appears in the priority queue.
		// or if the priority is negative.
		assertEquals(false, isPresent(element));
		assertEquals(true, priority >= 0);
				
		// Get the start_index as the current size, since the index is size-1. Then
		// add new element to the end of the heap; this will increment the heap size.
		// Finally, put the index to the new element in the location map 
		int start_index = size();  // the index is the size-1, so get it before adding new node
		heap.add(new Pair<Integer, Integer>(priority, element));
		location.put(element, start_index);
		
		// Now percolate up, since the new node may have a lower priority than its parents 
		// The current location is the end of the array, which is the current size - 1,
		// since we index from 0
		percolateUp(size()-1);		
	}
	
	
	/**
	 * Remove the highest priority element from the priority queue
	 * Remember that the highest priority element corresponds to the
	 * minimum value, because it is a min priority queue.
	 *  <br><br>
	 *	<b>Preconditions:</b>
	 *	<ul>
	 *	<li> The priority queue is non-empty.</li>
	 *	</ul>
	 *  
	 */
	public void pop() {
		assertEquals(false, isEmpty());

		// Swap the root node with the final leaf node
		swap(0, size()-1);
		
		// Pop off the final leaf node
		heap.remove(size()-1);
		location.remove(size());
				
		// Push down the new root node
		pushDown(0);

	}


	/**
	 * Returns the highest priority value from the priority queue
	 * (without removing it from the queue.) The highest priority value
	 * is at the root.
	 * 
	 * @return priority highest priority value
	 * 
	 * Preconditions:
	 *   -  The priority queue is non-empty
	 */
	public int topPriority() {
		assertEquals(false, isEmpty());
		
		Pair<Integer,Integer> output = heap.get(0);		
		return output.priority;
	}


	/**
	 * Returns the element with the highest priority
	 * 
	 * @return element element with highest priority
	 * 
	 * Preconditions:
	 *   -  The priority queue is non-empty
	 */
	public int topElement() {
		assertEquals(false, isEmpty());

		Pair<Integer,Integer> output = heap.get(0);
		return output.element;
	}


	
	/**
	 *  Change the priority of an element already in the
	 *  priority queue.
	 *  
	 *  @param newpriority the new priority	  
	 *  @param element element whose priority is to be changed
	 *  <br><br>
	 *	<b>Preconditions:</b>
	 *	<ul>
	 *	<li> The element exists in the priority queue</li>
	 *	<li> The new priority is non-negative </li>
	 *	</ul>
	 */
	public void changePriority(int newpriority, int element) {
		assertEquals(true, isPresent(element));
		assertEquals(true, newpriority >= 0);

		// The priority is in the heap, and the heap is just an ArrayList. 
		// We want to do this in constant time, so first get the index to to the heap.
		// That's in location, and you get it using the key, which is element
		int i = location.get(element);		    // get the index for a given element
		heap.set(i, new Pair<Integer, Integer>(newpriority, element));
		
		// Next we need to push it down or percolate it up
		pushDown(i);		
		percolateUp(i);
	}


	/**
	 *  Gets the priority of an element
	 *  
	 *  @param element the element whose priority is returned
	 *  @return the priority value
	 *  
	 *  <br><br>
	 *	<b>Preconditions:</b>
	 *	<ul>
	 *	<li> The element exists in the priority queue</li>
	 *	</ul>
	 */
	public int getPriority(int element) {
		
		// Check precondition: the element exists in the priority queue
		assertEquals(true, isPresent(element));
		
		int i = location.get(element);           // get the index for a given element
		return heap.get(i).priority;
	}

	
	/**
	 *  Returns true if the priority queue contains no elements
	 *  
	 *  @return true if the queue contains no elements, false otherwise
	 */
	public boolean isEmpty() {
		return size() == 0 ;
	}

	
	/**
	 *  Returns true if the element exists in the priority queue.
	 *  
	 *  @return true if the element exists, false otherwise
	 */
	public boolean isPresent(int element) {
		return location.containsKey(element);
	}
	

	/**
	 *  Removes all elements from the priority queue
	 *  and the map (location)
	 */
	public void clear() {
		location.clear();
		heap.clear();	
	}

	
	/**
	 *  Returns the number of elements in the priority queue
	 *  @return number of elements in the priority queue
	 */
	public int size() {
		return heap.size();
	}


	
	/*********************************************************
	 * 				Private helper methods
	 *********************************************************/
	

	/**
	 * Push down the element at the given position in the heap 
	 * 
	 * @param start_index the index of the element to be pushed down
	 * @return the index in the list where the element is finally stored
	 */
	private int pushDown(int start_index) {	
		
		int iparent = start_index;
		int ileft;
		int iright;
		int priority_parent;
		int priority_left;
		int priority_right;

		do {

			// If the parent is a leaf, we are finished
			if (isLeaf(iparent)) {
				return iparent;
			}
			
			// Get the parent priority
			priority_parent = heap.get(iparent).priority;
			
			// Get left child and compare parent to child or children
			ileft = left(iparent);
			priority_left = heap.get(ileft).priority;
			if (hasTwoChildren(iparent)) {
				iright = right(iparent);
				// Swap with child with greater priority (smaller number)
				priority_right = heap.get(iright).priority;
				if (priority_left <= priority_right && priority_left < priority_parent) {
					swap(iparent, ileft);
					iparent = ileft;					
				}
				else if (priority_right < priority_parent) {
					swap(iparent, iright);
					iparent = iright;
					priority_left = priority_right;
				}
			}
			else if (priority_left < priority_parent ) {
					swap(iparent, ileft);
					iparent = ileft;
			}
			
		
		} while (priority_left < priority_parent);
		
		return iparent;
	}

	/**
	 * Percolate up the element at the given position in the heap 
	 * 
	 * @param start_index the index of the element to be percolated up
	 * @return the index in the list where the element is finally stored
	 */
	private int percolateUp(int start_index) {
		// The child node is at the start_index
		int ichild = start_index;
		int priority_parent;
		int priority_child;
		
		do {
			// Get the priority of the child node. No need to use getPriority,
			// since we already have the index.
			priority_child = heap.get(ichild).priority;

			// Get the index to the location of the parent node and the parent priority
			int iparent = parent(ichild);
			priority_parent = heap.get(iparent).priority;
			
			// If Parent priority > Child priority, swap
			if (priority_parent > priority_child) {
				swap(iparent, ichild);
				ichild = iparent;
			}
			
		} while (priority_parent > priority_child);

		// return child index.			
		return ichild;
	}


	/**
	 * Swaps two elements in the priority queue by updating BOTH
	 * the list representing the heap AND the map
	 * 
	 * Remember that heap points to an ArrayList object, so index it,
	 * while location points to a HashMap, so set the values (locations) 
	 * by keys (element names)
	 * 
	 * @param i The index of the element to be swapped, in the heap
	 * @param j The index of the element to be swapped, in the heap
	 */
	private void swap(int i, int j) {
		// Get temporary references to nodes i and j
		Pair<Integer, Integer> node_i = heap.get(i);
		Pair<Integer, Integer> node_j = heap.get(j);
		
		// The index is an index to the hashMap, which holds the nodes
		heap.set(i, node_j);
		heap.set(j, node_i);
		
		// Location for the element in node j becomes location for the
		// element in node i and vice versa
		location.replace(node_i.element, j);
		location.replace(node_j.element, i);
	}

	
	/**
	 * Computes the index of the element's left child
	 * @param parent index of element in list
	 * @return index of element's left child in list
	 */
	private int left(int parent) {
		return (int) (parent*2 + 1);
	}

	/**
	 * Computes the index of the element's right child
	 * @param parent index of element in list
	 * @return index of element's right child in list
	 */
	private int right(int parent) {
		return (int) (parent*2 + 2);
	}

	/**
	 * Computes the index of the element's parent
	 * 
	 * @param child index of element in list
	 * 
	 * @return index of element's parent in list
	 * 
	 *   We have to return an int, so what to do if child is root 
	 *   (no parent)???
	 *   
	 *   Also, if the child is outside the tree, should bomb?
	 */
	private int parent(int child) {
		return (int) (child-1)/2;
	}
	
	/**
	 * Returns true if element is a leaf in the heap
	 * @param i index of element in heap
	 * @return true if element is a leaf
	 */
	private boolean isLeaf(int i){
		
		if (left(i) >= size() && right(i) >= size()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if element has two children in the heap
	 * @param i index of element in the heap
	 * @return true if element in heap has two children
	 */
	private boolean hasTwoChildren(int i) {
		if (left(i) < size() && right(i) < size()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Print the underlying list representation
	 */
	private void printHeap() {
		// Iterate using for-each loop and print each element of heap
		System.out.print("Heap: (priority, element): ");
		for (Pair<Integer, Integer> currentNode : heap) {
			System.out.print("(" + currentNode.priority + "," + currentNode.element + "), ");
		}
		System.out.println("");
	}

	/**
	 * Print the entries in the location map
	 */
	private void printMap() {
		// Iterate using for-each loop and print each element of map
		System.out.print("Map: (element:location): ");
		
		// I looked this up because I don't know how to iterate over a map yet.
		for (Map.Entry<Integer, Integer> entry : location.entrySet()) {
		    System.out.print("(" + entry.getKey() + ":" + entry.getValue() + "), ");
		}
		System.out.println("");
	}
	
	
}