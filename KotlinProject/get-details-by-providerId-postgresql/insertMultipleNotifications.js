use("testdb");

db.notifications.insertMany([
  {
    requestId: "123e4567-e89b-12d3-a456-426614174000",
    messageId: 146500,
    applicationId: 401,
    applicationName: "IDA",
    templateId: 3001,
    priorityQueue: "HIGH",
    channel: "SMS",
    content: "Your Indigo flight from MAA to BLR on 27 Jun 25 at 09:15 hrs is confirmed.",
    bulkContent: null,
    recipients: ["+919811111111"],
    currentStage: "Dispatched",
    currentTopic: "statusTopic",
    log: "SMS sent to vendor MSG91",
    timeStamp: ISODate("2025-06-27T03:30:00Z"),
    vendor: {
      id: {
        "+919811111111": "msg91-9988776655"
      },
      name: "MSG91"
    },
    partnerId: "P45678"
  },
  {
    requestId: "456e7890-f12a-34b5-b678-9876543210ff",
    messageId: 146501,
    applicationId: 401,
    applicationName: "IDA",
    templateId: 3002,
    priorityQueue: "LOW",
    channel: "SMS",
    content: "Your flight from DEL to HYD on 29 Jun 25 at 16:00 hrs has been updated.",
    bulkContent: null,
    recipients: ["+917733445566"],
    currentStage: "Updated",
    currentTopic: "reminderTopic",
    log: "Flight details updated in notification service",
    timeStamp: ISODate("2025-06-25T13:00:00Z"),
    vendor: {
      id: {
        "+917733445566": "bulkSMS-1122334455"
      },
      name: "BulkSMS"
    },
    partnerId: "P12345"
  },
  {
    requestId: "789f1234-5678-90ab-cdef-0987654321ff",
    messageId: 146502,
    applicationId: 401,
    applicationName: "IDA",
    templateId: 3003,
    priorityQueue: "MEDIUM",
    channel: "SMS",
    content: "Reminder: Boarding time for flight BOM to PNQ on 30 Jun 25 is 07:30 hrs.",
    bulkContent: null,
    recipients: ["+918800112233"],
    currentStage: "Reminder Sent",
    currentTopic: "reminderTopic",
    log: "Reminder successfully queued",
    timeStamp: ISODate("2025-06-28T09:45:00Z"),
    vendor: {
      id: {
        "+918800112233": "reminder-7788990011"
      },
      name: "ReminderVendor"
    },
    partnerId: "P67890"
  }
]);
