package com.test.jersey.dbPersistence;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="FamilyTable")
public class FamilyTable {
    private String name;
    private Integer age;
    private String relationship;

    @DynamoDBHashKey(attributeName = "Name")
    public String getName() {return name; }
    public void setName(String name) {this.name = name; }

    @DynamoDBAttribute(attributeName = "Age")
    public Integer getAge() { return age; }
    public void setAge(Integer age) {
        this.age = age;
    }

    @DynamoDBAttribute(attributeName = "Relationship")
    public String getRelationship() { return relationship;}
    public void setRelationship(String relationshipName) { this.relationship = relationshipName; }


}
