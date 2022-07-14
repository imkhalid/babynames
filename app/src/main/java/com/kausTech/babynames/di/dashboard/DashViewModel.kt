package com.kausTech.babynames.di.dashboard

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kausTech.babynames.ui.fragments.Names
import com.kausTech.babynames.util.PrefUtil
import com.kausTech.firebase.FirebaseCollections
import com.kausTech.network.DataManager
import com.kausTech.network.localDb.NamesDatabase
import com.kausTech.network.model.Regions
import com.xeontechnologies.autoassistant.firebase.FirebaseDB
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashViewModel @Inject constructor(private var dataManager: DataManager) : ViewModel() {
    var namesList: List<Names> by mutableStateOf(listOf())
    var regionList: List<String> by mutableStateOf(listOf())
    var isLoading by mutableStateOf(false)

    override fun onCleared() {
        super.onCleared()
        regionList = listOf()
        Log.i("COMPNAVILOG", "Screen1ViewModel.onCleared()")
    }


    fun writeNames(callback: () -> Unit) {

        if (isLoading.not()) {
            isLoading = true
            viewModelScope.launch {
                try {
//                namesList = dataManager.getNames().results
                    FirebaseDB(Names::class.java, FirebaseCollections.names).getAllList("name",
                        object :
                            FirebaseDB.GetAllListListener<Names> {
                            override fun onSuccess(list: List<Names>?) {
                                list?.let { names ->
                                    if (names.isNotEmpty()) {
                                        viewModelScope.launch {
                                            NamesDatabase.getDatabase().noteDao().insert(names)

                                        }
                                        PrefUtil.getPrefs().edit().putBoolean("IS_DATA_SAVED", true).apply()
                                        callback()
                                    }
                                }
                            }

                            override fun onNoData() {

                            }

                            override fun onFailure(reason: String?) {
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }

    fun getNames() {
        viewModelScope.launch {
            namesList = NamesDatabase.getDatabase().noteDao().getNames(1000)
        }
    }

    fun getNamesByRegion(region:String){
        viewModelScope.launch {
            namesList = NamesDatabase.getDatabase().noteDao().getNamesByRegion(region)
        }
    }

    fun getRegions() {
        viewModelScope.launch {
            regionList = NamesDatabase.getDatabase().noteDao().getRegions()
        }
    }

    fun writeRegions(callback:()->Unit) {
        viewModelScope.launch {
            try {
                FirebaseDB(
                    Regions::class.java,
                    FirebaseCollections.origins
                ).getAllList("origin_name", object :
                    FirebaseDB.GetAllListListener<Regions> {
                    override fun onSuccess(list: List<Regions>?) {
                        list?.let {regions->
                            viewModelScope.launch {
                                NamesDatabase.getDatabase().regionDao().insert(regions)

                            }
                            PrefUtil.getPrefs().edit().putBoolean("IS_DATA_SAVED", true).apply()
                            callback()
                        }
                    }

                    override fun onNoData() {
                        Log.d("TAG", "onNoData: ")
                    }

                    override fun onFailure(reason: String?) {
                        Log.d("TAG", "onFailure: $reason")
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}