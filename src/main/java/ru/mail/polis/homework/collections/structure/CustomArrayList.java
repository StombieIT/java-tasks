package ru.mail.polis.homework.collections.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Необходимо реализовать свой ArrayList (динамический массив).
 * При изменении размера массива помните про метод System.arraycopy()
 *
 * Задание оценивается в 10 тугриков
 */
public class CustomArrayList<E> implements List<E> {

    private static final int INITIAL_SIZE = 16;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private E[] array;
    private int size;
    private int modCount;

    public CustomArrayList() {
        size = 0;
        modCount = 0;
        array = (E[]) new Object[INITIAL_SIZE];
    }

    private static void checkIndex(int lowerBound, int index, int upperBound) {
        if (index < lowerBound || index > upperBound) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void checkIndex(int index) {
        checkIndex(0, index, size - 1);
    }

    private void growLazy(int count) {
        if (size + count > array.length) {
            grow(size + count - array.length);
        }
    }

    private void grow(int minCount) {
        if (MAX_ARRAY_SIZE - minCount < array.length) {
            throw new OutOfMemoryError();
        }
        int growth = minCount;
        int strongGrowth = array.length >> 1;
        if (MAX_ARRAY_SIZE - strongGrowth >= array.length && strongGrowth > growth) {
            growth = strongGrowth;
        }
        array = Arrays.copyOf(
                array,
                array.length + growth
        );
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new CustomListIterator();
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(array, size);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) Arrays.copyOf(array, size, a.getClass());
        }
        System.arraycopy(array, 0, a, 0, size);
        return a;
    }

    @Override
    public boolean add(E e) {
        growLazy(1);
        array[size++] = e;
        modCount++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int oIndex = indexOf(o);
        if (oIndex >= 0) {
            remove(oIndex);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        growLazy(c.size());
        System.arraycopy(c.toArray(), 0, array, size, c.size());
        size += c.size();
        modCount++;
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkIndex(0, index, size);
        if (c.isEmpty()) {
            return false;
        }
        growLazy(c.size());
        System.arraycopy(array, index, array, index + c.size(), size - index);
        System.arraycopy(c.toArray(), 0, array, index, c.size());
        size += c.size();
        modCount++;
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (int i = 0; i < size; i++) {
            if (c.contains(array[i])) {
                remove(i--);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        for (int i = 0; i < size; i++) {
            if (!c.contains(array[i])) {
                remove(i--);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        Arrays.fill(array, null);
        modCount++;
        size = 0;
    }

    @Override
    public E get(int index) {
        checkIndex(index);
        return array[index];
    }

    @Override
    public E set(int index, E element) {
        checkIndex(index);
        E oldValue = array[index];
        array[index] = element;
        modCount++;
        return oldValue;
    }

    @Override
    public void add(int index, E element) {
        checkIndex(0, index, size);
        growLazy(1);
        System.arraycopy(array, index, array, index + 1, size - index);
        array[index] = element;
        modCount++;
        size++;
    }

    @Override
    public E remove(int index) {
        checkIndex(index);
        E oldValue = array[index];
        System.arraycopy(array, index + 1, array, index, size - index - 1);
        size--;
        modCount++;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        return indexOfRange(o, 0, size);
    }

    @Override
    public int lastIndexOf(Object o) {
        return lastIndexOfRange(o, 0, size);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new CustomListIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new CustomListIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > size) {
            throw new IllegalStateException();
        }
        return new CustomSubList(fromIndex, toIndex);
    }

    private int indexOfRange(Object o, int fromInclusive, int toExclusive) {
        for (int i = fromInclusive; i < toExclusive; i++) {
            if (Objects.equals(array[i], o)) {
                return i;
            }
        }
        return -1;
    }

    private int lastIndexOfRange(Object o, int fromInclusive, int toExclusive) {
        for (int i = toExclusive - 1; i >= fromInclusive; i--) {
            if (Objects.equals(array[i], o)) {
                return i;
            }
        }
        return -1;
    }

    private class CustomSubList implements List<E> {

        private final int offset;
        private final CustomSubList parent;
        private int fixedModCount = modCount;
        private int size;

        public CustomSubList(int fromInclusive, int toExclusive) {
            this(null, fromInclusive, toExclusive);
        }

        public CustomSubList(CustomSubList parent, int fromInclusive, int toExclusive) {
            this.parent = parent;
            this.offset = fromInclusive;
            this.size = toExclusive - fromInclusive;
        }

        @Override
        public int size() {
            checkModCount();
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }

        // TODO: fix custom list iterator
        @Override
        public Iterator<E> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            checkModCount();
            return Arrays.copyOfRange(array, offset, offset + size);
        }

        @Override
        public <T> T[] toArray(T[] a) {
            checkModCount();
            if (a.length < size) {
                return (T[]) Arrays.copyOfRange(a, offset, offset + size, a.getClass());
            }
            System.arraycopy(array, offset, a, 0, size);
            return a;
        }

        @Override
        public boolean add(E e) {
            add(offset + size, e);
            return true;
        }

        @Override
        public boolean remove(Object o) {
            int oIndex = indexOf(o);
            if (oIndex >= 0) {
                remove(oIndex);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            checkModCount();
            for (Object element : c) {
                if (!contains(element)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return addAll(offset + size, c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            checkModCount();
            checkIndex(0, index, size);
            if (c.isEmpty()) {
                return false;
            }
            CustomArrayList.this.addAll(offset + index, c);
            updateSizeAndModCount(c.size());
            return true;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            checkModCount();
            boolean changed = false;
            for (int i = 0; i < size; i++) {
                if (c.contains(get(i))) {
                    remove(i--);
                    changed = true;
                }
            }
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            checkModCount();
            boolean changed = false;
            for (int i = 0; i < size; i++) {
                if (!c.contains(get(i))) {
                    remove(i--);
                    changed = true;
                }
            }
            return changed;
        }

        // TODO: fix clear
        @Override
        public void clear() {
        }

        @Override
        public E get(int index) {
            checkModCount();
            checkIndex(0, index, size - 1);
            return CustomArrayList.this.get(offset + index);
        }

        @Override
        public E set(int index, E element) {
            checkModCount();
            checkIndex(0, index, size - 1);
            E oldValue = CustomArrayList.this.set(offset + index, element);
            updateSizeAndModCount(0);
            return oldValue;
        }

        @Override
        public void add(int index, E element) {
            checkModCount();
            checkIndex(0, index, size);
            CustomArrayList.this.add(offset + index, element);
            updateSizeAndModCount(1);
        }

        @Override
        public E remove(int index) {
            checkModCount();
            checkIndex(0, index, size - 1);
            E oldValue = CustomArrayList.this.remove(offset + index);
            updateSizeAndModCount(-1);
            return oldValue;
        }

        @Override
        public int indexOf(Object o) {
            checkModCount();
            int index = indexOfRange(o, offset, offset + size);
            if (index >= 0) {
                return index - offset;
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            checkModCount();
            int lastIndex = indexOfRange(o, offset, offset + size);
            if (lastIndex >= 0) {
                return lastIndex - offset;
            }
            return -1;
        }

        // TODO: fix list iterator
        @Override
        public ListIterator<E> listIterator() {
            return null;
        }

        // TODO: fix list iterator
        @Override
        public ListIterator<E> listIterator(int index) {
            return null;
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            if (fromIndex < 0 || fromIndex > toIndex || toIndex > size) {
                throw new IllegalStateException();
            }
            return new CustomSubList(this, fromIndex, toIndex);
        }

        private void updateSizeAndModCount(int sizeDelta) {
            fixedModCount = modCount;
            size += sizeDelta;
            if (parent != null) {
                parent.updateSizeAndModCount(sizeDelta);
            }
        }

        private void checkModCount() {
            if (fixedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private class CustomListIterator implements ListIterator<E> {

        private int lastTaken;
        private int currentIndex;
        private int fixedModCount;

        public CustomListIterator() {
            this(0);
        }

        public CustomListIterator(int initialIndex) {
            currentIndex = initialIndex;
            fixedModCount = modCount;
            lastTaken = -1;
        }

        @Override
        public boolean hasNext() {
            checkModCount();
            return currentIndex < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastTaken = currentIndex;
            return array[currentIndex++];
        }

        @Override
        public boolean hasPrevious() {
            checkModCount();
            return currentIndex > 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            return array[--currentIndex];
        }

        @Override
        public int nextIndex() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return currentIndex;
        }

        @Override
        public int previousIndex() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            return currentIndex - 1;
        }

        @Override
        public void remove() {
            checkModCount();
            checkLastTaken();
            CustomArrayList.this.remove(lastTaken);
            lastTaken = -1;
            fixedModCount = modCount;
        }

        @Override
        public void set(E e) {
            checkModCount();
            checkLastTaken();
            CustomArrayList.this.set(currentIndex, e);
            fixedModCount = modCount;
        }

        @Override
        public void add(E e) {
            checkModCount();
            CustomArrayList.this.add(currentIndex++, e);
            lastTaken = -1;
            fixedModCount = modCount;
        }

        private void checkLastTaken() {
            if (lastTaken < 0) {
                throw new IllegalStateException();
            }
        }

        private void checkModCount() {
            if (fixedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}