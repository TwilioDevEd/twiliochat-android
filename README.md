# twiliochat-android
[![Build Status](https://travis-ci.org/TwilioDevEd/twiliochat-android.svg?branch=master)](https://travis-ci.org/TwilioDevEd/twiliochat-android)

Android implementation of Twilio Chat

### Running the Application
1. We use [Parse](https://www.parse.com) to handle user accounts and also fetch
   Twilio [access tokens](https://www.twilio.com/docs/api/ip-messaging/guides/identity)
   in this application.
   First, Configure your [Parse](https://www.parse.com) keys so the application can
   communicate with your parse app. Click [here](#create-a-parse-app) for more information on how
   to create a `Parse App`.

1. Clone the repository and `cd` into it.

1. The project includes a sample `keys.example.xml` file on the root of the project.
   This file should contain the `Client Key` and `Application ID` for your parse app.
   So, replace the sample keys in this file to match your keys and then, from the command line,
   you can use this command to copy and rename the file to where it is supposed to be:

   ```
   $ cp keys.example.xml app/src/main/res/values/keys.xml
   ```

   You can use any method you like, but in the end, you should have a file named
   `keys.xml` in the directory `app/src/main/res/values/` of the project, and this
   file should contain your parse keys in the marked places. If you try to import the
   project before this step, you'll get an error saying that the file is missing
   (if you already did, just copy the file and rebuild the project. The error should be gone).

1. In order for the app to get the access token from Twilio, you need to set up a
   [parse webhook](https://parse.com/docs/cloudcode/guide#cloud-code-advanced-cloud-code-webhooks)
   in your parse app described in a previous step. This webhook's URL should be pointing
   at your own `Twilio token generation app`. There's one example application written
   in Node that demonstrates how to generate access tokens,
   [here](https://github.com/TwilioDevEd/twiliochat).

   If you need to expose you localhost so it is visible to Parse, you can use
   [ngrok](https://ngrok.com/), a handy tool that will give the outside world access
   to you localhost in any port you specify.

1. This application was developed using [Android Studio](http://developer.android.com/tools/studio/index.html).
   So, if you are using a different tool like Eclipse with ADT there might be some additional
   steps you need to follow. If you are using Android Studio, just open the project
   using the IDE. If you didn't skip any step, you should be good to go. Just run
   the app on the simulator or your own device.

 **Note:** The current version of the app uses a fixed version of Twilio SDK (included in the
 repository). This version only work for arm devices as the SDK includes some native code.

### Create a Parse App
This android application uses [Parse](https://www.parse.com) to manage sessions.

The first thing you need to do, is login into your parse account, if you don't have
one yet, [signup](https://www.parse.com/signup) for one, it's free!

Once you are logged in, visit [your app dashboard](https://www.parse.com/apps/)
to create a new app. The next step is to get your parse `Application ID` and
`Client Key`, for this, visit the following url, replacing first your your app's
ID portion of it:

```
https://www.parse.com/apps/<your_app_name>/settings/keys
```

You will need both of these keys to configure the Twilio Chat Application.
