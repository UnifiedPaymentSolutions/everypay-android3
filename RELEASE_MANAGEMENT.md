# Update Version

1. update `build.gradle` file
    * The `VERSION` string
    * The `versionCode` integer
2. Tag git commit with specified version 

# Bintray Upload Steps

* Read https://github.com/bintray/gradle-bintray-plugin
* `gradle clean`
* `gradle install`
* `gradle bintrayUpload`

