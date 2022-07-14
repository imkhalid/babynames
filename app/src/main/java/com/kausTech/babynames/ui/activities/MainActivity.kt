package com.kausTech.babynames.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.WindowCompat
import androidx.work.impl.model.Preference
import com.kausTech.babynames.R
import com.kausTech.babynames.di.daggerViewModel
import com.kausTech.babynames.di.dashboard.DaggerComponentClass
import com.kausTech.babynames.di.dashboard.DashViewModel
import com.kausTech.babynames.ui.theme.BabyNamesTheme
import com.kausTech.babynames.util.MyApplication
import com.kausTech.babynames.util.PrefUtil
import com.kausTech.network.localDb.NamesDatabase

/**
 * Number of seconds to count down before showing the app open ad. This simulates the time needed
 * to load the app.
 */
private const val COUNTER_TIME = 3L

private const val LOG_TAG = "SplashActivity"

class MainActivity : ComponentActivity() {

    private var secondsRemaining: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabyNamesTheme {
                // A surface container using the 'background' color from the theme
                myySpalashScreen()
            }
        }

        hideSystemUI(window)

    }

    /**
     * Create the countdown timer, which counts down to zero and show the app open ad.
     *
     * @param seconds the number of seconds that the timer counts down from
     */
    @Composable
    private fun createTimer(seconds: Long) {
        val context = LocalContext.current
        val countDownTimer: CountDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000 + 1
            }

            override fun onFinish() {
                secondsRemaining = 0

                val application = application as? MyApplication

                // If the application is not an instance of MyApplication, log an error message and
                // start the MainActivity without showing the app open ad.
                if (application == null) {
                    Log.e(LOG_TAG, "Failed to cast application to MyApplication.")
                    return
                }

                // Show the app open ad.
                application.showAdIfAvailable(
                    this@MainActivity,
                    object : MyApplication.OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            val intent = Intent(context, MainScreen::class.java)
                            startActivity(context, intent, null)
                        }
                    })
            }
        }
        countDownTimer.start()
    }

    fun hideSystemUI(window: Window) {

        //Hide the status bars

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }


    @Preview(showBackground = true, widthDp = 520, heightDp = 1080)
    @Composable
    fun DefaultPreview() {
        BabyNamesTheme {
            myySpalashScreen()
        }
    }


    @Composable
    fun myySpalashScreen() {
        val fontFamily = FontFamily(
            Font(R.font.montserrat_regular, FontWeight.Normal),
            Font(R.font.montserrat_bold, FontWeight.SemiBold)
        )
        Box(
            modifier = Modifier.background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(id = R.color.startColor),
                        colorResource(id = R.color.endColor)
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo), contentDescription = "",
                        alignment = Alignment.Center
                    )
                    Text(
                        text = "Baby Name",
                        color = Color.White,
                        fontFamily = FontFamily.Cursive,
                        fontSize = 40.sp
                    )
                }
            }
            Text(
                text = "Making it Easier for You",
                color = colorResource(id = R.color.startColor),
                fontFamily = fontFamily,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            )
        }
        // Create a timer so the SplashActivity will be displayed for a fixed amount of time.
        NamesDatabase.getDatabase(this@MainActivity)
        val viewModel: DashViewModel = daggerViewModel {
            // option #2 create DI component and instantly get ViewModel instance
            DaggerComponentClass.builder()
                .build()
                .getViewModel()
        }

        if (PrefUtil.getPrefs(this).getBoolean("IS_DATA_SAVED", false).not())
            viewModel.writeNames {
                val intent = Intent(this@MainActivity, MainScreen::class.java)
                startActivity(this@MainActivity, intent, null)
            }
        else
            createTimer(COUNTER_TIME)
    }
}
