package com.example.weatherapp.ui.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.ui.Appbar

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    searchLocation: (String?) -> Unit,
    getCurrentLocation: () -> Unit,
    selectUnitSystem: (UnitSystem) -> Unit,
    navigateToSavedLocations: () -> Unit
) {
    Scaffold(topBar = {
        Appbar(
            title = stringResource(id = R.string.search),
            menuItems = {
                IconButton(onClick = {
                    navigateToSavedLocations()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_favorites_list), "",
                        tint = Color.White
                    )
                }
            }
        )
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = colorResource(id = R.color.jordi_blue)
        ) {
            Column(modifier = Modifier.padding(top = 50.dp)) {
                val unitSystemText =
                    remember { mutableStateOf(UnitSystem.METRIC.name.capitalizeFirst()) }
                TextSearchScreen(text = unitSystemText.value, TextAlign.Center, 26.sp)

                UnitsRadioGroup(
                    selectUnitSystem
                ) { newUnitText ->
                    unitSystemText.value =
                        newUnitText.capitalizeFirst()
                }

                var locationNameText by remember { mutableStateOf(TextFieldValue("")) }
                TextField(
                    value = locationNameText,
                    label = { Text(text = stringResource(id = R.string.city)) },
                    onValueChange = { newInput ->
                        locationNameText = newInput
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = TextFieldDefaults.OutlinedTextFieldShape,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                )

                ButtonSearchScreen(
                    { searchLocation(locationNameText.text) },
                    stringResource(R.string.search)
                )

                TextSearchScreen(
                    stringResource(R.string.or),
                    textAlign = TextAlign.Center
                )

                ButtonSearchScreen(
                    getCurrentLocation,
                    stringResource(R.string.get_my_location)
                )

                val recentLocationsList = viewModel.cityNames.collectAsState()

                if (recentLocationsList.value.isNotEmpty()) {
                    TextSearchScreen(
                        stringResource(R.string.recently_searched_locations),
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp,
                    )
                }

                LazyColumn {
                    items(recentLocationsList.value) { recentLocation ->
                        RecentLocationRow(recentLocation) { selectedLocation ->
                            searchLocation(selectedLocation)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnitsRadioGroup(
    selectUnits: (UnitSystem) -> Unit,
    setNewUnitText: (String) -> Unit
) {
    val unitsList = listOf(UnitSystem.METRIC, UnitSystem.STANDARD, UnitSystem.IMPERIAL)
    var selectedValue by remember { mutableStateOf(unitsList[0]) }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp)
    ) {
        unitsList.forEach { unit ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .selectable(
                        selected = (selectedValue == unit),
                        onClick = {
                            selectedValue = unit
                        },
                        role = Role.RadioButton
                    )
                    .padding(8.dp)
            ) {
                IconToggleButton(
                    checked = selectedValue == unit,
                    onCheckedChange = {
                        selectedValue = unit
                        setNewUnitText(unit.name)
                        selectUnits(unit)
                    },
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(70.dp), onDraw = {
                            drawCircle(
                                color = if (selectedValue == unit) {
                                    Color.Blue
                                } else {
                                    Color.LightGray
                                }
                            )
                        })
                        Text(
                            text = getUnitsFormat(unit),
                            color = Color.White,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getUnitsFormat(unit: UnitSystem) = when (unit) {
    UnitSystem.METRIC -> stringResource(R.string.celsius_temperature_format)
    UnitSystem.STANDARD -> stringResource(R.string.standard_temperature_format)
    UnitSystem.IMPERIAL -> stringResource(R.string.imperial_temperature_format)
}

@Composable
private fun ButtonSearchScreen(
    action: () -> Unit,
    btnText: String
) {
    Button(
        onClick = action,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        Text(text = btnText)
    }
}

@Composable
private fun TextSearchScreen(
    text: String,
    textAlign: TextAlign,
    fontSize: TextUnit = 16.sp,
) {
    Text(
        text = text,
        color = Color.White,
        textAlign = textAlign,
        fontSize = fontSize,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                top = 10.dp
            )
    )
}

@Composable
fun RecentLocationRow(city: City, selectLocation: (String) -> Unit) {
    Text(
        text = city.cityName,
        fontSize = 28.sp,
        color = Color.White,
        modifier = Modifier
            .padding(
                start = 20.dp,
                top = 10.dp
            )
            .fillMaxWidth()
            .clickable { selectLocation(city.cityName) }
    )
}

fun String.capitalizeFirst(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}