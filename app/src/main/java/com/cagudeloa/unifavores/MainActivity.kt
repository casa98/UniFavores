package com.cagudeloa.unifavores

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cagudeloa.unifavores.auth.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.header_layout.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.uid != null) {
            setContentView(R.layout.activity_main)
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)
            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            val navView: NavigationView = findViewById(R.id.nav_view)

            //TODO find the way to do it using some Architecture (OHHH, USING LIVEDATA AND DATABINBING DIRECTLY)
            val headerView = navView.getHeaderView(0)
            // Get user name and image (someday, why not) from model. Email from Firebase.
            val currentUser = FirebaseAuth.getInstance().currentUser!!
            headerView.email_header.text = currentUser.email
            FirebaseDatabase.getInstance().getReference(NODE_USERS).child(currentUser.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.child("username").value.toString()
                        headerView.name_header.text = username
                        val imageURL =
                            snapshot.child("image").value.toString()    // Load with Picasso
                        if (imageURL.isNotEmpty()) {
                            Picasso.get().load(imageURL).into(headerView.image_header)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })


            val navController = findNavController(R.id.nav_host_fragment)
            appBarConfiguration = AppBarConfiguration(
                // I only want a top-level destination, so I pass navController.graph instead of fragments ID
                navController.graph, drawerLayout
                /*setOf(
                    R.id.nav_home,
                    R.id.profileFragment,
                    R.id.myFavorsFragment,
                    R.id.incompletFragment,
                    R.id.chatsFragment,
                    R.id.statisticsFragment
                ), drawerLayout
                */
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}