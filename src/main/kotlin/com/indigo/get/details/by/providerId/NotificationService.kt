
import com.mongodb.client.MongoClient
import jakarta.enterprise.context.ApplicationScoped
import org.bson.Document
import java.time.Instant

@ApplicationScoped
class NotificationService(private val mongoClient: MongoClient) {
    private val collection get() = mongoClient
        .getDatabase("testdb")
        .getCollection("notifications")

    fun findNotifications(partnerId: String, applicationId: Int, fromDate: String, toDate: String, channel: String): List<Document> {
        val from = Instant.parse("${fromDate}T00:00:00Z")
        val to = Instant.parse("${toDate}T23:59:59Z")

        val filter = Document("partnerId", partnerId)
            .append("applicationId", applicationId)
            .append("channel", channel)
            .append("timeStamp", Document("\$gte", from).append("\$lte", to))
            

        return collection.find(filter).toList()
    }
}