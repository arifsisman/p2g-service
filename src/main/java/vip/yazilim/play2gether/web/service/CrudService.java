package vip.yazilim.play2gether.web.service;

import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface CrudService<T, ID> {

    // save
    public Optional<T> save(T item);

    // read
    public Optional<T> getByUuid(ID uuid);

}
