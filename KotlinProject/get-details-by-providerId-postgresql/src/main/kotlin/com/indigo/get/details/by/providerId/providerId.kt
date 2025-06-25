// package com.indigo.get.details.by.providerId

// import jakarta.ws.rs.*
// import jakarta.ws.rs.core.MediaType
// import jakarta.ws.rs.core.Response

// @Path("/notification/v1")
// @Produces(MediaType.TEXT_PLAIN)
// class GreetingResource {

//     @GET
//     @Path("/{providerId}/getDetails")
//     fun getDetails(
//         @PathParam("providerId") providerId: String,
//         @QueryParam("applicationId") applicationId: String?,
//         @QueryParam("fromDate") fromDate: String?,
//         @QueryParam("toDate") toDate: String?,
//         @QueryParam("channel") channel: String?
//     ): Response {
//         val responseMessage = """
//             {
//          "requestId": "89f24385-9b55-4999-a037-fab6228387f3",
//          "messageId": 146376,
//          "applicationId": 401,
//          "applicationName": "IDA",
//          "templateId": 2954,
//          "priorityQueue": "HIGH",
//          "channel": "SMS",
//          "content": "Dear Flyer, your travel plans are in place! We are delighted to be a part of this journey and look forward to welcoming you on board. We're happy to confirm the booking under PNR ****3N on 26 Jun 25, from BOM(T2) to GOI, 6E 652 at 17:20 hrs. 26 Jun 25, from GOI to MAA(T1), 6E 589 at 19:50 hrs. Should you need any assistance regarding the travel or destination, our instant chatbot, https://www.goindigo.in/support.html is always ready to help. To learn about passenger rights, please click https://www.civilaviation.gov.in/ministry-documents/passenger-charter-of-rights Ready. Set. IndiGo!",
//          "bulkContent": null,
//          "recipients": [
//                   "+9786********"
//          ],
//          "currentStage": "Message Posted to SMS G/W",
//          "currentTopic": "statusTopic",
//          "log": "Message sent to the SMS adapter through GupShup",
//          "timeStamp": "2025-06-18T07:07:24.162+00:00",
//          "vendor": {
//                   "id": {
//                            "9786********": "0688***************-1346**************"
//                   },
//                   "name": "GupShup"
//          }
// }
//         """.trimIndent()

//         return Response.ok(responseMessage).build()
//     }
// }

// package com.indigo.get.details.by.providerId

// import jakarta.inject.Inject
// import jakarta.ws.rs.*
// import jakarta.ws.rs.core.MediaType
// import jakarta.ws.rs.core.Response
// import javax.sql.DataSource
// import java.sql.Timestamp

// @Path("/notification/v1")
// @Produces(MediaType.APPLICATION_JSON)
// class GreetingResource {

//     @Inject
//     lateinit var dataSource: DataSource

//     @GET
//     @Path("/{providerId}/getDetails")
//     fun getDetails(
//         @PathParam("providerId") providerId: String,
//         @QueryParam("applicationId") applicationId: String?,
//         @QueryParam("fromDate") fromDate: String?,
//         @QueryParam("toDate") toDate: String?,
//         @QueryParam("channel") channel: String?
//     ): Response {
//         val results = mutableListOf<Map<String, Any?>>()
//         dataSource.connection.use { connection ->
//             val sql = StringBuilder("""
//                 SELECT 
//                     m.request_id, m.message_id, m.application_id, m.application_name, 
//                     m.template_id, m.priority_queue, m.channel, m.content, 
//                     m.bulk_content, m.current_stage, m.current_topic, m.log, 
//                     m.timestamp, m.vendor_name, r.recipient, v.phone, v.vendor_identifier
//                 FROM notification_messages m
//                 LEFT JOIN message_recipients r ON m.request_id = r.request_id
//                 LEFT JOIN vendor_ids v ON m.request_id = v.request_id
//                 WHERE m.provider_id = ?
//             """.trimIndent())

//             val params = mutableListOf<Any>(providerId)

//             if (!applicationId.isNullOrBlank()) {
//                 sql.append(" AND m.application_id = ?")
//                 params.add(applicationId)
//             }

//             if (!channel.isNullOrBlank()) {
//                 sql.append(" AND m.channel = ?")
//                 params.add(channel)
//             }

//             if (!fromDate.isNullOrBlank()) {
//                 sql.append(" AND m.timestamp >= ?")
//                 params.add(Timestamp.valueOf("${fromDate} 00:00:00"))
//             }

//             if (!toDate.isNullOrBlank()) {
//                 sql.append(" AND m.timestamp <= ?")
//                 params.add(Timestamp.valueOf("${toDate} 23:59:59"))
//             }

//             val statement = connection.prepareStatement(sql.toString())
//             for ((i, param) in params.withIndex()) {
//                 statement.setObject(i + 1, param)
//             }

//             val rs = statement.executeQuery()
//             while (rs.next()) {
//                 val row = mutableMapOf<String, Any?>(
//                     "requestId" to rs.getString("request_id"),
//                     "messageId" to rs.getInt("message_id"),
//                     "applicationId" to rs.getString("application_id"),
//                     "applicationName" to rs.getString("application_name"),
//                     "templateId" to rs.getInt("template_id"),
//                     "priorityQueue" to rs.getString("priority_queue"),
//                     "channel" to rs.getString("channel"),
//                     "content" to rs.getString("content"),
//                     "bulkContent" to rs.getString("bulk_content"),
//                     "currentStage" to rs.getString("current_stage"),
//                     "currentTopic" to rs.getString("current_topic"),
//                     "log" to rs.getString("log"),
//                     "timeStamp" to rs.getTimestamp("timestamp").toString(),
//                     "vendor" to mapOf(
//                         "id" to mapOf(
//                             rs.getString("phone") to rs.getString("vendor_identifier")
//                         ),
//                         "name" to rs.getString("vendor_name")
//                     ),
//                     "recipients" to listOf(rs.getString("recipient"))
//                 )
//                 results.add(row)
//             }

//             rs.close()
//             statement.close()
//         }

//         return if (results.isEmpty()) {
//             Response.status(Response.Status.NOT_FOUND)
//                 .entity(mapOf("message" to "No records found for given parameters")).build()
//         } else {
//             Response.ok(results).build()
//         }
//     }
// }





// package com.indigo.get.details.by.providerId

// import jakarta.ws.rs.*
// import jakarta.ws.rs.core.*
// import jakarta.inject.Inject
// import javax.sql.DataSource
// import java.sql.Timestamp
// import jakarta.enterprise.context.ApplicationScoped // 👈 Required for discovery

// @ApplicationScoped // 👈 Very important!
// @Path("/notification/v1")
// @Produces(MediaType.APPLICATION_JSON)
// class GreetingResource {

//     @Inject
//     lateinit var dataSource: DataSource

//     @GET
//     @Path("/{providerId}/getDetails")
//     fun getDetails(
//         @PathParam("providerId") providerId: String,
//         @QueryParam("applicationId") applicationId: String?,
//         @QueryParam("fromDate") fromDate: String?,
//         @QueryParam("toDate") toDate: String?,
//         @QueryParam("channel") channel: String?
//     ): Response {
//         return Response.ok(mapOf("message" to "Dummy response - endpoint working")).build()
//     }
// }







package com.indigo.get.details.by.providerId

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.sql.Timestamp
import javax.sql.DataSource

@ApplicationScoped
@Path("/notification/v1")
@Produces(MediaType.APPLICATION_JSON)
class GreetingResource {

    @Inject
    lateinit var dataSource: DataSource

    @GET
    @Path("/{providerId}/getDetails")
    fun getDetails(
        @PathParam("providerId") providerId: String, // used only for logging or future use
        @QueryParam("applicationId") applicationId: String?,
        @QueryParam("fromDate") fromDate: String?,
        @QueryParam("toDate") toDate: String?,
        @QueryParam("channel") channel: String?
    ): Response {
        val results = mutableListOf<Map<String, Any?>>()

        dataSource.connection.use { connection ->
            val sql = StringBuilder("""
                SELECT 
                    m.request_id, m.message_id, m.application_id, m.application_name, 
                    m.template_id, m.priority_queue, m.channel, m.content, 
                    m.bulk_content, m.current_stage, m.current_topic, m.log, 
                    m.timestamp, m.vendor_name, r.recipient, v.phone, v.vendor_identifier
                FROM notification_messages m
                LEFT JOIN message_recipients r ON m.request_id = r.request_id
                LEFT JOIN vendor_ids v ON m.request_id = v.request_id
                WHERE 1 = 1
            """.trimIndent())

            val params = mutableListOf<Any>()

            // if (!applicationId.isNullOrBlank()) {
            //     sql.append(" AND m.application_id = ?")
            //     params.add(applicationId)
            // }

            if (!applicationId.isNullOrBlank()) {
    try {
        sql.append(" AND m.application_id = ?")
        params.add(applicationId.toInt())
    } catch (e: NumberFormatException) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(mapOf("error" to "applicationId must be an integer")).build()
    }
}


            if (!channel.isNullOrBlank()) {
                sql.append(" AND m.channel = ?")
                params.add(channel)
            }

            if (!fromDate.isNullOrBlank()) {
                sql.append(" AND m.timestamp >= ?")
                params.add(Timestamp.valueOf("${fromDate} 00:00:00"))
            }

            if (!toDate.isNullOrBlank()) {
                sql.append(" AND m.timestamp <= ?")
                params.add(Timestamp.valueOf("${toDate} 23:59:59"))
            }

            val statement = connection.prepareStatement(sql.toString())
            for ((i, param) in params.withIndex()) {
                statement.setObject(i + 1, param)
            }

            val rs = statement.executeQuery()
            while (rs.next()) {
                val row = mutableMapOf<String, Any?>(
                    "requestId" to rs.getString("request_id"),
                    "messageId" to rs.getInt("message_id"),
                    "applicationId" to rs.getInt("application_id"),
                    "applicationName" to rs.getString("application_name"),
                    "templateId" to rs.getInt("template_id"),
                    "priorityQueue" to rs.getString("priority_queue"),
                    "channel" to rs.getString("channel"),
                    "content" to rs.getString("content"),
                    "bulkContent" to rs.getString("bulk_content"),
                    "currentStage" to rs.getString("current_stage"),
                    "currentTopic" to rs.getString("current_topic"),
                    "log" to rs.getString("log"),
                    "timeStamp" to rs.getTimestamp("timestamp").toString(),
                    "vendor" to mapOf(
                        "id" to mapOf(
                            rs.getString("phone") to rs.getString("vendor_identifier")
                        ),
                        "name" to rs.getString("vendor_name")
                    ),
                    "recipients" to listOf(rs.getString("recipient"))
                )
                results.add(row)
            }

            rs.close()
            statement.close()
        }

        return if (results.isEmpty()) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to "No records found for given parameters")).build()
        } else {
            Response.ok(results).build()
        }
    }
}










// package com.indigo.get.details.by.providerId

// import jakarta.enterprise.context.ApplicationScoped
// import org.apache.camel.Exchange
// import org.apache.camel.builder.RouteBuilder
// import java.sql.Timestamp

// @ApplicationScoped
// class NotificationDetailsRoute : RouteBuilder() {
//     override fun configure() {

//         restConfiguration()
//             .contextPath("/")
//             .port(8080)

//         rest("/notification/v1")
//             .get("/{providerId}/getDetails")
//             .to("direct:notification-details")

//         from("direct:notification-details")
//             .routeId("notification-details-route")
//             .process { exchange ->
//                 val providerId = exchange.getIn().getHeader("providerId", String::class.java)
//                 val applicationId = exchange.getIn().getHeader("applicationId", String::class.java)
//                 val fromDate = exchange.getIn().getHeader("fromDate", String::class.java)
//                 val toDate = exchange.getIn().getHeader("toDate", String::class.java)
//                 val channel = exchange.getIn().getHeader("channel", String::class.java)

//                 val sqlBuilder = StringBuilder("""
//                     SELECT 
//                         m.request_id, m.message_id, m.application_id, m.application_name, 
//                         m.template_id, m.priority_queue, m.channel, m.content, 
//                         m.bulk_content, m.current_stage, m.current_topic, m.log, 
//                         m.timestamp, m.vendor_name, r.recipient, v.phone, v.vendor_identifier
//                     FROM notification_messages m
//                     LEFT JOIN message_recipients r ON m.request_id = r.request_id
//                     LEFT JOIN vendor_ids v ON m.request_id = v.request_id
//                     WHERE 1=1
//                 """.trimIndent())

//                 val params = mutableListOf<Any>()

//                 if (!providerId.isNullOrBlank()) {
//                     sqlBuilder.append(" AND m.provider_id = ?")
//                     params.add(providerId)
//                 }

//                 if (!applicationId.isNullOrBlank()) {
//                     sqlBuilder.append(" AND m.application_id = ?")
//                     params.add(applicationId.toInt())
//                 }

//                 if (!channel.isNullOrBlank()) {
//                     sqlBuilder.append(" AND m.channel = ?")
//                     params.add(channel)
//                 }

//                 if (!fromDate.isNullOrBlank()) {
//                     sqlBuilder.append(" AND m.timestamp >= ?")
//                     params.add(Timestamp.valueOf("${fromDate} 00:00:00"))
//                 }

//                 if (!toDate.isNullOrBlank()) {
//                     sqlBuilder.append(" AND m.timestamp <= ?")
//                     params.add(Timestamp.valueOf("${toDate} 23:59:59"))
//                 }

//                 exchange.setProperty("dynamicSql", sqlBuilder.toString())
//                 exchange.setProperty("dynamicParams", params)
//             }
//             .to("direct:queryDb")

//         from("direct:queryDb")
//             .routeId("query-db-route")
//             .process { exchange ->
//                 val sql = exchange.getProperty("dynamicSql", String::class.java)
//                 val params = exchange.getProperty("dynamicParams", List::class.java)

//                 exchange.getIn().body = params
//                 exchange.getIn().setHeader("CamelSqlQuery", sql)
//             }
//             .to("sql:dummy?dataSource=#myDataSource&outputType=SelectList")
//             .process { exchange ->
//                 val rows = exchange.getIn().getBody(List::class.java) as List<Map<String, Any?>>
//                 if (rows.isEmpty()) {
//                     exchange.message.body = mapOf("message" to "No records found")
//                     exchange.message.setHeader(Exchange.HTTP_RESPONSE_CODE, 404)
//                 } else {
//                     exchange.message.body = rows
//                 }
//             }
//     }
// }