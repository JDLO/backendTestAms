package com.jdlotest.backendTestAms.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.jdlotest.backendTestAms.exception.ResourceNotFoundException;
import com.jdlotest.backendTestAms.model.ProductDetail;

@Service
public class ProductService {
	private static String urlMock = "http://localhost:3001";
	
	private final RestClient restClient;
	
	private final ExecutorService executor;
	
	public ProductService(RestClient.Builder builder) {
		this.restClient = builder.baseUrl(urlMock).build();
		this.executor = Executors.newVirtualThreadPerTaskExecutor();
	}
	
	public Set<ProductDetail> getSimilarProducts(String productId){
		try {
			List<String> similarIds = restClient.get()
					.uri("/product/{productId}/similarids", productId)
					.retrieve()
					.body(new ParameterizedTypeReference<List<String>>() {});
			
			if (similarIds == null||similarIds.isEmpty()) return Set.of();
						
			List<CompletableFuture<ProductDetail>> futures = similarIds.stream()
					.map(id -> CompletableFuture.supplyAsync(()-> fetchProductDetail(id), executor)
							.orTimeout(3, TimeUnit.SECONDS)
							.exceptionally(ex -> null))
					.toList();
			
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
			
			return futures.stream()
					.map(CompletableFuture::join)
					.filter(Objects::nonNull)				
					.collect(Collectors.toUnmodifiableSet());
			
		} catch (Exception e) {
			throw new ResourceNotFoundException("Product with ID " + productId + " not found");
		}
	}
	
	private ProductDetail fetchProductDetail(String id) {
		try {
			return restClient.get()
					.uri("/product/{id}", id)
					.retrieve()
					.body(ProductDetail.class);

		} catch(Exception e) {
			return null;
		}
	}
}
