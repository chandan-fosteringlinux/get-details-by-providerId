
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.bson.Document

@Path("/notification/v1")
@Produces(MediaType.APPLICATION_JSON)
class NotificationResource(private val service: NotificationService) {

    @GET
    @Path("/{partnerId}/getDetails")
    fun getDetails(
        @PathParam("partnerId") partnerId: String,
        @QueryParam("applicationId") applicationId: Int,
        @QueryParam("fromDate") fromDate: String,
        @QueryParam("toDate") toDate: String,
        @QueryParam("channel") channel: String
    ): List<Document> {
        return service.findNotifications(partnerId, applicationId, fromDate, toDate, channel)
    }
}