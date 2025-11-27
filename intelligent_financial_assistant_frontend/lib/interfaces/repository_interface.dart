abstract class RepositoryInterface<T> {
  Future<T> getById(String id);
  Future<List<T>> getAll();
  Future<void> save(T item);
  Future<void> delete(String id);
}