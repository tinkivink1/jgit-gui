package com.example.fxjgit.db;

import com.example.fxjgit.db.entities.*;

import java.util.List;

public interface ModelDAO <T>{
    List<T> getAll();
    T getById(int id);
    void add(T model);
    void update(T model);
    void delete(int id);

}
