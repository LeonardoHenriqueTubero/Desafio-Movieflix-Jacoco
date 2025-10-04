package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.nimbusds.jose.RemoteKeySourceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

    @Mock
    private UserService userService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ScoreRepository repository;

    private MovieEntity movie;
    private UserEntity user;
    private ScoreEntity score;
    private ScoreDTO scoreDTO;
    private MovieDTO movieDTO;
    private Long existingMovieId, nonExistingMovieId;

    @BeforeEach
    void setUp() throws Exception {
        movie = MovieFactory.createMovieEntity();
        user = UserFactory.createUserEntity();
        score = ScoreFactory.createScoreEntity();
        movie.getScores().add(score);
        scoreDTO = ScoreFactory.createScoreDTO();
        movieDTO = MovieFactory.createMovieDTO();
        existingMovieId = 1L;
        nonExistingMovieId = 2L;

        Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
        Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
        Mockito.when(movieRepository.save(any())).thenReturn(movie);

        Mockito.when(repository.saveAndFlush(any())).thenReturn(score);
    }

    @Test
	public void saveScoreShouldReturnMovieDTO() {
        Mockito.when(userService.authenticated()).thenReturn(user);

        MovieDTO result = service.saveScore(scoreDTO);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(movieDTO.getId(), result.getId());
        Assertions.assertEquals(movieDTO.getTitle(), result.getTitle());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
        Mockito.when(userService.authenticated()).thenReturn(user);
        scoreDTO = new ScoreDTO(nonExistingMovieId, 0.0);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            MovieDTO result = service.saveScore(scoreDTO);
        });
	}
}
