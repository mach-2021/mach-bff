package com.mach.bff.customer.controller;

import com.mach.bff.customer.service.CustomerService;
import com.mach.core.model.customer.request.CustomerCreateRequest;
import com.mach.core.model.customer.request.CustomerLoginRequest;
import com.mach.core.model.customer.response.CustomerLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerLoginResponse> createCustomer(@RequestBody CustomerCreateRequest customerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(customerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerLoginResponse> loginCustomer(@RequestBody CustomerLoginRequest customerRequest) {
        return ResponseEntity.ok(customerService.loginCustomer(customerRequest));
    }
}
