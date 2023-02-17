package com.example.todofinal


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(application:Application):ViewModel() {
    private val db=RoomSingleton.getInstance(application)

    internal val todoList:LiveData<MutableList<ToDo>> = db.todoDao().getTodos()

    fun insert(todo: ToDo){
        viewModelScope.launch(Dispatchers.IO) {
            db.todoDao().insert(todo)
        }
    }

    fun update(todo: ToDo){
        viewModelScope.launch(Dispatchers.IO) {
            db.todoDao().update(todo)
        }
    }

    fun delete(todo: ToDo){
        viewModelScope.launch(Dispatchers.IO) {
            db.todoDao().delete(todo)
        }
    }

    fun clear(){
        viewModelScope.launch(Dispatchers.IO) {
            db.todoDao().clear()
        }
    }

}
class ToDoViewmodelFactory(private val application: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            return TodoViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}