package com.mach.bff.customer.mapper;

import com.mach.core.model.customer.request.CustomerCreateRequest;
import com.mach.core.model.customer.response.CustomerLoginResponse;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerDraft;
import io.sphere.sdk.customers.CustomerDraftBuilder;
import io.sphere.sdk.types.CustomFieldsDraftBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerDraft mapToCustomerDraft(CustomerCreateRequest customerRequest) {
        final String email = RandomStringUtils.randomAlphabetic(12);
        return CustomerDraftBuilder.of(customerRequest.getEmail(), customerRequest.getPassword())
                .firstName(customerRequest.getName())
                .custom(CustomFieldsDraftBuilder.ofTypeKey("customerType")
                        .build())
                .build();
    }

    public CustomerLoginResponse mapCustomerToResponse(Customer customer) {
        return CustomerLoginResponse.builder()
                .id(customer.getId())
                .name(customer.getFirstName())
                .build();
    }
}
