package net.ripe.commons.ip;

public class SortedResourceSet<C extends SingleInternetResource<C, R>, R extends Range<C, R>> extends SortedRangeSet<C, R> {

    public void add(C resource) {
        add(resource.asRange());
    }

    public boolean remove(C resource) {
        return remove(resource.asRange());
    }

    public boolean contains(C resource) {
        return contains(resource.asRange());
    }
}
