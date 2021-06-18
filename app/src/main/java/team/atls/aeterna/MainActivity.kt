package team.atls.aeterna

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            System.loadLibrary("interaction")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("Interaction", "Load libary ERROR: $e")
            return
        }

        val username = findViewById<EditText>(R.id.username);
        val password = findViewById<EditText>(R.id.password);
        val event = findViewById<TextView>(R.id.event);
        val generate: Button = findViewById(R.id.generate);

        generate.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                event.text = PokerBros.login(username.text.toString(), password.text.toString());
            }
        })
    }
}