package vip.yazilim.p2g.web.service;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface CrudService<T, ID extends Serializable> {

    /**
     * Create new resource.
     *
     * @param item Resource to create
     * @return new resource
     */
    Optional<T> create(T item) throws Exception;

    /**
     * Read existing resource.
     *
     * @param uuid Resource id
     * @return resource read
     */
    Optional<T> getByUuid(ID uuid) throws Exception;

    /**
     * Update existing resource.
     *
     * @param item Resource to update
     * @return resource updated
     */
    Optional<T> update(T item) throws Exception;

    /**
     * Delete existing resource.
     *
     * @param item Resource to delete
     */
    void delete(T item) throws Exception;

    /**
     * Delete existing resource.
     *
     * @param uuid Resource id
     */
    void delete(ID uuid) throws Exception;

}
