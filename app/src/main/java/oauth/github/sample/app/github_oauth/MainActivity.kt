package oauth.github.sample.app.github_oauth

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.github.rahul.github_oauth.Constants
import com.github.rahul.github_oauth.GithubOAuthActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, GithubOAuthActivity::class.java)
        intent.putExtra("id", "fbd747cbb00a75eda14f")
        intent.putExtra("secret", "38f6c51cf1d6b9df16603a03d16a8a9da970970b")
        startActivityForResult(intent, Constants.REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "token received: " + data!!.getStringExtra(Constants.TOKEN), Toast.LENGTH_LONG).show()
        }
    }
}
