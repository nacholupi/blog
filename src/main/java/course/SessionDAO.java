package course;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import sun.misc.BASE64Encoder;

import java.security.SecureRandom;

public class SessionDAO {
    private final MongoCollection<Document> sessionsCollection;

    public SessionDAO(final MongoDatabase blogDatabase) {
        sessionsCollection = blogDatabase.getCollection("sessions");
    }


    public String findUserNameBySessionId(String sessionId) {
        Document session = getSession(sessionId);

        if (session == null) {
            return null;
        }
        else {
            return session.get("username").toString();
        }
    }


    // starts a new session in the sessions table
    public String startSession(String username) {

        // get 32 byte random number. that's a lot of bits.
        SecureRandom generator = new SecureRandom();
        byte randomBytes[] = new byte[32];
        generator.nextBytes(randomBytes);

        BASE64Encoder encoder = new BASE64Encoder();

        String sessionID = encoder.encode(randomBytes);

        // build the BSON object
        Document session = new Document("username", username);

        session.append("_id", sessionID);

        sessionsCollection.deleteMany(
                new Document("username",username));

        sessionsCollection.insertOne(session);

        return session.getString("_id");
    }

    // ends the session by deleting it from the sesisons table
    public void endSession(String sessionID) {
        sessionsCollection.deleteOne(Filters.eq("_id", sessionID));
    }

    // retrieves the session from the sessions table
    public Document getSession(String sessionID) {
        return sessionsCollection.find(Filters.eq("_id", sessionID)).first();
    }
}
