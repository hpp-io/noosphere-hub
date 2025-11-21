CREATE DATABASE nshub;
CREATE DATABASE keycloak;
CREATE DATABASE scheduler;
GRANT ALL PRIVILEGES ON keycloak.* TO 'nsuser';
GRANT ALL PRIVILEGES ON nshub.* TO 'nsuser';
GRANT ALL PRIVILEGES ON nshub.* TO 'scheduler';
