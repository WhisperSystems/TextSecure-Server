web:    java $JAVA_OPTS -Ddw.http.port=$PORT -Ddw.http.adminPort=$PORT -Ddw.twilio.accountId=$TWILIO_ACCOUNTID -Ddw.twilio.accountToken=$TWILIO_ACCOUNT_TOKEN -Ddw.twilio.numbers[0]=$TWILIO_NUMBER -Ddw.twilio.localDomain=https://localhost:$PORT -Ddw.cache.url=$REDIS_URL/0 -Ddw.cache.replicaUrls[0]=$REDIS_URL/0 -Ddw.directory.url=$REDIS_URL/1 -Ddw.directory.replicaUrls[0]=$REDIS_URL/1 -Ddw.messageCache.redis.url=$REDIS_URL/2 -Ddw.messageCache.redis.replicaUrls[0]=$REDIS_URL/2 -Ddw.pushScheduler.url=$REDIS_URL/3 -Ddw.pushScheduler.replicaUrls[0]=$REDIS_URL/3 -Ddw.messageStore.driverClass=org.postgresql.Driver -Ddw.messageStore.user=`echo $HEROKU_POSTGRESQL_PINK_URL | awk -F'://' {'print $2'} | awk -F':' {'print $1'}` -Ddw.messageStore.password=`echo $HEROKU_POSTGRESQL_PINK_URL | awk -F'://' {'print $2'} | awk -F':' {'print $2'} | awk -F'@' {'print $1'}` -Ddw.messageStore.url=jdbc:postgresql://`echo $HEROKU_POSTGRESQL_PINK_URL | awk -F'@' {'print $2'}` -Ddw.attachments.accessKey=$S3_ACCESS_KEY -Ddw.attachments.accessSecret=$S3_ACCESS_SECRET -Ddw.attachments.bucket=$S3_ATTACHMENTS_BUCKET -Ddw.profiles.accessKey=$S3_ACCESS_KEY -Ddw.profiles.accessSecret=$S3_ACCESS_SECRET -Ddw.profiles.bucket=$S3_PROFILES_BUCKET -Ddw.profiles.region=$S3_REGION -Ddw.database.driverClass=org.postgresql.Driver -Ddw.database.user=`echo $DATABASE_URL | awk -F'://' {'print $2'} | awk -F':' {'print $1'}` -Ddw.database.password=`echo $DATABASE_URL | awk -F'://' {'print $2'} | awk -F':' {'print $2'} | awk -F'@' {'print $1'}` -Ddw.database.url=jdbc:postgresql://`echo $DATABASE_URL | awk -F'@' {'print $2'}` -Ddw.apn.bundleId=$APN_BUNDLE_ID -Ddw.apn.pushCertificate=$APN_PUSH_CERTIFICATE -Ddw.apn.pushKey=$APN_PUSH_KEY -Ddw.gcm.apiKey=$GCM_API_KEY -Ddw.gcm.senderId=$GCM_SENDER_ID -Ddw.testDevices[0].number=$TEST_DEVICE_NUMBER -Ddw.testDevices[0].code=$TEST_DEVICE_CODE -jar target/TextSecureServer-1.88.jar server