package com.libertsolutions.washington.apppropagandista.api;

import com.libertsolutions.washington.apppropagandista.Model.Especialidade;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by washington on 21/12/2015.
 */
public interface EspecialidadeService {
    @GET("EspecialidadeApi/Get/")
    Observable<List<Especialidade>> get();
}
