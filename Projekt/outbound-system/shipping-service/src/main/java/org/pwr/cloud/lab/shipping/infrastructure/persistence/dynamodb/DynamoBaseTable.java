package org.pwr.cloud.lab.shipping.infrastructure.persistence.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class DynamoBaseTable<T> {
    public static final String PK_NAME = "PK";

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute(PK_NAME)})
    protected String pk;

    @Getter(onMethod_ = @DynamoDbAttribute("lastModifiedTimestamp"))
    protected Long lastModifiedTimestamp;

    public abstract T toModel();
}
