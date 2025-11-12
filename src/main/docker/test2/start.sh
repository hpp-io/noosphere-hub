docker-compose -f ./services.yml up noosphere-mysql -d
docker-compose -f ./services.yml up noosphere-noosphere-consul-config-loader -d
sleep 40
docker-compose -f ./services.yml up noosphere-auth -d
docker-compose -f ./services.yml up noosphere-consul -d
docker-compose -f ./services.yml up noosphere-pulsar -d
