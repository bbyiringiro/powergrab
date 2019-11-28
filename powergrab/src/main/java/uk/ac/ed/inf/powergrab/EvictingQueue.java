package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

public class EvictingQueue<K> extends ArrayList<K> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7598907934193826633L;
	private int maxSize;

    public EvictingQueue(int size){
        this.maxSize = size;
    }

    public boolean add(K k){
        boolean r = super.add(k);
        if (size() > maxSize){
            removeRange(0, size() - maxSize);
        }
        return r;
    }

    public K getYoungest() {
        return get(size() - 1);
    }

    public K getOldest() {
        return get(0);
    }
}