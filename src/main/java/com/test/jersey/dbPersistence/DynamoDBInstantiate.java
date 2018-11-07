package com.test.jersey.dbPersistence;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class DynamoDBInstantiate {
    static boolean initialized = false;
    static AmazonDynamoDB client = null;
    static DynamoDB dynamoDB = null;
    static DynamoDBMapper mapper = null;
    static final String tableName = "DynamoDbTest";
    static String forumTableName = "Forum";
    static String threadTableName = "Thread";
    static String replyTableName = "Reply";

    PutItemRequest request;



    static String familyTableName = "FamilyTable";



    public void init() throws Exception {
        if (initialized) {
            System.out.println("Already initialized");
            throw new Exception("Already initialized");
        }

        if (dynamoDB != null) {
            System.out.println("DynamoDb not null, already instantiated");
            throw new Exception("DynamoDb not null, already instantiated");
        }


        client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
                .build();
        dynamoDB = new DynamoDB(client);
        mapper = new DynamoDBMapper(client);
        createTable(familyTableName, 10L, 5L,
                "Name", "S", null, null);


    }

    private static void createTable(String tableName, long readCapacityUnits, long writeCapacityUnits,
                                    String partitionKeyName, String partitionKeyType, String sortKeyName, String sortKeyType) {

        try {
            Table tb = dynamoDB.getTable(tableName);
            if (tb != null) {
                System.out.println("Table already exists");
                return;
            }

            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(new KeySchemaElement().withAttributeName(partitionKeyName).withKeyType(KeyType.HASH)); // Partition
            // key

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions
                    .add(new AttributeDefinition().withAttributeName(partitionKeyName).withAttributeType(partitionKeyType));

            if (sortKeyName != null) {
                keySchema.add(new KeySchemaElement().withAttributeName(sortKeyName).withKeyType(KeyType.RANGE)); // Sort
                // key
                attributeDefinitions
                        .add(new AttributeDefinition().withAttributeName(sortKeyName).withAttributeType(sortKeyType));
            }

            CreateTableRequest request = new CreateTableRequest().withTableName(tableName).withKeySchema(keySchema)
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits));


            request.setAttributeDefinitions(attributeDefinitions);

            System.out.println("Issuing CreateTable request for " + tableName);
            Table table = dynamoDB.createTable(request);
            System.out.println("Waiting for " + tableName + " to be created...this may take a while...");
            table.waitForActive();

        }
        catch (Exception e) {
            System.err.println("CreateTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

    public void putFamilyTbRow(FamilyTable ftb) throws Exception {
        try {

            FamilyTable item = mapper.load(FamilyTable.class, ftb.getName());
            if (item == null) {
                mapper.save(ftb);
            } else {
                System.out.println("Already exists");
                throw new Exception("Item already exists");
            }
        } catch (Exception ex) {
            System.out.println("This is the exception " + ex);
            throw ex;
        }
    }

    public FamilyTable getFamilyTbRow(String key) {
        FamilyTable tb = null;
        try {
             tb = mapper.load(FamilyTable.class, key);
        } catch (Exception ex) {
            System.out.println("This is the exception " + ex);
        }
        return tb;
    }


    public void updateFamilyTbRow(FamilyTable ftb) throws Exception {
        try {

            FamilyTable item = mapper.load(FamilyTable.class, ftb.getName());
            if (item == null) {
                System.out.println("Doesnt exists");
                throw new Exception("Item doesnt exists");

            } else {
                mapper.save(ftb);
            }
        } catch (Exception ex) {
            System.out.println("This is the exception " + ex);
            throw ex;
        }
    }

    public FamilyTable deleteFamilyTbRow(FamilyTable row) {
        FamilyTable tb = null;
        try {
            mapper.delete(row);
        } catch (Exception ex) {
            System.out.println("This is the exception " + ex);
        }
        return tb;
    }


}
