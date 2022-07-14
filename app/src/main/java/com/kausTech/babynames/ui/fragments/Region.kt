package com.kausTech.babynames.ui.fragments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.kausTech.babynames.R
import com.kausTech.babynames.di.dashboard.DashViewModel
import com.kausTech.babynames.ui.activities.MainScreen.Companion.showRegion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Region(navController: NavHostController, viewModel: DashViewModel) {
    showRegion = ""
    Surface() {
        LazyVerticalGrid(GridCells.Fixed(2)) {
            val span: (LazyGridItemSpanScope) -> GridItemSpan = { GridItemSpan(2) }
            item(span = span) {
                Text(
                    text = "Regions",
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, bottom = 10.dp),
                    fontSize = 20.sp,
                    fontFamily = family,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            viewModel.getRegions()
            items(viewModel.regionList.size) { index ->
                MyRegionColumn(name = viewModel.regionList[index]) {
                    showRegion = viewModel.regionList[index]
                    navController.navigate("dashboard/$showRegion")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class)
@Composable
fun MyRegionColumn(name: String, showAd: Boolean = false, onClick: () -> Unit) {

    Column {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(5.dp)
                .height(80.dp)
                .fillMaxWidth(),
            onClick = onClick
        ) {
            Column(Modifier.padding(12.dp)) {
                Row {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = name ?: "",
                            textAlign=TextAlign.Center,
                            fontSize = TextUnit(16f, type = TextUnitType.Sp),
                            fontFamily = family,
                            fontWeight = FontWeight.SemiBold
                        )

                    }

                }
            }

        }
        AnimatedVisibility(visible = showAd) {
            AdvertView()
        }
    }

}