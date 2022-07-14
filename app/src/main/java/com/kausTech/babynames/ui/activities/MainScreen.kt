package com.kausTech.babynames.ui.activities

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.FirebaseApp
import com.kausTech.babynames.di.daggerViewModel
import com.kausTech.babynames.di.dashboard.DaggerComponentClass
import com.kausTech.babynames.di.dashboard.DashViewModel
import com.kausTech.babynames.ui.fragments.Dashboard
import com.kausTech.babynames.ui.fragments.Region
import com.kausTech.babynames.ui.theme.BabyNamesTheme


class MainScreen : ComponentActivity() {

    companion object{
        var showRegion =""
        lateinit var colorSchema:ColorScheme
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            BabyNamesTheme {
                val navController = rememberNavController()
                // A surface container using the 'background' color from the theme
                MyApp(navController)
            }
        }

        MobileAds.initialize(this) {}
        hideSystemUI(window)
    }


    fun hideSystemUI(window: Window) {

        //Hide the status bars

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyApp(navController: NavHostController) {
        val region=""
        val start = "dashboard"
        Column() {

            Box {
                NavHost(
                    navController = navController,
                    startDestination = start,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {
                    composable("dashboard"){ backstack->
                        val viewModel: DashViewModel = daggerViewModel {
                            // option #2 create DI component and instantly get ViewModel instance
                            DaggerComponentClass.builder()
                                .build()
                                .getViewModel()
                        }
                        Dashboard(navController,viewModel,"")
                    }

                    composable("dashboard/{region}",
                    listOf(navArgument("region"){defaultValue=""} )){ backstack->
                        val viewModel: DashViewModel = daggerViewModel {
                            // option #2 create DI component and instantly get ViewModel instance
                            DaggerComponentClass.builder()
                                .build()
                                .getViewModel()
                        }
                        Dashboard(navController,viewModel,backstack.arguments?.getString("region")?:"")
                    }
                    composable("region") {
                        val viewModel: DashViewModel = daggerViewModel {
                            // option #2 create DI component and instantly get ViewModel instance
                            DaggerComponentClass.builder()
                                .build()
                                .getViewModel()
                        }
                        Region(navController,viewModel)
                    }
                }
            }


        }


    }



    @Preview(showBackground = true, widthDp = 520, heightDp = 1080)
    @Composable
    fun DefaultPreview() {
        BabyNamesTheme {
            MyApp(rememberNavController())
        }
    }


    override fun onDestroy() {
        myNativeAd?.destroy()
        super.onDestroy()
    }


}
var myNativeAd: NativeAd? = null
@Composable
fun refreshAd(mutable: MutableState<Boolean>) {

    val adId ="ca-app-pub-7238108829340450/5471139673"
//    val adId="ca-app-pub-3940256099942544/1044960115" //   test ad id (video)
    val builder = AdLoader.Builder(LocalContext.current, adId)
    builder.forNativeAd(
        NativeAd.OnNativeAdLoadedListener { nativeAd ->
            // OnLoadedListener implementation.
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.

            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
//            if (myNativeAd != null) {
//                myNativeAd?.destroy()
//            }
            myNativeAd = nativeAd
            mutable.value=true
        })
    val videoOptions =
        VideoOptions.Builder().setStartMuted(true).build()
    val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
    builder.withNativeAdOptions(adOptions)
    val adLoader = builder
        .withAdListener(
            object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                    val error = String.format(
                        "domain: %s, code: %d, message: %s",
                        loadAdError.domain,
                        loadAdError.code,
                        loadAdError.message
                    )
                }
            })
        .build()
    adLoader.loadAd(AdRequest.Builder().build())
}

