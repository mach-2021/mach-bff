package com.mach.bff.customer.repository;

import com.mach.commercetools.repository.BaseRepository;
import com.mach.core.model.customer.request.CustomerLoginRequest;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerDraft;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.customers.commands.CustomerCreateCommand;
import io.sphere.sdk.customers.commands.CustomerSignInCommand;
import io.sphere.sdk.customers.queries.CustomerQuery;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.types.FieldDefinition;
import io.sphere.sdk.types.ReferenceFieldType;
import io.sphere.sdk.types.SetFieldType;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.TypeDraft;
import io.sphere.sdk.types.TypeDraftBuilder;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletionStage;

@Repository
@RequiredArgsConstructor
public class CustomerRepository {
    private final BaseRepository repository;

    public CompletionStage<CustomerSignInResult> createCustomer(CustomerDraft customerDraft) {
        return repository.executeWithThrowing(CustomerCreateCommand.of(customerDraft));
    }

    public CompletionStage<CustomerSignInResult> loginCustomer(CustomerLoginRequest customerRequest) {
        return repository.executeWithThrowing(CustomerSignInCommand.of(customerRequest.getEmail(), customerRequest.getPassword()));
    }

    public void createCustomType() {
        final FieldDefinition fieldDefinition = FieldDefinition.of(SetFieldType.of(ReferenceFieldType.of("category")), "categories", LocalizedString.of(Locale.US, "Choosen categories"), false);
        final TypeDraft typeDraft = TypeDraftBuilder.of("customerType", LocalizedString.of(Locale.US, "Customer type"), Set.of("customer"))
                .fieldDefinitions(List.of(fieldDefinition)).build();
        final Type join = repository.executeWithThrowing(TypeCreateCommand.of(typeDraft))
                .toCompletableFuture().join();
        System.out.println("Create type");
    }

    public CompletionStage<PagedQueryResult<Customer>> findCustomersByEmail(String email) {
        return repository.executeWithThrowing(CustomerQuery.of()
                .withPredicates(customer -> customer.email().is(email))
                .withLimit(1));
    }
}
