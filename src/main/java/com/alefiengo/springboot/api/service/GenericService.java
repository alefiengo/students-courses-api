package com.alefiengo.springboot.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GenericService<E> {

    Optional<E> findById(Long id);

    E save(E entity);

    List<E> findAll();

    Page<E> findAll(Pageable pageable);

    void deleteById(Long id);
}
