package vip.yazilim.p2g.web.util;

import java.util.ArrayList;
import java.util.List;

public class CollectionHelper {

    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        List<T> result = new ArrayList<>();
        for (T item : iterable) {
            result.add(item);
        }
        return result;
    }
}
