package indexing;

import common.RecordPointer;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

public class BPlusTree<K> implements IBPlusTree<K> {
    //TODO if reading from disk, initialize based on largest id written to disk + 1
    public static int curId = 0;

    private String indexName;
    private String tableName;
    private boolean isLeaf;
    private final int id;
    //TODO as per the writeup, the degree is determined by the page size and size of attributes being stored
    // rework as per the writeup
    private final int degree;
    // refers to the node ID that is less than all searchKeys in children
    private Integer less;
    // refers to the parent node ID
    private Integer parent;
    // if a leaf node, refers to the subsequent node ID
    private Integer next;
    // This ensures when we get the set of keys, they are in order
    // key: searchKey
    // value: BPlusTree node ID. all children searchKeys are greater than K
    private TreeMap<K,Integer> children = new TreeMap<>();
    // The actual index
    // key: searchKey
    // value: pageID and index (of the page) this searchKey appears in
    private TreeMap<K,ArrayList<RecordPointer>> index = new TreeMap<>();

    public BPlusTree(String indexName, boolean isLeaf, int degree, String tablename, Integer parent) {
        this.indexName = indexName;
        this.isLeaf = isLeaf;
        this.degree = degree;
        this.tableName = tablename;
        this.parent = parent;
        this.id = BPlusTree.curId++;
    }

    //TODO read in data from file based on path, and set values from there
    public BPlusTree(String path) {
        this.id = 0;
        this.degree = 0;
    }

    public boolean isLeaf() {
        return this.isLeaf;
    }

    private int getSize() {
        if(this.isLeaf()) {
            int size = 0;
            for(K key : this.index.keySet())
                size += this.index.get(key).size();

            return size;
        }

        return this.children.size();
    }

    private Integer compare(K left, K right) {
        if(left instanceof Integer)
            return ((Integer)left).compareTo((Integer)right);

        if(left instanceof Double)
            return ((Double)left).compareTo((Double)right);

        if(left instanceof Boolean)
            return ((Boolean)left).compareTo((Boolean)right);

        if(left instanceof String)
            return ((String)left).compareTo((String)right);

        // Trying to compare invalid types
        return null;
    }

    private void addToIndex(K searchKey, RecordPointer rp) {
        if(!this.index.keySet().contains(searchKey))
            this.index.put(searchKey, new ArrayList<RecordPointer>());

        this.index.get(searchKey).add(rp);
    }

    private void addToIndex(K searchKey, ArrayList<RecordPointer> rps) {
        if(!this.index.keySet().contains(searchKey))
            this.index.put(searchKey, new ArrayList<RecordPointer>());

        for(RecordPointer rp : rps)
            this.index.get(searchKey).add(rp);
    }

    private RecordPointer removeFromIndex(K searchKey) {
        // if the index doesn't contain the searchKey, nothing to return
        if(!this.index.keySet().contains(searchKey))
            return null;

        // doesn't matter which rp we are returning. they have the same searchKey, ergo they are equivilent
        RecordPointer rp = this.index.get(searchKey).remove(0);

        // if there are no more record pointers, remove the searchKey
        if(this.index.get(searchKey).size() == 0)
            this.index.remove(searchKey);

        return rp;
    }

    private void split() {
        if(this.getSize() < degree)
            return;

        int mid = this.children.size() / 2;
        // either the parent (read from disk), or a new node (which will become the parent)
        BPlusTree<K> parent = null;
        if(this.parent == null)
            parent = new BPlusTree<>(this.indexName, false, this.degree, this.tableName, null);
        else
            parent = new BPlusTree<>(this.parent.toString());

        this.parent = parent.id;
        BPlusTree<K> split = new BPlusTree<>(this.indexName, this.isLeaf, this.degree, this.tableName, parent.id);

        // move some pointers to the new split
        for(int i = 0; i < (degree-1)/2; i++) {
            if(this.isLeaf()) {
                Entry<K,ArrayList<RecordPointer>> rpEntry = this.index.pollLastEntry();
                split.addToIndex(rpEntry.getKey(), rpEntry.getValue());
            }
            else {
                Entry<K,Integer> entry = this.children.pollLastEntry();
                split.children.put(entry.getKey(), entry.getValue());
            }
        }


        // assign self and new split as children of parent
        if(parent.less == null)
            parent.less = this.id;
        else {
            K first = parent.children.firstKey();
            parent.children.lowerEntry(first).setValue(this.id);
        }
        parent.children.put(split.children.lastKey(), split.id);
        split.next = this.next;
        this.next = split.id;

        // chance that the target is now overful and must split
        parent.split();
    }

    public boolean insertRecordPointer(RecordPointer rp, K searchKey) {
        if(this.isLeaf()) {
            this.addToIndex(searchKey, rp);
            this.split();

            return true;
        }

        Integer nextPageId = less;
        // find the key
        for(K key : this.children.keySet()){
            Integer comp = compare(key, searchKey);
            if(comp == null)
                return false;

            if(comp > 0) break;
            nextPageId = this.children.get(key);
        }

        if(nextPageId == null)
            return false;

        //TODO find on disk the nextPageId
        BPlusTree<K> fromDisk = new BPlusTree<>(nextPageId.toString());
        return fromDisk.insertRecordPointer(rp, searchKey);
    }

    private BPlusTree<K> getParent() {
        if(this.parent == null)
            return null;

        return new BPlusTree<>(this.parent.toString());
    }

    private BPlusTree<K> getLeftSibling() {
        BPlusTree<K> parent = this.getParent();
        if(parent == null)
            return null;

        Integer leftId = null;
        Integer cur = parent.less;
        for(K key : parent.children.keySet()) {
            leftId = cur;
            cur = parent.children.get(key);
            if(cur == this.id)
                return new BPlusTree<>(leftId.toString());
        }

        return null;
    }

    // TODO this is a duplicate of the code above
    // consider returning a tuple of the left and right siblings
    private BPlusTree<K> getRightSibling() {
        BPlusTree<K> parent = this.getParent();
        if(parent == null)
            return null;

        Integer cur = null;
        Integer rightId = parent.less;
        for(K key : parent.children.keySet()) {
            cur = rightId;
            rightId = parent.children.get(key);
            if(cur == this.id)
                return new BPlusTree<>(rightId.toString());
        }

        return null;
    }

    // runs under the assumption that a merge is legal
    private void merge(BPlusTree<K> sibling) {
        if(this.isLeaf())
            for(K key : sibling.index.keySet())
                this.addToIndex(key, sibling.index.get(key));
        else
            for(K key : sibling.children.keySet())
                this.children.put(key, sibling.children.get(key));

        BPlusTree<K> parent = this.getParent();

        // this is practically an impossible situation
        // a sibling must have a parent
        if(parent == null) {
            System.err.println("Parent didn't exist when merging ids " +
                    this.id + " and " + sibling.id + " of index " + this.indexName);
            return;
        }

        K keyOfThis = null;
        for(K key : parent.children.keySet())
            if(parent.children.get(key) == this.id)
                keyOfThis = key;

        // this is practically an impossible situation
        // the child must exist in the parents children
        if(keyOfThis == null) {
            System.err.println("There was a serious issue trying to merge ids " +
                    this.id + " and " + sibling.id + " of index " + this.indexName);
            return;
        }

        parent.children.remove(keyOfThis);
    }

    // runs under the assumption that a borrow is legal
    private void borrow(BPlusTree<K> sibling, boolean fromLeft) {
        if(this.isLeaf()) {
            K key = fromLeft ? sibling.index.firstKey() : sibling.index.lastKey();
            RecordPointer rp = sibling.removeFromIndex(key);
            this.addToIndex(key, rp);
        }
        else {
            Entry<K,Integer> entry = fromLeft ? sibling.children.pollLastEntry() : sibling.children.pollFirstEntry();
            this.children.put(entry.getKey(), entry.getValue());
        }

        BPlusTree<K> parent = this.getParent();

        // this is practically an impossible situation
        // a sibling must have a parent
        if(parent == null) {
            System.err.println("Parent didn't exist when moving values amongst ids " +
                    this.id + " and " + sibling.id + " of index " + this.indexName);
            return;
        }

        // update parent searchKey
        // if we steal from left, update "this"
        // if we steal from right, update sibling
        Integer targetId = fromLeft ? this.id : sibling.id;
        K targetKey = null;
        for(K key : parent.children.keySet())
            if(parent.children.get(key) == targetId)
                targetKey = key;

        // this is practically an impossible situation
        // the child must exist in the parents children
        if(targetKey == null) {
            System.err.println("There was a serious issue when moving values amongst ids " +
                    this.id + " and " + sibling.id + " of index " + this.indexName);
            return;
        }
    }

    private void resize() {
        // special case if root

        // large enough, do nothing
        if(this.getSize() > (this.degree / 2))
            return;

        BPlusTree<K> left = this.getLeftSibling();
        BPlusTree<K> right = this.getRightSibling();

        BPlusTree<K> target = null;

        // try merging left
        if(this.getSize() + left.getSize() < degree)
            target = left;

        // try merging right
        if(this.getSize() + right.getSize() < degree)
            target = right;

        if(target != null) {
            this.merge(target);
            return;
        }

        // try borrowing left
        if(left.getSize() > (this.degree / 2) + 1)
            target = left;
        // try borrowing right
        if(right.getSize() > (this.degree / 2) + 1)
            target = right;

        if(target == null) {
            System.err.println("A serious issue occured when resizing index " + this.indexName + " of id " + this.id);
            return;
        }

        this.borrow(target, target == left);
    }

    public boolean removeRecordPointer(RecordPointer rp, K searchKey) {
        if(this.isLeaf()) {
            if(!this.index.containsKey(searchKey))
                return false;
            boolean removed = this.index.get(searchKey).remove(rp);

            if(!removed)
                return false;

            resize();
            return true;
        }

        Integer nextPageId = less;
        // find the key
        for(K key : this.children.keySet()){
            Integer comp = compare(key, searchKey);
            if(comp == null)
                return false;

            if(comp > 0) break;
            nextPageId = this.children.get(key);
        }

        if(nextPageId == null)
            return false;

        //TODO find on disk the nextPageId
        BPlusTree<K> fromDisk = new BPlusTree(nextPageId.toString());
        return fromDisk.removeRecordPointer(rp, searchKey);
    }

    public ArrayList<RecordPointer> search(K searchKey) {
        if (this.isLeaf()) {
            if (this.index.containsKey(searchKey)) {
                return this.index.get(searchKey);
            }
            return new ArrayList<>();
        }
        Integer nextPageId = less;
        for(K key : this.children.keySet()){
            Integer comp = compare(key, searchKey);
            if(comp > 0) break;
            nextPageId = this.children.get(key);
        }

        //TODO find on disk the nextPageId
        BPlusTree<K> fromDisk = new BPlusTree(nextPageId.toString());
        return fromDisk.search(searchKey);
    }

    public ArrayList<RecordPointer> searchRange(K searchKey, boolean lessThan, boolean equalTo) {
        if (this.isLeaf()) {
            ArrayList<RecordPointer> pointers = new ArrayList<>();
            if (this.index.containsKey(searchKey) && equalTo) {
                pointers.addAll(this.index.get(searchKey));
            }
            for (K key: this.index.keySet()) {
                Integer comp = compare(key, searchKey);
                if (comp > 0 && !lessThan) {
                    pointers.addAll(this.index.get(key));
                }
                if (comp < 0 && lessThan) {
                    pointers.addAll(this.index.get(key));
                }
            }
            return pointers;
        }
        Integer nextPageId = less;
        for(K key : this.children.keySet()){
            Integer comp = compare(key, searchKey);
            if(comp > 0) break;
            nextPageId = this.children.get(key);
        }

        //TODO find on disk the nextPageId
        BPlusTree<K> fromDisk = new BPlusTree(nextPageId.toString());
        return fromDisk.searchRange(searchKey, lessThan, equalTo);
    }
}
