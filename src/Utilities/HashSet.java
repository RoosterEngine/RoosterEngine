package Utilities;

/**
 * documentation
 * User: davidrusu
 * Date: 15/03/13
 * Time: 7:24 PM
 */
public class HashSet {
    private static final int INITIAL_SIZE = 16; //this must be a power of 2
    private static final double LOAD_FACTOR = 0.75;
    private int maxUseableSize;
    private UnorderedArrayList[] items = new UnorderedArrayList[INITIAL_SIZE];
    private int numItems = 0;

    //not allowing initial size as it must be a power of 2 and we don't want to check / fix that
    public HashSet(){
        createLists();
    }

    private void createLists(){
        maxUseableSize = (int)(items.length * LOAD_FACTOR);
        for(int i = 0; i < items.length; i++){
            items[i] = new UnorderedArrayList(4);
        }
    }

    public void add(Object item){
        if(numItems >= maxUseableSize){
            expand();
        }
        items[getIndex(item)].add(item);
        numItems++;
    }

    public void remove(Object item){
        items[getIndex(item)].remove(item);
        numItems--;
    }

    public void clear(){
        numItems = 0;
        for(int i = 0; i < items.length; i++){
            items[i].clear();
        }
    }

    public boolean contains(Object item){
        return items[getIndex(item)].contains(item);
    }

    private int getIndex(Object item){
        int h = item.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);
        h ^= (h >>> 7) ^ (h >>> 4);
        return h & (items.length - 1);
    }

    private void expand(){
        UnorderedArrayList[] oldItems = items;
        items = new UnorderedArrayList[oldItems.length * 2];//this needs to be a 2 so that the size remains a power of 2
        createLists();
        numItems = 0;
        for (int i = 0; i < oldItems.length; i++){
            UnorderedArrayList list = items[i];
            int listSize = list.size();
            for (int j = 0; j < listSize; j++){
                add(list.get(j));
            }
        }
    }
}
