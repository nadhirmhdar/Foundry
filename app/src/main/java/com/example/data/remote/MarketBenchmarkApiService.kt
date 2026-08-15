package com.example.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface MarketBenchmarkApiService {

    // Query Yahoo Finance live quotes for enterprise SaaS & Industrial automation proxies
    @GET
    suspend fun getLiveIndexQuotes(
        @Url url: String = "https://query1.finance.yahoo.com/v8/finance/chart/IGV?interval=1d&range=1d"
    ): Response<ResponseBody>

    // Query macroeconomic / tech sector live metrics
    @GET
    suspend fun getSectorMacroData(
        @Url url: String = "https://query1.finance.yahoo.com/v8/finance/chart/ROBO?interval=1d&range=1d"
    ): Response<ResponseBody>
}
