DO
$$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak') THEN
    CREATE DATABASE keycloak;
  END IF;
END
$$;
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
