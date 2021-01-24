package com.mach.bff.customer.repository;

import com.mach.commercetools.repository.BaseRepository;
import io.sphere.sdk.customers.CustomerDraft;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.customers.commands.CustomerCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletionStage;

@Repository
@RequiredArgsConstructor
public class CustomerRepository {
    private final BaseRepository repository;

    public CompletionStage<CustomerSignInResult> createCustomer(CustomerDraft customerDraft) {
        return repository.executeWithThrowing(CustomerCreateCommand.of(customerDraft));
    }
}
