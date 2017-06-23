package interaction.util;

import java.util.*;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class CollectionUtil {

    // TODO jz: avoid with bidi map?
    public static <K, V> Map<V, List<K>> invertListMap(Map<K, List<V>> key2ValuesMap) {
        One2ManyListMap<V, K> value2KeysMap = new One2ManyListMap<V, K>();
        for (K shardName : key2ValuesMap.keySet()) {
            List<V> nodes = key2ValuesMap.get(shardName);
            for (V node : nodes) {
                value2KeysMap.add(node, shardName);
            }
        }
        return value2KeysMap.asMap();
    }

    public static List<String> getListOfAdded(final Collection<String> oldList, final Collection<String> updatedList) {
        final List<String> addedEntriesList = new ArrayList<String>();
        extractAddedEntries(oldList, updatedList, addedEntriesList);
        return addedEntriesList;
    }

    public static Set<String> getSetOfAdded(final Collection<String> oldSet, final Collection<String> updatedSet) {
        final Set<String> addedEntriesSet = new HashSet<String>();
        extractAddedEntries(oldSet, updatedSet, addedEntriesSet);
        return addedEntriesSet;
    }

    public static List<String> getListOfRemoved(final Collection<String> oldList, final Collection<String> updatedList) {
        final List<String> removedEntriesList = new ArrayList<String>();
        extractRemovedEntries(oldList, updatedList, removedEntriesList);
        return removedEntriesList;
    }

    public static Set<String> getSetOfRemoved(final Collection<String> oldSet, final Collection<String> updatedSet) {
        final Set<String> removedEntriesSet = new HashSet<String>();
        extractRemovedEntries(oldSet, updatedSet, removedEntriesSet);
        return removedEntriesSet;
    }

    private final static void extractAddedEntries(final Collection<String> oldCollection,
                                                  final Collection<String> updatedCollection, final Collection<String> addedEntriesCollection) {
        for (final String entry : updatedCollection) {
            if (!oldCollection.contains(entry)) {
                addedEntriesCollection.add(entry);
            }
        }
    }

    private static void extractRemovedEntries(final Collection<String> oldCollection,
                                              final Collection<String> updatedCollection, final Collection<String> removedEntriesCollection) {
        for (final String string : oldCollection) {
            if (!updatedCollection.contains(string)) {
                removedEntriesCollection.add(string);
            }
        }
    }

}
