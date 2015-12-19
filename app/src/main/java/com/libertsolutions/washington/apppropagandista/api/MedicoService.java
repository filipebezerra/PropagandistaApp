package com.libertsolutions.washington.apppropagandista.api;

import com.libertsolutions.washington.apppropagandista.Model.Medico;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by washington on 14/12/2015.
 */
public interface MedicoService {
    @GET("MedicoApi/Get/")
    Observable<List<Medico>> getByCpf(@Query("cpf") String cpf);

    @PUT("MedicoApi/Put/")
    Observable<Integer> put(@Query("cpf") String cpf,@Body Medico medico);

    @PUT("MedicoApi/Post/")
    Observable<Integer> post(@Body Medico medico);
}
