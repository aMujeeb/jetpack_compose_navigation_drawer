package com.mujapps.navicomposedrawer

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mujapps.navicomposedrawer.model.NavigationItem
import com.mujapps.navicomposedrawer.ui.theme.NaviComposeDrawerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val mViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            //Operations during splash screen.. Even network calls like token validation
            //This Lambda function will keep splash screen alive until background functions are done
            setKeepOnScreenCondition {
                !mViewModel.mIsReady.value    //Until False will hang
            }
            //To add animation as old XML fashion
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.4f,
                    0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 500L
                zoomX.doOnEnd {
                    screen.remove()
                }

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.4f,
                    0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 500L
                zoomY.doOnEnd {
                    screen.remove()
                }

                zoomX.start()
                zoomY.start()
            }
        }
        setContent {
            NaviComposeDrawerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    DrawerM()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerM() {
    val menuItems = listOf(
        NavigationItem(
            title = "All",
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home
        ),
        NavigationItem(
            title = "Urgent",
            selectedIcon = Icons.Filled.Info,
            unSelectedIcon = Icons.Outlined.Info,
            badgeCount = 34
        ),
        NavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unSelectedIcon = Icons.Outlined.Settings
        )
    )
    val mDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val mCoroutineScope = rememberCoroutineScope() // This is needed to handle suspended functions. Drawer open() close() are suspend functions
    var mSelectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    //Permenent Navigation Drawer for Tab layouts. Landscape
    //Dismissible navigation Drawer -> Can dismiss when needed

    ModalNavigationDrawer(drawerContent = {
        ModalDrawerSheet {
            Spacer(modifier = Modifier.height(16.dp))
            menuItems.forEachIndexed { index, item ->
                NavigationDrawerItem(label = { Text(text = item.title) }, selected = index == mSelectedItemIndex, onClick = {
                    mSelectedItemIndex = index
                    mCoroutineScope.launch { mDrawerState.close() }
                    //*******Nav controller to navigate to any view *********\\\
                }, icon = {
                    Icon(
                        imageVector = if (index == mSelectedItemIndex) {
                            item.selectedIcon
                        } else {
                            item.unSelectedIcon
                        }, contentDescription = item.title
                    )
                }, badge = {
                    item.badgeCount?.let {
                        Text(text = item.badgeCount.toString())
                    }
                }, modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
            }
        }
    }, drawerState = mDrawerState) {
        Scaffold(topBar = {
            TopAppBar(title = {
                Text(text = "Title Of Drawer")
            }, navigationIcon = {
                IconButton(onClick = { mCoroutineScope.launch { mDrawerState.open() } }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                }
            })
        }) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DrawerMPreview() {
    NaviComposeDrawerTheme {
        DrawerM()
    }
}