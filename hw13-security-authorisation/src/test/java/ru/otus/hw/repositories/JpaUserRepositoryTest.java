package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий пользователей ")
@DataJpaTest
class JpaUserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("должен вернуть всех пользователей")
    @Test
    void findAll() {
        List<User> resultUsers = userRepository.findAll();

        assertThat(resultUsers)
                .extracting(User::getUsername)
                .containsExactly("user", "admin");
    }

    @DisplayName(" должен вернуть пользователя по username")
    @Test
    void findById() {
        Optional<User> resultUser = userRepository.findByUsername("user");

        assertTrue(resultUser.isPresent());
    }
}