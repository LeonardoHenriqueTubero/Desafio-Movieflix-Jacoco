package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private CustomUserUtil util;

    private UserEntity user;
    private List<UserDetailsProjection> list;
    private String existingUsername, nonExistingUsername;

    @BeforeEach
    void setUp() throws Exception{
        user = UserFactory.createUserEntity();
        existingUsername = "maria@gmail.com";
        list = UserDetailsFactory.createCustomClientUser(existingUsername);
        nonExistingUsername = "teste@gmail.com";

        Mockito.when(repository.findByUsername(existingUsername)).thenReturn(Optional.of(user));
        Mockito.when(repository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());
        Mockito.when(repository.searchUserAndRolesByUsername(existingUsername)).thenReturn(list);
        Mockito.when(repository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(List.of());
    }

    @Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
        Mockito.when(util.getLoggedUsername()).thenReturn(existingUsername);

        UserEntity result = service.authenticated();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingUsername, result.getUsername());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        Mockito.doThrow(ClassCastException.class).when(util).getLoggedUsername();
        Assertions.assertThrows(UsernameNotFoundException.class, service::authenticated);
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        UserDetails result = service.loadUserByUsername(existingUsername);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingUsername, result.getUsername());
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(nonExistingUsername);
        });
	}
}
