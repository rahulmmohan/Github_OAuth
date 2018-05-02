package oauth.github.sample.app.github_oauth

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.rahul.github_oauth.ErrorCallback
import com.github.rahul.github_oauth.GithubAuthenticator
import com.github.rahul.github_oauth.SuccessCallback

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val build = GithubAuthenticator.builder(this)
                .clientId("fbd747cbb00a75eda14f")
                .clientSecret("38f6c51cf1d6b9df16603a03d16a8a9da970970b")
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
                .build()

        build.authenticate()

    }

}
