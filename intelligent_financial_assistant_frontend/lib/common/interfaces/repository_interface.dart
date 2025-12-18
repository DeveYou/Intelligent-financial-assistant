/// An abstract interface defining standard CRUD operations for a repository.
///
/// [T] is the type of the entity managed by the repository.
abstract class RepositoryInterface<T> {
  /// Retrieves an entity by its unique [id].
  Future<T> getById(String id);

  /// Retrieves all entities.
  Future<List<T>> getAll();

  /// Saves or updates the [item].
  Future<void> save(T item);

  /// Deletes an entity by its unique [id].
  Future<void> delete(String id);
}