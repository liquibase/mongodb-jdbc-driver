package com.dbschema;

import com.dbschema.wrappers.WrappedMongoDatabase;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SimpleTest extends AbstractTestCase{

    private Connection con;

    private static final String urlWithAuth = "jdbc:mongodb://admin:fictivpwd@localhost:27017/local?authSource=local&connectTimeoutMS=1000";
    private static final String urlWithoutAuth = "jdbc:mongodb://localhost";


    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        Class.forName("com.dbschema.MongoJdbcDriver");
        con = DriverManager.getConnection( urlWithoutAuth, null, null);
        /*
        Statement stmt=con.createStatement();
        stmt.execute("local.words.drop();");
        stmt.execute("local.words.insertOne({word: 'sample', qty:2});");
        stmt.execute("local.words.insertOne({word: 'sample', qty:5});");
        stmt.close();
        */
    }

    @Test
    public void testListDatabases() {

        MongoConnection mongoConnection = (MongoConnection) this.con;
        mongoConnection.client.listDatabaseNames();
        WrappedMongoDatabase mongoDatabase = mongoConnection.client.getDatabase("localaaa");
        for( String dbName: mongoDatabase.listCollectionNames() ){
            System.out.println(dbName);
        }
    }

    @Test
    public void testFind() throws Exception {
        Statement stmt=con.createStatement();
        printResultSet( stmt.executeQuery("local.products.find()") );
        stmt.close();
    }
    @Test
    public void testInsert2() throws Exception {
        Statement stmt=con.createStatement();
        printResultSet( stmt.executeQuery("" +
                "local.cities3.drop();" +
                "local.cities3.insert(\n" +
                "{ \"country_id\" : \"USA\", \n" +
                "    \"city_name\" : \"San Francisco\", \n" +
                "    \"brother_cities\" : [\n" +
                "        \"Urban\", \"Paris\"\n" +
                "    ], \n" +
                "    \"suburbs\" : [\n" +
                "         {\n" +
                "            \"name\" : \"Scarsdale\"\n" +
                "         }, \n" +
                "        {\n" +
                "            \"name\" : \"North Hills\"\n" +
                "        } ]\n" +
                "    })") );
        con.commit();
        stmt.close();
    }
    @Test
    public void testFindAnd() throws Exception {
        Statement stmt=con.createStatement();
        printResultSet( stmt.executeQuery("local.sample.find({ $and: [ {'name':'DbSchema Free Version'}, {'name':'DbSchema Free Version'} ] } )") );
        stmt.close();
    }

    @Test
    public void testFindAndOr() throws Exception {
        Statement stmt=con.createStatement();
        printResultSet( stmt.executeQuery("local.sample.find({'likes': {$gt:10}, $or: [{'by': 'tutorials point'}, {'title': 'MongoDB Overview' }]})") );
        stmt.close();
    }

    @Test
    public void testUpdate() throws Exception {
        Statement stmt=con.createStatement();
        printResultSet( stmt.executeQuery("local.sample.update({'title':'MongoDB Overview'},{$set:{'title':'New MongoDB Tutorial'}})") );
        stmt.close();
    }


    @Test
    public void testFindId() throws Exception {
        /*
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId("5dd593595f94074908de3db9"));
        printResultSet( new ResultSetIterator(((MongoConnection)con).client.getDatabase("local").getCollection("products").find( query), true));
*/
        Statement stmt=con.createStatement();
        printResultSet( stmt.executeQuery("local.products.find({_id:'5f3291806abad86535411518'})" ) );
        stmt.close();
    }

    @Test
    public void testInsert() throws Exception {
        Statement stmt=con.createStatement();
        printResultSet( stmt.executeQuery("local.sample2.insert({ 'firstname' : 'Anna', 'lastname' : 'Pruteanu' })") );
        stmt.close();
    }

    @Test
    public void testISODate() throws Exception {
        Statement stmt=con.createStatement();
        printResultSet( stmt.executeQuery("local.sample2.insert({'shopId':'baaacd90d36e11e9adb40a8baad32c5a','date':ISODate('2019-12-25T07:23:18.408Z')})") );
        stmt.close();
    }

    @Test
    public void testFindGt() throws Exception {
        Statement stmt=con.createStatement();
        printResultSet( stmt.executeQuery("local.words.find( {qty:{$gt: 4}})"));
        stmt.close();
    }

    @Test
    public void testOID() throws Exception {
        Statement stmt=con.createStatement();
        printResultSet( stmt.executeQuery("db.test.insert({\"_id\":ObjectId(\"5e95cfecdfa8c111a4b2a53a\"), \"name\":\"first\"})"));
        stmt.close();
    }

    private static final String[] aggregateScript = new String[]{
            "db.foodCool.drop();",
            "db.foodColl.insert([\n"+
                    "   { category: \"cake\", type: \"chocolate\", qty: 10 },\n"+
                    "   { category: \"cake\", type: \"ice cream\", qty: 25 },\n"+
                    "   { category: \"pie\", type: \"boston cream\", qty: 20 },\n"+
                    "   { category: \"pie\", type: \"blueberry\", qty: 15 }\n"+
                    "]);",
            "db.foodColl.createIndex( { qty: 1, type: 1 } );",
            "db.foodColl.createIndex( { qty: 1, category: 1 } );",
            "db.foodColl.aggregate(\n"+
                    "   [ { $sort: { qty: 1 }}, { $match: { category: \"cake\", qty: 10  } }, { $sort: { type: -1 } } ]" +
                    ")"
    };


    @Test
    public void testAggregate() throws Exception {
        Statement stmt = con.createStatement();
        for ( String str : aggregateScript ) {
            printResultSet( stmt.executeQuery(str) );
        }
    }


}
