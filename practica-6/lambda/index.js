// ───────────────────────────────────────────────────
// AWS Lambda Functions – Lab Reservation System
// ───────────────────────────────────────────────────
// Deploy as individual Lambda functions behind API Gateway.
// Runtime: Node.js 20.x
// Environment variable: TABLE_NAME = "lab_reservations"

const { DynamoDBClient } = require("@aws-sdk/client-dynamodb");
const {
  DynamoDBDocumentClient,
  PutCommand,
  ScanCommand,
  QueryCommand,
} = require("@aws-sdk/lib-dynamodb");

const client = new DynamoDBClient({});
const ddb = DynamoDBDocumentClient.from(client);
const TABLE = process.env.TABLE_NAME || "lab_reservations";

// Max people per hour per lab
const MAX_PER_HOUR = 7;

// Valid hours: 8:00 – 22:00 (8am – 10pm)
const MIN_HOUR = 8;
const MAX_HOUR = 22;

// ─── CORS headers ─────────────────────────────────
const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "Content-Type",
  "Access-Control-Allow-Methods": "GET,POST,OPTIONS",
};

function response(statusCode, body) {
  return {
    statusCode,
    headers: { "Content-Type": "application/json", ...CORS },
    body: JSON.stringify(body),
  };
}

// ─────────────────────────────────────────────────────
// Handler: createReservation (POST /reservations)
// ─────────────────────────────────────────────────────
exports.createReservation = async (event) => {
  if (event.httpMethod === "OPTIONS") return response(200, {});

  try {
    const body = JSON.parse(event.body);
    const { email, name, studentId, carrera, lab, date, hour } = body;

    // ── Validation ──────────────────────────────
    if (!email || !name || !studentId || !carrera || !lab || !date || hour === undefined) {
      return response(400, { error: "All fields are required: email, name, studentId, carrera, lab, date, hour" });
    }

    const hourNum = parseInt(hour, 10);
    if (isNaN(hourNum) || hourNum < MIN_HOUR || hourNum >= MAX_HOUR) {
      return response(400, { error: `Hour must be between ${MIN_HOUR} and ${MAX_HOUR - 1} (8am – 9pm start times)` });
    }

    // Check the reservation date is not in the past
    const reservationDate = new Date(`${date}T${String(hourNum).padStart(2, "0")}:00:00`);
    if (reservationDate < new Date()) {
      return response(400, { error: "Cannot reserve in the past" });
    }

    // ── Capacity check ──────────────────────────
    const slotKey = `${date}#${hourNum}#${lab}`;
    const existing = await ddb.send(
      new QueryCommand({
        TableName: TABLE,
        IndexName: "slot-index",
        KeyConditionExpression: "slotKey = :sk",
        ExpressionAttributeValues: { ":sk": slotKey },
      })
    );

    if (existing.Items && existing.Items.length >= MAX_PER_HOUR) {
      return response(409, { error: `Maximum capacity (${MAX_PER_HOUR}) reached for this hour` });
    }

    // ── Save reservation ────────────────────────
    const id = `${Date.now()}-${Math.random().toString(36).substring(2, 8)}`;
    const item = {
      id,
      email,
      name,
      studentId,
      carrera,
      lab,
      date,
      hour: hourNum,
      slotKey,
      createdAt: new Date().toISOString(),
    };

    await ddb.send(new PutCommand({ TableName: TABLE, Item: item }));

    return response(201, { message: "Reservation created", reservation: item });
  } catch (err) {
    console.error(err);
    return response(500, { error: "Internal server error" });
  }
};

// ─────────────────────────────────────────────────────
// Handler: getActiveReservations (GET /reservations)
// ─────────────────────────────────────────────────────
exports.getActiveReservations = async (event) => {
  if (event.httpMethod === "OPTIONS") return response(200, {});

  try {
    const today = new Date().toISOString().split("T")[0];

    const result = await ddb.send(
      new ScanCommand({
        TableName: TABLE,
        FilterExpression: "#d >= :today",
        ExpressionAttributeNames: { "#d": "date" },
        ExpressionAttributeValues: { ":today": today },
      })
    );

    // Sort by date and hour
    const items = (result.Items || []).sort((a, b) => {
      const cmp = a.date.localeCompare(b.date);
      return cmp !== 0 ? cmp : a.hour - b.hour;
    });

    return response(200, { reservations: items });
  } catch (err) {
    console.error(err);
    return response(500, { error: "Internal server error" });
  }
};

// ─────────────────────────────────────────────────────
// Handler: getReservationsByRange (GET /reservations/history?from=..&to=..)
// ─────────────────────────────────────────────────────
exports.getReservationsByRange = async (event) => {
  if (event.httpMethod === "OPTIONS") return response(200, {});

  try {
    const from = event.queryStringParameters?.from;
    const to = event.queryStringParameters?.to;

    if (!from || !to) {
      return response(400, { error: "Query params 'from' and 'to' are required (YYYY-MM-DD)" });
    }

    const result = await ddb.send(
      new ScanCommand({
        TableName: TABLE,
        FilterExpression: "#d BETWEEN :from AND :to",
        ExpressionAttributeNames: { "#d": "date" },
        ExpressionAttributeValues: { ":from": from, ":to": to },
      })
    );

    const items = (result.Items || []).sort((a, b) => {
      const cmp = a.date.localeCompare(b.date);
      return cmp !== 0 ? cmp : a.hour - b.hour;
    });

    return response(200, { reservations: items });
  } catch (err) {
    console.error(err);
    return response(500, { error: "Internal server error" });
  }
};

// ─────────────────────────────────────────────────────
// Handler: getAvailability (GET /availability?date=..&lab=..)
// ─────────────────────────────────────────────────────
exports.getAvailability = async (event) => {
  if (event.httpMethod === "OPTIONS") return response(200, {});

  try {
    const date = event.queryStringParameters?.date;
    const lab = event.queryStringParameters?.lab;

    if (!date || !lab) {
      return response(400, { error: "Query params 'date' and 'lab' are required" });
    }

    // Get all reservations for this date+lab
    const result = await ddb.send(
      new ScanCommand({
        TableName: TABLE,
        FilterExpression: "#d = :date AND lab = :lab",
        ExpressionAttributeNames: { "#d": "date" },
        ExpressionAttributeValues: { ":date": date, ":lab": lab },
      })
    );

    // Build availability map
    const counts = {};
    for (let h = MIN_HOUR; h < MAX_HOUR; h++) counts[h] = 0;
    for (const item of result.Items || []) {
      counts[item.hour] = (counts[item.hour] || 0) + 1;
    }

    const availability = [];
    for (let h = MIN_HOUR; h < MAX_HOUR; h++) {
      availability.push({
        hour: h,
        label: `${h}:00 – ${h + 1}:00`,
        reserved: counts[h],
        available: MAX_PER_HOUR - counts[h],
        full: counts[h] >= MAX_PER_HOUR,
      });
    }

    return response(200, { date, lab, availability });
  } catch (err) {
    console.error(err);
    return response(500, { error: "Internal server error" });
  }
};
