package Utilities;

/**
 * documentation
 * User: davidrusu
 * Date: 28/02/13
 * Time: 5:35 PM
 */
public interface List<T> {

    public void add(T element);

    public T get(int index);

    public boolean remove(T element);

    public T remove(int index);

    public boolean contains(T element);

    public void clear();

    public int size();
}
