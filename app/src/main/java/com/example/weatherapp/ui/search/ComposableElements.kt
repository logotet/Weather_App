package com.example.weatherapp.ui.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.measure.UnitSystem

//todo extract values and set theme
@Composable
fun UnitsRadioGroup(
    modifier: Modifier = Modifier,
    selectUnits: (UnitSystem) -> Unit,
    getNewUnitText: (String) -> Unit
) {
    val unitsList = listOf(UnitSystem.METRIC, UnitSystem.STANDARD, UnitSystem.IMPERIAL)
    var selectedValue by remember { mutableStateOf(unitsList[0]) }

    val colorBlue = colorResource(R.color.royal_blue)
    val colorGrey = colorResource(R.color.dusty_grey)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        unitsList.forEach { unit ->
            Box(
                modifier = Modifier
                    .selectable(
                        selected = (selectedValue == unit),
                        onClick = {
                            selectedValue = unit
                            getNewUnitText(unit.name)
                            selectUnits(unit)
                        },
                        role = Role.RadioButton
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(70.dp), onDraw = {
                    drawCircle(
                        color = if (selectedValue == unit) {
                            colorBlue
                        } else {
                            colorGrey
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

@Preview
@Composable
fun UnitsRadioGroupPreview() {
    UnitsRadioGroup(
        selectUnits = { },
        getNewUnitText = { }
    )
}

@Composable
private fun getUnitsFormat(unit: UnitSystem) = when (unit) {
    UnitSystem.METRIC -> stringResource(R.string.celsius_temperature_format)
    UnitSystem.STANDARD -> stringResource(R.string.standard_temperature_format)
    UnitSystem.IMPERIAL -> stringResource(R.string.imperial_temperature_format)
}

@Composable
fun ButtonWide(
    modifier: Modifier = Modifier,
    action: () -> Unit,
    buttonText: String
) {
    val colorBlue = colorResource(id = R.color.royal_blue)

    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 10.dp,
                horizontal = 20.dp
            )
            .height(46.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorBlue,
            contentColor = Color.White
        ),
        onClick = action,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(text = buttonText)
    }
}

@Preview
@Composable
fun ButtonWidePreview() {
    ButtonWide(action = {}, buttonText = "Search")
}

@Composable
fun TextSearchScreen(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign,
    fontSize: TextUnit = 16.sp,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                top = 10.dp
            ),
        text = text,
        color = Color.White,
        textAlign = textAlign,
        fontSize = fontSize
    )
}

@Preview
@Composable
fun TextSearchScreenPreview() {
    TextSearchScreen(text = "Metric", textAlign = TextAlign.Start)
}

@Composable
fun RecentLocationRow(
    modifier: Modifier = Modifier,
    city: City,
    selectLocation: (String) -> Unit
) {
    Text(
        modifier = modifier
            .padding(
                start = 20.dp,
                top = 10.dp
            )
            .fillMaxWidth()
            .clickable { selectLocation(city.cityName) },
        text = city.cityName,
        fontSize = 24.sp,
        color = Color.White
    )
}

@Preview
@Composable
fun RecentLocationRowPreview() {
    RecentLocationRow(
        city = City("Sofia", 1L),
        selectLocation = {})
}