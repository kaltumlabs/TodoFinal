package com.example.todofinal

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todofinal.ui.theme.TodoFinalTheme
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoFinalTheme {

                Surface(color = MaterialTheme.colors.background) {
                    GetScaffold()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview
@Composable
fun GetScaffold() {
    val scaffoldState: ScaffoldState = rememberScaffoldState(
        snackbarHostState = SnackbarHostState()
    )
    Scaffold(
        scaffoldState = scaffoldState,
        content = { MainContent(scaffoldState) },
        backgroundColor = Color(0xFFCEF5F0),
    )
}


@Composable
fun MainContent(scaffoldState: ScaffoldState) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val model: TodoViewModel = viewModel(
        factory = ToDoViewmodelFactory(
            context.applicationContext as Application
        )
    )
    val list: List<ToDo> = model.todoList.observeAsState(listOf()).value
    var textState = remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        //contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),

                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFFFFFFF),
                    focusedIndicatorColor = Color.Transparent, //hide the indicator
                ),
                value = textState.value, onValueChange = { textState.value = it },
                placeholder = {
                    Text(text = "Enter Your  Notes guys!")
                },
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(



                    onClick = {
                        if (textState.value.trim().isNotEmpty()) {
                            model.insert(
                                ToDo(
                                    null,
                                    UUID.randomUUID().toString(),
                                    textState.value
                                )
                            )
                        } else {
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = "please enter a note",
                                )
                            }


                        }
                        scope.launch {
                            textState.value = ""
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = "Notes added",
                            )
                        }
                    },) {
                    Text(text = "Add Notes", style = TextStyle(color = Color.White, ),)
                }
                Button(onClick = {
                    model.clear()
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = "All Notes deleted",
                        )
                    }

                }) {
                    Text(text = "Clear",style = TextStyle(color = Color.White, ))
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(list.size) { index ->
                    Card(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.CenterVertically)
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text =  list[index].notes,
                                style = TextStyle(
                                    color = if (list[index].id!! <= 33)
                                        Color(0xFF000000)
                                    else Color(0xFF97AEB5)
                                ),
                                modifier = Modifier.weight(2F).padding(start = 4.dp)
                            )

                            IconButton(onClick = {
                                list[index].notes = textState.value
                                model.update(list[index])
                                scope.launch {
                                    scaffoldState.snackbarHostState
                                        .showSnackbar(
                                            "Notes updated id" +
                                                    " : ${list[index].id}",
                                        )
                                    textState.value = ""
                                }
                            }) {
                                Icon(Icons.Filled.Edit, "", tint = Black)
                            }

                            IconButton(onClick = {
                                model.delete(list[index])
                                scope.launch {
                                    scaffoldState.snackbarHostState
                                        .showSnackbar(
                                            "Notes deleted id" +
                                                    " : ${list[index].id}",
                                        )
                                    textState.value = ""
                                }

                            }) {
                                Icon(Icons.Filled.Delete, "", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}