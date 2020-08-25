package pl.cekus.antologicproject.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import pl.cekus.antologicproject.model.Project;

import java.util.*;

public class ProjectRepositoryStub implements ProjectRepository {

    private List<Project> preparedProjectsList = new ArrayList<>();

    @Override
    public Optional<Project> findByProjectName(String projectName) {
        return preparedProjectsList.stream()
                .filter(project -> project.getProjectName().equals(projectName))
                .findFirst();
    }

    @Override
    public Optional<Project> findByUuid(UUID uuid) {
        return preparedProjectsList.stream()
                .filter(project -> project.getUuid().equals(uuid))
                .findFirst();
    }

    @Override
    public boolean existsByProjectName(String projectName) {
        return preparedProjectsList.stream()
                .anyMatch(project -> project.getProjectName().equals(projectName));
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        preparedProjectsList.stream()
                .filter(project -> project.getUuid() == uuid)
                .findFirst()
                .ifPresent(project -> preparedProjectsList.remove(project));
    }

    @Override
    public List<Project> findAll() {
        return preparedProjectsList;
    }

    @Override
    public List<Project> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Project> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Project> findAllById(Iterable<Long> longs) {
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
    public void delete(Project entity) {
        preparedProjectsList.remove(entity);
    }

    @Override
    public void deleteAll(Iterable<? extends Project> entities) {
    }

    @Override
    public void deleteAll() {
        preparedProjectsList.clear();
    }

    @Override
    public <S extends Project> S save(S entity) {
        preparedProjectsList.add(entity);
        return entity;
    }

    @Override
    public <S extends Project> List<S> saveAll(Iterable<S> entities) {
        preparedProjectsList.addAll((Collection<? extends Project>) entities);
        return null;
    }

    @Override
    public Optional<Project> findById(Long aLong) {
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
    public <S extends Project> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Project> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Project getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends Project> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Project> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Project> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Project> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Project> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Project> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public Optional<Project> findOne(Specification<Project> spec) {
        return Optional.empty();
    }

    @Override
    public List<Project> findAll(Specification<Project> spec) {
        return null;
    }

    @Override
    public Page<Project> findAll(Specification<Project> spec, Pageable pageable) {
        return null;
    }

    @Override
    public List<Project> findAll(Specification<Project> spec, Sort sort) {
        return null;
    }

    @Override
    public long count(Specification<Project> spec) {
        return preparedProjectsList.size();
    }
}
