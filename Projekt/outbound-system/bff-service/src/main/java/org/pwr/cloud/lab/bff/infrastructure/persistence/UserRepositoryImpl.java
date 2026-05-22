package org.pwr.cloud.lab.bff.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.pwr.cloud.lab.bff.domain.model.User;
import org.pwr.cloud.lab.bff.domain.repository.UserRepository;
import org.pwr.cloud.lab.bff.infrastructure.persistence.entity.UserEntity;
import org.pwr.cloud.lab.bff.infrastructure.persistence.jpa.UserJpaRepository;
import org.pwr.cloud.lab.common.domain.model.id.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return toDomain(userJpaRepository.save(toEntity(user)));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(UserId.of(entity.getId()))
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.id() != null ? user.id().value() : null)
                .username(user.username())
                .email(user.email())
                .password(user.password())
                .role(user.role())
                .build();
    }
}
