package com.libertsolutions.washington.apppropagandista.api;

import com.libertsolutions.washington.apppropagandista.Model.Agenda;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by washington on 20/12/2015.
 */
public interface AgendaService {
    @GET("AgendaApi/Get/")
    Observable<List<Agenda>> getByCpf(@Query("cpf") String cpf);

    @PUT("AgendaApi/Put/")
    Observable<Integer> put(@Query("cpf") String cpf,@Body Agenda agenda);

    @PUT("AgendaApi/Post/")
    Observable<Integer> post(@Body Agenda agenda);
}
