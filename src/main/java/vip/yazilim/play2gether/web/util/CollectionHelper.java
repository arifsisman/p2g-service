package vip.yazilim.play2gether.web.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public class CollectionHelper {

    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        List<T> result = new ArrayList<>();
        for (T item : iterable) {
            result.add(item);
        }
        return result;
    }
}
