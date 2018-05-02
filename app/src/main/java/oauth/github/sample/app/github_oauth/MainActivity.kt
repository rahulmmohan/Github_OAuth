package oauth.github.sample.app.github_oauth

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.rahul.githuboauth.ErrorCallback
import com.github.rahul.githuboauth.GithubAuthenticator
import com.github.rahul.githuboauth.SuccessCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val build = GithubAuthenticator.builder(this)
                .clientId("fbd747cbb00a75eda14f")
                .clientSecret("38f6c51cf1d6b9df16603a03d16a8a9da970970b")
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
                .build()

        login.setOnClickListener {
            build.authenticate()
        }


    }

}
