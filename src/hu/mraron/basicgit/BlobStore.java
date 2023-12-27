package hu.mraron.basicgit;

import java.util.HashMap;

public class BlobStore {
    private final HashMap<String, Blob> store;
    public BlobStore() {
        this.store = new HashMap<>();
    }

    public Blob getBlob(String data) {
        if(!store.containsKey(SHA1.hash(data))) {
            store.put(SHA1.hash(data), new Blob(data));
        }

        return store.get(SHA1.hash(data));
    }
}
