#!/bin/bash

# Script to create a new user via the API

API_URL="http://localhost:8080/auth/register"

# Default values
FIRST_NAME=""
LAST_NAME=""
EMAIL=""
PASSWORD=""
PHONE=""
ADDRESS=""
CIN=""

# Function to display usage
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Create a new user in the Intelligent Financial Assistant application"
    echo ""
    echo "Options:"
    echo "  -f, --firstname   First name (required)"
    echo "  -l, --lastname    Last name (required)"
    echo "  -e, --email       Email address (required)"
    echo "  -p, --password    Password (required, min 6 characters)"
    echo "  -n, --phone       Phone number (optional)"
    echo "  -a, --address     Address (optional)"
    echo "  -c, --cin         CIN (optional)"
    echo "  -h, --help        Display this help message"
    echo ""
    echo "Example:"
    echo "  $0 -f John -l Doe -e john@example.com -p password123"
    echo ""
    echo "Interactive mode (no arguments):"
    echo "  $0"
    exit 1
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -f|--firstname)
            FIRST_NAME="$2"
            shift 2
            ;;
        -l|--lastname)
            LAST_NAME="$2"
            shift 2
            ;;
        -e|--email)
            EMAIL="$2"
            shift 2
            ;;
        -p|--password)
            PASSWORD="$2"
            shift 2
            ;;
        -n|--phone)
            PHONE="$2"
            shift 2
            ;;
        -a|--address)
            ADDRESS="$2"
            shift 2
            ;;
        -c|--cin)
            CIN="$2"
            shift 2
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo "Unknown option: $1"
            usage
            ;;
    esac
done

# Interactive mode if no arguments provided
if [ -z "$FIRST_NAME" ] || [ -z "$LAST_NAME" ] || [ -z "$EMAIL" ] || [ -z "$PASSWORD" ]; then
    echo "=== Create New User ==="
    echo ""
    
    if [ -z "$FIRST_NAME" ]; then
        read -p "First Name: " FIRST_NAME
    fi
    
    if [ -z "$LAST_NAME" ]; then
        read -p "Last Name: " LAST_NAME
    fi
    
    if [ -z "$EMAIL" ]; then
        read -p "Email: " EMAIL
    fi
    
    if [ -z "$PASSWORD" ]; then
        read -sp "Password (min 6 characters): " PASSWORD
        echo ""
    fi
    
    read -p "Phone Number (optional): " PHONE
    read -p "Address (optional): " ADDRESS
    read -p "CIN (optional): " CIN
fi

# Validate required fields
if [ -z "$FIRST_NAME" ] || [ -z "$LAST_NAME" ] || [ -z "$EMAIL" ] || [ -z "$PASSWORD" ]; then
    echo "Error: First name, last name, email, and password are required!"
    exit 1
fi

# Build JSON payload
JSON_PAYLOAD=$(cat <<EOF
{
  "firstName": "$FIRST_NAME",
  "lastName": "$LAST_NAME",
  "email": "$EMAIL",
  "password": "$PASSWORD"
EOF
)

# Add optional fields if provided
if [ -n "$PHONE" ]; then
    JSON_PAYLOAD="$JSON_PAYLOAD,
  \"phoneNumber\": \"$PHONE\""
fi

if [ -n "$ADDRESS" ]; then
    JSON_PAYLOAD="$JSON_PAYLOAD,
  \"address\": \"$ADDRESS\""
fi

if [ -n "$CIN" ]; then
    JSON_PAYLOAD="$JSON_PAYLOAD,
  \"cin\": \"$CIN\""
fi

JSON_PAYLOAD="$JSON_PAYLOAD
}"

echo ""
echo "Creating user..."
echo ""

# Make API call
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "$JSON_PAYLOAD")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    echo "✓ User created successfully!"
    echo ""
    echo "Response:"
    echo "$BODY" | python3 -m json.tool 2>/dev/null || echo "$BODY"
    echo ""
    echo "You can now login at http://localhost:4200 with:"
    echo "  Email: $EMAIL"
    echo "  Password: ******"
else
    echo "✗ Failed to create user (HTTP $HTTP_CODE)"
    echo ""
    echo "Response:"
    echo "$BODY"
    exit 1
fi
