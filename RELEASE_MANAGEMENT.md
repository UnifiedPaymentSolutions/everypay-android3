# SETUP

Ensure that the `local.properties` file exists and contains Bintray credentials:

```
bintray.user=<USERNAME>
bintray.apikey=<API_KEY>
```

# Update Version

1. update `build.gradle` file
    * The `VERSION` string
    * The `versionCode` integer
2. Tag git commit with specified version 

# Bintray Upload Steps

* Read https://github.com/bintray/gradle-bintray-plugin
* `./gradlew clean`
* `./gradlew install`
* `./gradlew bintrayUpload`

