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
import io.sphere.sdk.types.BooleanFieldType;
import io.sphere.sdk.types.FieldDefinition;
import io.sphere.sdk.types.ReferenceFieldType;
import io.sphere.sdk.types.SetFieldType;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.TypeDraft;
import io.sphere.sdk.types.TypeDraftBuilder;
import io.sphere.sdk.types.commands.TypeUpdateCommand;
import io.sphere.sdk.types.commands.updateactions.AddFieldDefinition;
import io.sphere.sdk.types.queries.TypeQuery;
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
        final Type join = repository.executeWithThrowing(TypeQuery.of().plusPredicates(t->t.key().is("customerType")))
                .toCompletableFuture().join().getResults().get(0);
        repository.executeWithThrowing(TypeUpdateCommand.of(join,
        AddFieldDefinition.of(FieldDefinition.of(BooleanFieldType.of(), "isFirstLogin", LocalizedString.of(Locale.US,"Is first login"), true))))
                .toCompletableFuture().join();
        System.out.println("Create type");
    }

    public CompletionStage<PagedQueryResult<Customer>> findCustomersByEmail(String email) {
        return repository.executeWithThrowing(CustomerQuery.of()
                .withPredicates(customer -> customer.email().is(email))
                .withLimit(1));
    }
}
