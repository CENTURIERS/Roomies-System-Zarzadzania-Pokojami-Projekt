package com.roomies.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao <T, ID> {
    void save(T entity);
    void update(T entity);
    void delete(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
}
