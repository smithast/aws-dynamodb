package com.test.jersey;

import com.test.jersey.dbPersistence.DynamoDBInstantiate;
import com.test.jersey.dbPersistence.FamilyTable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
@Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
public class MyResource {

    static final DynamoDBInstantiate db = Main.getDb();
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("person/{param}")
    public String getIt(@PathParam("param") String name) {
        FamilyTable fTb = db.getFamilyTbRow(name);
        return (fTb != null)? name + " is " + fTb.getRelationship() + " and is" + fTb.getAge() + "old" :
                "No person by this name " + name;

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public FamilyTable putIt(FamilyTable ftb) throws Exception {

        db.putFamilyTbRow(ftb);
        return ftb;
    }

    @PUT
    @Path("{param}")
    public FamilyTable updateIt(@PathParam("param")  String name, FamilyTable familyTable) throws Exception {
        if (name == null || familyTable.getName() == null) {
            throw new BadRequestException("name is null");
        }

        FamilyTable currFtb = db.getFamilyTbRow(name);
        if (currFtb == null) {
            throw new NotFoundException("Row with name not found: " + name);
        }

        if (familyTable.getAge() == null) {
            familyTable.setAge(currFtb.getAge());
        }
        if(familyTable.getRelationship() == null) {
            familyTable.setRelationship(currFtb.getRelationship());
        }

        db.updateFamilyTbRow(familyTable);
        return familyTable;
    }

    @DELETE
    @Path("{param}")
    public void deletePerson(@PathParam("param") String name) {
        if(name == null) {
            throw new BadRequestException("name is null");
        }

        FamilyTable currFtb = db.getFamilyTbRow(name);
        if (currFtb == null) {
            throw new NotFoundException("Row with name not found: " + name);
        }

        db.deleteFamilyTbRow(currFtb);

    }
}
