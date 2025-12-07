#!/bin/bash

# Keycloak Configuration
KEYCLOAK_URL="http://localhost:8080"
REALM="studentwellnesshub"
CLIENT_ID="wellness-hub-client"
CLIENT_SECRET="wellness-hub-secret"
USERNAME="admin1"
PASSWORD="admin123"

echo "=== Getting Authentication Token from Keycloak ==="
echo "Keycloak URL: $KEYCLOAK_URL"
echo "Realm: $REALM"
echo "Client ID: $CLIENT_ID"
echo "Username: $USERNAME"
echo ""

# Get token from Keycloak with client secret
LOGIN_RESPONSE=$(curl -s -X POST \
  "$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=$CLIENT_ID" \
  -d "client_secret=$CLIENT_SECRET" \
  -d "username=$USERNAME" \
  -d "password=$PASSWORD")

# Extract access token from Keycloak response
JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

if [ -z "$JWT_TOKEN" ]; then
  echo "✗ Failed to obtain token"
  echo ""
  echo "Response from Keycloak:"
  echo "$LOGIN_RESPONSE"
  echo ""
  exit 1
else
  echo "✓ Token obtained successfully"
  echo "Token preview: ${JWT_TOKEN:0:50}..."
  echo "Token length: ${#JWT_TOKEN} characters"
  echo ""
fi

# Export token
export JWT_TOKEN

# Run the complete circuit breaker test
echo "=== Starting Complete Circuit Breaker Test ==="
echo ""

if [ ! -f "./test-circuit-breaker.sh" ]; then
  echo "✗ Error: test-circuit-breaker-complete.sh not found"
  exit 1
fi

JWT_TOKEN="$JWT_TOKEN" ./test-circuit-breaker.sh