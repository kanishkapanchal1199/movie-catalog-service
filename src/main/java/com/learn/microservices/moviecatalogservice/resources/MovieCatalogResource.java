package com.learn.microservices.moviecatalogservice.resources;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.learn.microservices.moviecatalogservice.models.CatalogItem;
import com.learn.microservices.moviecatalogservice.models.Movie;
import com.learn.microservices.moviecatalogservice.models.Rating;
import com.learn.microservices.moviecatalogservice.models.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@RequestMapping("/{userId}")
	@HystrixCommand(fallbackMethod = "getFallbackCatalog")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId)
	{
		//RestTemplate restTemplate=new RestTemplate();
		
		//WebClient.Builder builder=WebClient.builder();//creating a webclient
		
		/*
		 * List<Rating> ratings=Arrays.asList( new Rating("1234",3) ,new
		 * Rating("4567",5) );
		 */
		
		UserRating ratings=restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/"+userId, UserRating.class);
		
		
		
		
		
		
		return ratings.getUserRating().stream().map
				
				(rating->{
				
			Movie movie=restTemplate.getForObject( "http://movie-info-service/movies/" +rating.getMovieId(), Movie.class);
					
					/*
					 * Movie movie= webClientBuilder.build() .get()
					 * .uri("http://localhost:8082/movies/" +rating.getMovieId()) .retrieve()
					 * .bodyToMono(Movie.class) .block();
					 */
				return new CatalogItem(movie.getName(), "Test", rating.getRating());
				}).collect(Collectors.toList());
		
	}
	
	public List<CatalogItem> getFallbackCatalog(@PathVariable ("userId") String userId)
	{
		return Arrays.asList(new CatalogItem("No Movie","", 0));
	}

}
