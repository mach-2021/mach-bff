package com.mach.bff.customer.service;

import com.mach.bff.customer.mapper.CustomerMapper;
import com.mach.bff.customer.repository.CustomerRepository;
import com.mach.bff.security.model.UserPrincipal;
import com.mach.bff.security.util.SecurityUtils;
import com.mach.bff.wishlist.service.WishListService;
import com.mach.core.exception.BadRequestException;
import com.mach.core.exception.EmailExistException;
import com.mach.core.exception.UnprocessableEntityException;
import com.mach.core.model.customer.request.CustomerCreateRequest;
import com.mach.core.model.customer.request.CustomerLoginRequest;
import com.mach.core.model.customer.response.CustomerLoginResponse;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerDraft;
import io.sphere.sdk.customers.CustomerSignInResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mach.bff.security.model.Authorities.CUSTOMER;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final WishListService wishListService;

    public CustomerLoginResponse createCustomer(CustomerCreateRequest customerRequest) {
        if (StringUtils.isAnyBlank(customerRequest.getEmail(), customerRequest.getPassword())) {
            throw new BadRequestException("Name or password empty", "registration_empty_fields");
        }
        isEmailAvailable(customerRequest.getEmail());
        log.info("Start create customer");
        final CustomerDraft customerDraft = customerMapper.mapToCustomerDraft(customerRequest);
        CustomerSignInResult signInResult = customerRepository.createCustomer(customerDraft).toCompletableFuture().join();
        if (signInResult.getCustomer() == null) {
            log.error("Can not create customer. Sign in result is empty");
            throw new UnprocessableEntityException("Can not create customer", "registration_failed");
        }
        wishListService.createCustomerWishList(signInResult.getCustomer().getId());
        return customerMapper.mapCustomerToResponse(signInResult.getCustomer());
    }

    public void isEmailAvailable(final String email) {
        log.debug("Start validating customer by email");
        boolean isAvailable = customerRepository.findCustomersByEmail(email)
                .toCompletableFuture().join()
                .getResults().isEmpty();
        if (!isAvailable) {
            throw new EmailExistException("Email is not available", "email_not_available");
        }
    }

    public CustomerLoginResponse loginCustomer(CustomerLoginRequest customerRequest) {
        if (StringUtils.isAnyBlank(customerRequest.getEmail(), customerRequest.getPassword())) {
            throw new BadRequestException("Name or password empty", "registration_empty_fields");
        }
        log.info("Start create customer");
        CustomerSignInResult signInResult = customerRepository.loginCustomer(customerRequest).toCompletableFuture().join();
        final Customer customer = signInResult.getCustomer();
        if (customer == null) {
            log.error("Can not login customer. Invalid credentials");
            throw new UnprocessableEntityException("Can not login customer", "login_invalid_credentials");
        }
        final UserPrincipal userPrincipal = UserPrincipal.builder()
                .name(customer.getFirstName())
                .email(customer.getEmail())
                .internalId(customer.getId())
                .authorities(List.of(CUSTOMER.authority()))
                .build();
        SecurityUtils.setUserPrincipal(userPrincipal);
        return customerMapper.mapCustomerToResponse(customer);
    }
}
