@file:OptIn(ExperimentalUnitApi::class)

package com.kausTech.babynames.ui.fragments


import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.kausTech.babynames.R
import com.kausTech.babynames.di.dashboard.DaggerComponentClass
import com.kausTech.babynames.di.dashboard.DashViewModel
import com.kausTech.babynames.ui.activities.MainScreen
import com.kausTech.babynames.ui.activities.myNativeAd
import com.kausTech.babynames.ui.activities.refreshAd
import com.kausTech.babynames.ui.theme.BabyNamesTheme
import java.io.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    navController: NavHostController,
    dashboardViewModel: DashViewModel,
    showRegionButton: String = ""
) {
    val categories: List<String> = listOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )
    Surface(color = Color.White) {

        Column(Modifier.padding(start = 10.dp, end = 10.dp, top = 30.dp, bottom = 10.dp)) {

            val selected = rememberSaveable {
                mutableStateOf("")
            }

            val list = remember { mutableStateOf(ArrayList<Names>()) }
            LazyRow {
                items(items = categories) { name ->
                    MyCategory(
                        name,
                        selected.value,
                        onItemSelected = { selected.value = it })
                }
            }
            AnimatedVisibility(visible = showRegionButton.isEmpty()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Popular(
                            "Popular Names",
                            painterResource(id = R.drawable.flame),
                            onItemSelected = {
                            })
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Popular(
                            "Regions",
                            painterResource(id = R.drawable.growth),
                            onItemSelected = {
                                navController.navigate("region")
                            })
                    }
                }
            }
            if (showRegionButton.isEmpty())
                dashboardViewModel.getNames()
            else
                dashboardViewModel.getNamesByRegion(showRegionButton)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                dashboardViewModel.namesList.takeIf { x -> x.isNotEmpty() }?.let {
                    itemsIndexed(it) { index, item ->
                        if (showRegionButton.isEmpty() && index == it.lastIndex) {
                            dashboardViewModel.getNames()
                        }
                        MyColumn(item, index % 5 == 0)
                    }

                }

            }
        }

    }


}


@ExperimentalMaterial3Api
@Composable
fun Popular(
    name: String,
    painter: Painter = painterResource(id = R.drawable.flame),
    onItemSelected: (name: String) -> Unit
) {
    val family = FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_bold, FontWeight.SemiBold)
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            0.dp,
            Color.White
        ),
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        onClick = { onItemSelected(name) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            painter?.let {
                Image(
                    painter = it,
                    contentDescription = "",
                    Modifier.padding(start = 10.dp)
                )
            }
            Text(
                text = name,
                fontSize = 11.sp,
                fontFamily = family,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

val family = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_bold, FontWeight.SemiBold)
)

@ExperimentalMaterial3Api
private
@Composable
fun MyCategory(name: String, currentSelected: String, onItemSelected: (name: String) -> Unit) {


    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(5.dp),
        onClick = { onItemSelected(name) }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    color = if (currentSelected == name) MainScreen.colorSchema.primary else Color.Transparent
                )
                .size(50.dp)
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                fontFamily = family,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class)
@Composable
fun MyColumn(name: Names, showAd: Boolean = false) {

    val expanded = remember {
        mutableStateOf(false)
    }
    Column {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
        ) {
            Column(Modifier.padding(12.dp)) {
                Row {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = name.name,
                            fontSize = TextUnit(16f, type = TextUnitType.Sp),
                            fontFamily = family,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row {
                            Column {
                                Text(
                                    text = "Region:",
                                    fontSize = TextUnit(12f, type = TextUnitType.Sp),
                                    fontFamily = family,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                            Column {
                                Text(
                                    text = name.region,
                                    fontSize = TextUnit(12f, type = TextUnitType.Sp),
                                    fontFamily = family,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                    OutlinedButton(
                        onClick = { expanded.value = expanded.value.not() },
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = if (expanded.value) "Collapse" else "Expand",
                            fontSize = TextUnit(12f, type = TextUnitType.Sp),
                            color = MainScreen.colorSchema.inverseSurface
                        )
                    }
                }
                AnimatedVisibility(visible = expanded.value) {
                    Column {
                        Divider(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp)
                        )
                        val stat = remember {
                            mutableStateOf(false)
                        }
                        refreshAd(stat)
                        AnimatedVisibility(visible = stat.value) {
                            myNativeAd?.let {
                                populateNativeAdView(it) {
                                    stat.value = true
                                }

                            }
                        }
                        AnimatedVisibility(visible = stat.value) {
                            Divider(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 10.dp)
                            )
                        }
                        Row {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = " Gender",
                                    fontSize = TextUnit(12f, type = TextUnitType.Sp),
                                    fontFamily = family,
                                    fontWeight = FontWeight.Normal
                                )
                                Text(
                                    text = name.gender,
                                    fontSize = TextUnit(12f, type = TextUnitType.Sp),
                                    fontFamily = family,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = " Meaning",
                                    fontSize = TextUnit(12f, type = TextUnitType.Sp),
                                    fontFamily = family,
                                    fontWeight = FontWeight.Normal
                                )
                                Text(
                                    text = if (name.name_meaning.toLowerCase()
                                            .equals("unknown", true)
                                    ) "N/A" else name.name_meaning,
                                    fontSize = TextUnit(12f, type = TextUnitType.Sp),
                                    fontFamily = family,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                    }
                }
            }

        }
        AnimatedVisibility(visible = showAd) {
            AdvertView()
        }
    }

}

/**
 * Populates a [NativeAdView] object with data from a given [NativeAd].
 *
 * @param nativeAd the object containing the ad's assets
 * @param view the view to be populated
 */
@Composable
fun populateNativeAdView(nativeAd: NativeAd, ended: () -> Unit) {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            LayoutInflater.from(context).inflate(R.layout.ad_unified, null) as NativeAdView
        },
        update = { view ->
            view.mediaView = view.findViewById<View>(R.id.ad_media) as MediaView

            // Set other ad assets.
            view.headlineView = view.findViewById(R.id.ad_headline)
            view.bodyView = view.findViewById(R.id.ad_body)
            view.callToActionView = view.findViewById(R.id.ad_call_to_action)
            view.iconView = view.findViewById(R.id.ad_app_icon)
            view.priceView = view.findViewById(R.id.ad_price)
            view.starRatingView = view.findViewById(R.id.ad_stars)
            view.storeView = view.findViewById(R.id.ad_store)
            view.advertiserView = view.findViewById(R.id.ad_advertiser)
            // The headline and mediaContent are guaranteed to be in every NativeAd.
            (view.headlineView as TextView?)?.text = nativeAd.headline
            view.mediaView?.setMediaContent(nativeAd.mediaContent!!)
            // These assets aren't guaranteed to be in every NativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                view.bodyView?.visibility = View.INVISIBLE
            } else {
                view.bodyView?.visibility = View.VISIBLE
                (view.bodyView as TextView?)?.text = nativeAd.body
            }
            if (nativeAd.callToAction == null) {
                view.callToActionView?.visibility = View.INVISIBLE
            } else {
                view.callToActionView?.visibility = View.VISIBLE
                (view.callToActionView as Button?)?.text = nativeAd.callToAction
            }
            if (nativeAd.icon == null) {
                view.iconView?.visibility = View.GONE
            } else {
                (view.iconView as ImageView?)?.setImageDrawable(
                    nativeAd.icon?.drawable
                )
                view.iconView?.visibility = View.VISIBLE
            }
            if (nativeAd.price == null) {
                view.priceView?.visibility = View.INVISIBLE
            } else {
                view.priceView?.visibility = View.VISIBLE
                (view.priceView as TextView?)?.text = nativeAd.price
            }
            if (nativeAd.store == null) {
                view.storeView?.visibility = View.INVISIBLE
            } else {
                view.storeView?.visibility = View.VISIBLE
                (view.storeView as TextView?)?.text = nativeAd.store
            }
            if (nativeAd.starRating == null) {
                view.starRatingView?.visibility = View.INVISIBLE
            } else {
                (view.starRatingView as RatingBar?)?.rating = nativeAd.starRating!!.toFloat()
                view.starRatingView?.visibility = View.VISIBLE
            }
            if (nativeAd.advertiser == null) {
                view.advertiserView?.visibility = View.INVISIBLE
            } else {
                (view.advertiserView as TextView?)?.text = nativeAd.advertiser
                view.advertiserView?.visibility = View.VISIBLE
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            view.setNativeAd(nativeAd)

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            val vc = nativeAd.mediaContent!!.videoController

            // Updates the UI to say whether or not this ad has a video asset.
            if (vc.hasVideoContent()) {


                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.videoLifecycleCallbacks =
                    object : VideoController.VideoLifecycleCallbacks() {
                        override fun onVideoEnd() {
                            // Publishers should allow native ads to complete video playback before
                            // refreshing or replacing them with another ad in the same UI location.
                            ended()

                            super.onVideoEnd()
                        }
                    }
            } else {

            }
        })


}

@Composable
fun AdvertView(modifier: Modifier = Modifier) {
    val isInEditMode = LocalInspectionMode.current
    if (isInEditMode) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(horizontal = 2.dp, vertical = 6.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            text = "Advert Here",
        )
    } else {
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = "ca-app-pub-7238108829340450/3079945901"
//                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 520, heightDp = 1080)
@Composable
fun DefaultPreview() {
    BabyNamesTheme {
        Dashboard(rememberNavController(), DaggerComponentClass.builder().build().getViewModel())
    }

}