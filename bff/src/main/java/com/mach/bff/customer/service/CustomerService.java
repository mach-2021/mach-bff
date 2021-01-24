package com.mach.bff.customer.service;

import com.mach.bff.customer.mapper.CustomerMapper;
import com.mach.bff.customer.repository.CustomerRepository;
import com.mach.core.exception.BadRequestException;
import com.mach.core.exception.UnprocessableEntityException;
import com.mach.core.model.customer.request.CreateCustomerRequest;
import com.mach.core.model.customer.response.CustomerLoginResponse;
import io.sphere.sdk.customers.CustomerDraft;
import io.sphere.sdk.customers.CustomerSignInResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerLoginResponse createCustomer(CreateCustomerRequest customerRequest) {
        if (StringUtils.isAnyBlank(customerRequest.getName(), customerRequest.getPassword())) {
            throw new BadRequestException("Name or password empty", "registration_empty_fields");
        }
        log.info("Start create customer");
        final CustomerDraft customerDraft = customerMapper.mapToCustomerDraft(customerRequest);
        CustomerSignInResult signInResult = customerRepository.createCustomer(customerDraft).toCompletableFuture().join();
        if (signInResult.getCustomer() == null) {
            log.error("Can not create customer. Sign in result is empty");
            throw new UnprocessableEntityException("Can not create customer", "registration_failed");
        }
        return customerMapper.mapCustomerToResponse(signInResult.getCustomer());
    }
}
