package com.jdlotest.backendTestAms.controller;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.jdlotest.backendTestAms.api.ProductApi;
import com.jdlotest.backendTestAms.model.ProductDetail;
import com.jdlotest.backendTestAms.service.ProductService;

@Controller
public class ProductController implements ProductApi {
	private ProductService service;
	
	public ProductController(ProductService productService) {
		this.service = productService;
	}

	@Override
	public ResponseEntity<Set<ProductDetail>> getProductSimilar(String productId) {
		Set<ProductDetail> response = service.getSimilarProducts(productId);
		return ResponseEntity.ok(response);
	}	

}
