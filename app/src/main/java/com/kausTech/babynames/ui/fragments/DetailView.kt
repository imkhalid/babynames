package com.kausTech.babynames.ui.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kausTech.babynames.ui.theme.BabyNamesTheme

@Composable
fun DetailView(id:String){
    Column(Modifier.padding(10.dp).background(
        color = dynamicLightColorScheme(LocalContext.current).background
    )) {

    }
}

@Preview(showBackground = true, widthDp = 520, heightDp = 1080)
@Composable
fun DefaultsPreview() {
    BabyNamesTheme {
        DetailView("1")
    }
}