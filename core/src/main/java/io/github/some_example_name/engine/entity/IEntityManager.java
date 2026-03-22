package io.github.some_example_name.engine.entity;

import com.badlogic.gdx.math.Rectangle;
import java.util.Collection;
import java.util.UUID;

/**
 * Interface for managing entities in the Abstract Engine.
 * 
 * IMPORTANT: This interface is named "IEntityManager" to avoid
 * naming conflict with the concrete implementation class.
 * 
 * Responsibilities:
 * - Create and register new entities
 * - Remove entities from the system
 * - Retrieve entities by unique ID
 * - Update all managed entities
 * - Provide access to entity collection (without exposing internal structure)
 */

public interface IEntityManager {
  
  /**
   * Creates and registers a new entity.
   * 
   * @param entity The entity to register
   * @return The UUID of the created entity
   */
  UUID create(Entity entity);

  /**
   * Removes an entity by its ID.
   * 
   * @param id The UUID of the entity to remove
   * @return The removed entity, or null if not found
   */
  Entity remove(UUID id);

  /**
   * Retrieves an entity by its ID.
   * 
   * @param id The UUID of the entity to retrieve
   * @return The entity with the given ID, or null if not found
   */
  Entity get(UUID id);

  /**
   * Updates all managed entities.
   * 
   * @param deltaTime Time elapsed since last update (in seconds)
   */
  void update(float deltaTime);
  
    /**
     * Returns an unmodifiable view of all entities
     * 
     * IMPORTANT: This preserves encapsulation by returning
     * an unmodifiable collection. Callers cannot modify
     * the internal entity list.
     * 
     * @return Unmodifiable collection of all entities
   */
  Collection<Entity> getAll();

  Collection<Entity> getEntitiesInBounds(Rectangle area);

  Collection<Entity> getNearbyEntities(float centerX, float centerY, float radius);

  /**
   * Returns the number of entities currently managed
   * 
   * @return Entity count
   */
  int size();

  /**
   * Checks if an entity with given ID exists.
   * 
   * @param id The UUID to check
   * @return true if entity exists, false otherwise
   */
  boolean contains(UUID id);

  /**
   * Removes all entities from the manager.
   */
  void clear();
}
