package database;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

public class MongoController 
{
	
	DB db;
	MongoClient mongoClient;
	
	public MongoController(String dbname)
	{
		try
		{
			mongoClient=new MongoClient("localhost", 27017);
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		db=mongoClient.getDB(dbname);
	}
	
	
	
}
