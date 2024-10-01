package server;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConcurrentArrayList<T> implements Iterable<T> {
    private final List<T> list = new ArrayList<>();

    public void add(T t) {
        list.add(t);
    }

    public void remove(T t) {
        list.remove(t);
    }

    public void clear() {
        list.clear();
    }

    public int size() {
        return list.size();
    }

    @Override
    public Iterator<T> iterator() {
        // TODO Auto-generated method stub
        return list.iterator();
    }

    public class ConcurrentArrayListIterator<T> implements Iterator<T> {
        private final Iterator<T> iterator;

        public ConcurrentArrayListIterator() {
            this.iterator = (Iterator<T>) list.iterator();
        }
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next();
        }

        @Override
        synchronized public void remove() {
            iterator.remove();
        }

    }
}
