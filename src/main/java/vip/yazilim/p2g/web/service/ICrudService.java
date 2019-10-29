package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.exception.DatabaseException;

import java.io.Serializable;
import java.util.List;


/**
 * Business method definitions for CRUD operations for Entity
 *
 * @author Emre Sen, 27.06.2019
 * @contact maemresen07@gmail.com
 */
public interface ICrudService<E extends Serializable, ID> {

	// (C) create Operations
	/**
	 * Insert Entity to the Data source.
	 *
	 * @param entity data to insert
	 * @return inserted entity
	 */
	 E create(E entity) throws DatabaseException;

	 // (R) read Operations

	 /**
	 * Get all entities on the table
	 *
	 * @return list of entities
	 */
	List<E> getAll() throws DatabaseException;

	/**
	 * Get an entity from table by id.
	 *
	 * @param id id field of entity
	 * @return entity with id
	 */
	E getById(ID id) throws DatabaseException;

	// (U) update Operations

	/**
	 * Update table with given model
	 *
	 * @param newEntity new updated values to save into data source
	 * @return saved entity to database
	 */
	E update(E newEntity) throws DatabaseException;

	// (D) delete Operations
	boolean delete(E entity) throws DatabaseException;

	boolean deleteAll() throws DatabaseException;
}
