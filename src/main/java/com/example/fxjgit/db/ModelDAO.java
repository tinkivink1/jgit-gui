package com.example.fxjgit.db;

import com.example.fxjgit.db.entities.*;

import java.util.List;

public interface ModelDAO <T>{
    List<T> getAll();
    T getById(int id);
    boolean add(T model);
    boolean update(T model);
    boolean delete(int id);
}
