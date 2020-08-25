package pl.cekus.antologicproject.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.cekus.antologicproject.model.WorkingTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorkingTimeRepositoryStub implements WorkingTimeRepository {

    private List<WorkingTime> workingTimes = new ArrayList<>();

    @Override
    public Optional<WorkingTime> findByUuid(UUID uuid) {
        return workingTimes.stream()
                .filter(workingTime -> workingTime.getUuid().equals(uuid))
                .findFirst();
    }

    @Override
    public List<WorkingTime> findAll() {
        return workingTimes;
    }

    @Override
    public List<WorkingTime> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<WorkingTime> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<WorkingTime> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(WorkingTime entity) {
        workingTimes.remove(entity);
    }

    @Override
    public void deleteAll(Iterable<? extends WorkingTime> entities) {

    }

    @Override
    public void deleteAll() {
        workingTimes.clear();
    }

    @Override
    public <S extends WorkingTime> S save(S entity) {
        workingTimes.add(entity);
        return entity;
    }

    @Override
    public <S extends WorkingTime> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<WorkingTime> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends WorkingTime> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<WorkingTime> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public WorkingTime getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends WorkingTime> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends WorkingTime> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends WorkingTime> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends WorkingTime> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends WorkingTime> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends WorkingTime> boolean exists(Example<S> example) {
        return false;
    }
}
