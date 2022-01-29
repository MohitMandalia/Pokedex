package com.strings.pokedex.pokemondetail


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.textInputServiceFactory
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.strings.pokedex.data.remote.responses.Pokemon
import com.strings.pokedex.data.remote.responses.Type
import com.strings.pokedex.ui.theme.DarkThemedBlack
import com.strings.pokedex.util.Resource
import com.strings.pokedex.util.parseStatToAbbr
import com.strings.pokedex.util.parseStatToColor
import com.strings.pokedex.util.parseTypeToColor
import java.util.*
import kotlin.math.round


@Composable
fun PokemonDetailScreen(
    dominantColor: Color,
    pokemonName: String,
    navController: NavController,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 300.dp,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()){
        value = viewModel.getPokemonInfo(pokemonName)
    }.value

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = dominantColor
        )
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(DarkThemedBlack)
    ){
        Column {

            PokemonDetailTopSection(
                navController = navController,
                dominantColor = dominantColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.1f)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 30.dp,
                            bottomEnd = 30.dp
                        )
                    )
                    .background(dominantColor)
                    .fillMaxHeight(0.3f)
                    .padding(bottom = 16.dp)
                    .offset(y = (-40).dp)
            ) {
                if(pokemonInfo is Resource.Success){
                    pokemonInfo.data?.sprites?.let {
                        Image(
                            painter = rememberImagePainter(
                                data = it.front_default,
                                builder = {
                                    crossfade(true)
                                }
                            ),
                            contentDescription = pokemonInfo.data.name,
                            modifier = Modifier
                                .size(pokemonImageSize)
                                .offset(y = topPadding)
                        )
                    }
                }
            }
            PokemonDetailStateWrapper(
                modifier = Modifier
                    .fillMaxSize(),
                pokemonInfo = pokemonInfo,
                loadingModifier = Modifier
                    .size(100.dp)
                    .align(CenterHorizontally)
                    .padding(16.dp)
            )
        }
    }

}


@Composable
fun PokemonDetailTopSection(
    navController: NavController,
    modifier: Modifier = Modifier,
    dominantColor: Color
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(dominantColor)
    ){
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(20.dp, 20.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}


@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {

    when(pokemonInfo){
        is Resource.Success -> {
            PokemonDetailSection(
                pokemonInfo = pokemonInfo.data!!,
                modifier = modifier
                    .offset(y = (-20).dp)
            )
        }
        is Resource.Error -> {
            Text(
                text = pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier
            )
        }
        is Resource.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primarySurface,
                modifier = loadingModifier
            )
        }
    }
    
}

@Composable
fun PokemonDetailSection(
    pokemonInfo: Pokemon,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 40.dp)
            .verticalScroll(scrollState)
    ) {

        Text(
            text = pokemonInfo.name.capitalize(androidx.compose.ui.text.intl.Locale.current),
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        PokemonTypeSection(types = pokemonInfo.types)
        PokemonDetailDataSection(pokemonWeight = pokemonInfo.weight, pokemonHeight = pokemonInfo.height)
        PokemonBaseStats(pokemonInfo = pokemonInfo)
    }
}

@Composable
fun PokemonTypeSection(types: List<Type>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        for(type in types){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .background(parseTypeToColor(type))
                    .height(35.dp)
            ){
                Text(
                    text = type.type.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    },
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int,
    sectionHeight: Dp = 80.dp
) {
    val pokemonWeightInKg = remember{
        round(pokemonWeight * 100f) / 1000f
    }

    val pokemonHeightInMeters = remember {
        round(pokemonHeight * 100f) / 1000f
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        PokemonDetailDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "Kg",
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.size(1.dp,sectionHeight))
        PokemonDetailDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            modifier = Modifier.weight(1f)
        )

    }
}

@Composable
fun PokemonDetailDataItem(
    dataValue: Float,
    dataUnit: String,
    modifier: Modifier = Modifier
) {

    val textUnit = if(dataUnit == "m"){
        "Height"
    }else{
        "Weight"
    }

    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = "$dataValue ${dataUnit.capitalize(androidx.compose.ui.text.intl.Locale.current)}",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = textUnit,
            fontSize = 15.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PokemonStat(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    height: Dp = 25.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val curPercent = animateFloatAsState(
        targetValue =  if(animationPlayed){
            statValue / statMaxValue.toFloat()
        }else 0f,
        animationSpec = tween(
            animDuration,
            animDelay
        )
    )

    LaunchedEffect(key1 = true){
        animationPlayed = true
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(Color.White)
    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(curPercent.value)
                .clip(CircleShape)
                .background(statColor)
                .padding(horizontal = 8.dp)
        ) {
            Text(text = statName, fontWeight = FontWeight.Bold)
            Text(text = (curPercent.value * statMaxValue).toInt().toString(), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PokemonBaseStats(
    pokemonInfo: Pokemon,
    animDelayPerItem: Int = 100
) {
    val maxBaseStat = remember{
        pokemonInfo.stats.maxOf { it.base_stat }
    }
    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-20).dp)
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
    ) {
        Text(
            text = "Base Stats",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        for(i in pokemonInfo.stats.indices){
            val stat = pokemonInfo.stats[i]
            PokemonStat(
                statName = parseStatToAbbr(stat),
                statValue = stat.base_stat,
                statMaxValue = maxBaseStat,
                statColor = parseStatToColor(stat),
                animDelay = i * animDelayPerItem
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}