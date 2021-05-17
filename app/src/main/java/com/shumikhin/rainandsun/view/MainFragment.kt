package com.shumikhin.rainandsun.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shumikhin.rainandsun.R
import com.shumikhin.rainandsun.viewmodel.MainViewModel

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        //С помощью метода observe() мы можем подписаться на изменения в LiveData и получать
        //обновлённые данные каждый раз, когда вызван один из методов для передачи данных в эту LiveData
        viewModel.getData().observe(viewLifecycleOwner, Observer { renderData(it) })
    }

    // В качестве аргумента renderData
    //принимает объект, возвращаемый LiveData. Далее мы вызываем у созданной ViewModel метод
    //getData, который возвращает нам LiveData, и вызываем у LiveData метод observe, который и
    //передаём в жизненный цикл вместе с Observer’ом. Теперь, если данные, которые хранит LiveData,
    //изменятся, Observer сразу об этом узнает и вызовет метод renderData, в который передаст новые
    //данные.
    private fun renderData(data: Any) {
        Toast.makeText(context, "data", Toast.LENGTH_LONG).show()
    }

}