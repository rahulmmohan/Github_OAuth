# Github OAuth Client Library - Android

# Setup
## 1. Include in your project

### Using Gradle
The **Github-OAuth** library is pushed to jcenter, so you need to add the following dependency to your app's `build.gradle`.

```gradle
implementation 'com.github.rahul:github-oauth:1.0'
```

### As a module
If you can't include it as gradle dependency, you can also download this GitHub repo and copy the library folder to your project.


## 2. Usage
### Obtaining GithubAuthenticator instance
```kotlin
val githubAuthenticatorBuilder = GithubAuthenticator.builder(this)
                .clientId(GITHUB_ID)
                .clientSecret(GITHUB_SECRET)
                .onSuccess(object : SuccessCallback {
                    override fun onSuccess(result: String) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, result,
                                    Toast.LENGTH_LONG).show()
                        }
                    }
                })
                .onError(object : ErrorCallback {
                    override fun onError(error: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, error.message,
                                    Toast.LENGTH_LONG).show()
                        }
                    }
                })
                
val githubAuthenticator = githubAuthenticatorBuilder.build()
```

### Obtaining an access token via the GithubAuthenticator
```kotlin
githubAuthenticator.authenticate()
```
Authenticate will open a new Dialog Fragment with webview and user token will be returned on callback

### Enable log
```kotlin
githubAuthenticatorBuilder.debug(true)
```
### scope can also be defined (optional)
```kotlin
githubAuthenticatorBuilder.scopeList(arrayListOf("scope1", "scope2"))
```
Available scopes are from [developer page](https://developer.github.com/apps/building-oauth-apps/scopes-for-oauth-apps/#available-scopes)

## License

    Copyright 2018, Rahul M Mohan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
