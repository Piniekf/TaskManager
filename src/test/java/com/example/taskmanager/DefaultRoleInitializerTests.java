package com.example.taskmanager;
import com.example.taskmanager.DefaultRoleInitializer;
import com.example.taskmanager.entity.Role;
import com.example.taskmanager.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class DefaultRoleInitializerTests {

    private RoleRepositoryStub roleRepository;
    private DefaultRoleInitializer defaultRoleInitializer;


    @BeforeEach
    void setUp() {
        // Ustawienie testu - tworzymy stuba repozytorium i inicjalizatora ról.
        roleRepository = new RoleRepositoryStub();
        defaultRoleInitializer = new DefaultRoleInitializer(roleRepository);
    }

    @Test
    void shouldInitializeRolesIfNotExist() throws Exception {
        // Act - wykonujemy metodę, którą chcemy przetestować.
        defaultRoleInitializer.run();

        // Assert - sprawdzamy, czy wyniki są zgodne z oczekiwaniami.
        assertEquals(2, roleRepository.roles.size());
        assertTrue(roleRepository.roles.stream().anyMatch(role -> "ROLE_USER".equals(role.getName())));
        assertTrue(roleRepository.roles.stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName())));
    }

    @Test
    void shouldNotInitializeRolesIfTheyExist() throws Exception {
        // Arrange - przygotowujemy warunki początkowe dla testu.
        roleRepository.roles.add(new Role("ROLE_USER"));
        roleRepository.roles.add(new Role("ROLE_ADMIN"));

        // Act - wykonujemy metodę, którą chcemy przetestować.
        defaultRoleInitializer.run();

        // Assert - sprawdzamy, czy wyniki są zgodne z oczekiwaniami.
        assertEquals(2, roleRepository.roles.size()); // Nie powinny być dodane nowe role.
    }

    // Stub dla RoleRepository - zastępuje prawdziwe repozytorium w testach.
    private static class RoleRepositoryStub implements RoleRepository {
        List<Role> roles = new ArrayList<>();

        @Override
        public Role findByName(String name) {
            // Wyszukuje rolę po nazwie z listy ról.
            return roles.stream().filter(role -> name.equals(role.getName())).findFirst().orElse(null);
        }

        @Override
        public Role save(Role role) {
            // Zapisuje rolę do listy. W prawdziwym repozytorium metoda ta zajmowałaby się
            // sprawdzaniem, czy rola już istnieje i aktualizacją rekordu.
            roles.add(role);
            return role;
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends Role> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends Role> List<S> saveAllAndFlush(Iterable<S> entities) {
            return null;
        }

        @Override
        public void deleteAllInBatch(Iterable<Role> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<Long> longs) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public Role getOne(Long aLong) {
            return null;
        }

        @Override
        public Role getById(Long aLong) {
            return null;
        }

        @Override
        public Role getReferenceById(Long aLong) {
            return null;
        }

        @Override
        public <S extends Role> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Role> List<S> findAll(Example<S> example) {
            return null;
        }

        @Override
        public <S extends Role> List<S> findAll(Example<S> example, Sort sort) {
            return null;
        }

        @Override
        public <S extends Role> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Role> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Role> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends Role, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }

        @Override
        public <S extends Role> List<S> saveAll(Iterable<S> entities) {
            return null;
        }

        @Override
        public Optional<Role> findById(Long aLong) {
            return Optional.empty();
        }

        @Override
        public boolean existsById(Long aLong) {
            return false;
        }

        @Override
        public List<Role> findAll() {
            return null;
        }

        @Override
        public List<Role> findAllById(Iterable<Long> longs) {
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
        public void delete(Role entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends Long> longs) {

        }

        @Override
        public void deleteAll(Iterable<? extends Role> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public List<Role> findAll(Sort sort) {
            return null;
        }

        @Override
        public Page<Role> findAll(Pageable pageable) {
            return null;
        }
    }
}
