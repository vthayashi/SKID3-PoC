# SKID3-PoC
SKID3 authentication protocol Proof of Concept with mobile device, bank server and user interface entities

## Requisites
* Tested with Samsung S20 with Android 11, Windows 10 laptop, Google Chrome as browser
* Android Studio
* Android Debug Bridge over WiFi: https://developer.android.com/studio/command-line/adb
* Android Debug Bridge port forwarding: in C:\Users\username\AppData\Local\Android\Sdk\platform-tools, execute command prompt with "adb reverse tcp:8765 tcp:8765"

## Folders
* BS: bank server emulated in python with websockets communication
* UI: user interface emulated as websockets HTML/javascript client
* TM: trusted mobile device in Android/java with websockets communication
* results: response time results obtained with 1000 requests, with one request each 2 seconds in a local environment with 802.11 WiFi

## References

### Android
* OKHTTP websockets library: https://github.com/square/okhttp
* Pseudorandom number generator: https://developer.android.com/reference/java/util/Random
* JSON parser: https://developer.android.com/reference/org/json/JSONObject
* Keyed hash: https://developer.android.com/reference/kotlin/java/security/MessageDigest
* Base64 encoding: https://developer.android.com/reference/android/util/Base64.html

### Python
* Websockets library: https://websockets.readthedocs.io/en/stable/
* Pseudorandom number generator: https://docs.python.org/3/library/random.html
* Keyed hash: https://docs.python.org/3/library/hmac.html
