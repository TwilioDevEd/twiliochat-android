<a href="https://www.twilio.com">
  <img src="https://static0.twilio.com/marketing/bundles/marketing/img/logos/wordmark-red.svg" alt="Twilio" width="250" />
</a>

# Twilio chat - Android
[![Build Status](https://travis-ci.org/TwilioDevEd/twiliochat-android.svg?branch=master)](https://travis-ci.org/TwilioDevEd/twiliochat-android)

Learn to implement a simple chat application using Twilio Programmable Chat Client

### Local development

1. Clone the repository.

  ```bash
  $ git clone https://github.com/TwilioDevEd/twiliochat-android.git
  $ cd twiliochat-android
  ```

1. This application was developed using [Android Studio](http://developer.android.com/tools/studio/index.html).
   So if you are using a different tool like Eclipse with ADT, there might be some additional
   steps you need to follow. If you are using Android Studio just open the project
   using the IDE.

1. [Twilio's Programmable Chat Client](https://www.twilio.com/docs/api/chat) requires an
   [access token](https://www.twilio.com/docs/api/ip-messaging/guides/identity) generated using your
   Twilio credentials in order to connect. First we need to setup a server that will generate this token
   for the mobile application to use. We have created web versions of Twilio Chat, you can use any of these
   applications to generate the token that this mobile app requires, just pick you favorite flavor:

   * [PHP - Laravel](https://github.com/TwilioDevEd/twiliochat-laravel)
   * [C# - .NET MVC](https://github.com/TwilioDevEd/twiliochat-csharp)
   * [Java - Servlets](https://github.com/TwilioDevEd/twiliochat-servlets)
   * [JS - Node](https://github.com/TwilioDevEd/twiliochat-node)

   Look for instructions on how to setup these servers in any of the links above.

1. Once you have the server running (from the previous step), you need to edit one file in this android
   application.

   ```
   ProjectRoot(app) -> res -> values -> keys.xml (on the Android Studio)
   or
   ProjectRoot/app/src/main/res/values/keys.xml (on the file system)
   ```
   This file contains the `token_url` key. The default values is `http://10.0.2.2:8000/token`. This
   address refers to the host machine loopback interface (127.0.0.1) when running this application
   in the android emulator. You must change this value to match the address of your server running
   the token generation application. We are using the [PHP - Laravel](https://github.com/TwilioDevEd/twiliochat-laravel)
   version in this case, that's why we use port 8000.

   ***Note:*** In some operating systems you need to specify the address for the development server
   when you run the Laravel application, here's an example:
   ```
   $ php artisan serve --host=127.0.0.1
   ```

1. Now Twilio Chat is ready to go. Run the application on the android emulator or your own device.
   Make sure that you have properly set up the token generation server and the `token_url` key.
   To run the application in a real device you'll need to expose your local token generation server
   by manually forwarding ports, or using a tool like [ngrok](https://ngrok.com/).
   If you decide to work with ngrok, your keys.xml file should hold a key like the following:

   ```
   <string name="token_url">"http://<your_subdomain>.ngrok.io/token"</string>
   ```
   No need to specify the port in this url, as ngrok will forward the request to the specified port.

 ***Note:*** The current version of the app uses a fixed version of Twilio SDK (included in the
 repository). This version only works for ARM devices as the SDK includes some native code.


 ## Meta
* No warranty expressed or implied. Software is as is. Diggity.
* [MIT License](http://www.opensource.org/licenses/mit-license.html)
* Lovingly crafted by Twilio Developer Education.
