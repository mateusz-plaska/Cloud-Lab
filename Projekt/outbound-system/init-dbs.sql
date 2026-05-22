SELECT 'CREATE DATABASE orderdb' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'orderdb')\gexec
SELECT 'CREATE DATABASE reservationdb' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'reservationdb')\gexec
SELECT 'CREATE DATABASE packingdb' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'packingdb')\gexec
SELECT 'CREATE DATABASE bffdb' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'bffdb')\gexec
SELECT 'CREATE DATABASE shippingdb' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'shippingdb')\gexec