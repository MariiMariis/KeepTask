package br.infnet.dev.keeptask.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import br.infnet.dev.keeptask.api.CatJson

interface ApiRequests {
    @GET("/facts/random")
    fun getCatFacts() : Call<CatJson>

}