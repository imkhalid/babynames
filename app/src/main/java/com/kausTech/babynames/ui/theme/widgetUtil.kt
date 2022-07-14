package com.kausTech.babynames.ui.theme

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.kausTech.babynames.R

@Composable
fun mySpalashScreen(){
    Surface(color = Color.Red) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
               Image(painter = painterResource( R.mipmap.ic_launcher) , contentDescription = "",
               alignment = Alignment.Center)
            }
        }
    }
}