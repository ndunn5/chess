package dataaccess;

public interface GeneralDAO <T>{
    void create(T t) throws DataAccessException;
    T read(String id) throws DataAccessException;
    void update(T t) throws DataAccessException;
    void delete(String id) throws DataAccessException;
}
