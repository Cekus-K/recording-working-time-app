package pl.cekus.antologicproject.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import pl.cekus.antologicproject.model.User;

import java.util.*;

public class UserRepositoryStub implements UserRepository {
    @Override
    public Optional<User> findByLogin(String login) {
        return preparedUsersList.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        return preparedUsersList.stream()
                .filter(user -> user.getUuid().equals(uuid))
                .findFirst();
    }

    @Override
    public boolean existsByLogin(String login) {
        return preparedUsersList.stream()
                .anyMatch(user -> user.getLogin().equals(login));
    }

    @Override
    public boolean existsByUuid(UUID uuid) {
        return preparedUsersList.stream()
                .anyMatch(user -> user.getUuid().equals(uuid));
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        preparedUsersList.stream()
                .filter(user -> user.getUuid().equals(uuid))
                .findFirst()
                .ifPresent(user -> preparedUsersList.remove(user));
    }

    @Override
    public List<User> findAll() {
        return preparedUsersList;
    }

    @Override
    public List<User> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return new PageImpl<>(preparedUsersList, pageable, preparedUsersList.size());
    }

    @Override
    public List<User> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return preparedUsersList.size();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(User entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
    }

    @Override
    public void deleteAll() {
        preparedUsersList.clear();
    }

    @Override
    public <S extends User> S save(S entity) {
        preparedUsersList.add(entity);
        return entity;
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        preparedUsersList.addAll((Collection<? extends User>) entities);
        return null;
    }

    @Override
    public Optional<User> findById(Long aLong) {
        return preparedUsersList.stream()
                .filter(user -> user.getId().equals(aLong))
                .findFirst();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<User> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public User getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public Optional<User> findOne(Specification<User> spec) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll(Specification<User> spec) {
        return null;
    }

    @Override
    public Page<User> findAll(Specification<User> spec, Pageable pageable) {
        return null;
    }

    @Override
    public List<User> findAll(Specification<User> spec, Sort sort) {
        return null;
    }

    @Override
    public long count(Specification<User> spec) {
        return 0;
    }

    private List<User> preparedUsersList = new ArrayList<>();
}
