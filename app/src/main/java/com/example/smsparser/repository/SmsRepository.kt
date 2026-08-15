package com.example.smsparser.repository

import android.content.Context
import com.example.smsparser.model.ParsedResult
import com.example.smsparser.parser.SmsParser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SmsRepository(
    private val context: Context,
    private val parser: SmsParser = SmsParser()
) {

    private val gson = Gson()

    private val _results =
        MutableStateFlow<List<ParsedResult>>(emptyList())

    val results: StateFlow<List<ParsedResult>> =
        _results.asStateFlow()

    fun loadSamples() {

        val json = context.assets
            .open("samples.json")
            .bufferedReader()
            .use { it.readText() }

        val type =
            object : TypeToken<List<SmsSample>>() {}.type

        val samples: List<SmsSample> =
            gson.fromJson(json, type)

        _results.value = samples.map { sample ->
            parser.parse(
                sms = sample.sms,
                id = sample.id
            )
        }
    }

    private data class SmsSample(
        val id: Int,
        val sms: String
    )
}