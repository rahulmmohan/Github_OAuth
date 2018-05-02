package oauth.github.sample.app.github_oauth

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.rahul.githuboauth.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val GITHUB_ID = BuildConfig.GITHUB_ID
    val GITHUB_SECRET = BuildConfig.GITHUB_SECRET
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val githubAuthenticatorBuilder = GithubAuthenticator.builder(this)
                .clientId(GITHUB_ID)
                .clientSecret(GITHUB_SECRET)
                .scopeList(arrayListOf("gist", "repo"))
                .debug(true)
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
        githubAuthenticatorBuilder.debug(true)
        val githubAuthenticator = githubAuthenticatorBuilder.build()

        login.setOnClickListener {
            githubAuthenticator.authenticate()
        }


    }

}
