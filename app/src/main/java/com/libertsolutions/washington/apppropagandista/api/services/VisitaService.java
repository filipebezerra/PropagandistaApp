package com.libertsolutions.washington.apppropagandista.api.services;

import com.libertsolutions.washington.apppropagandista.api.models.VisitaModel;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by washington on 01/01/2016.
 */
public interface VisitaService {
    @GET("VisitaApi/Get/")
    Observable<List<VisitaModel>> getByCpf(@Query("cpf") String cpf);

    @PUT("VisitaApi/Put/")
    Observable<VisitaModel> put(@Body VisitaModel model);

    @POST("VisitaApi/Post/")
    Observable<VisitaModel> post(@Body VisitaModel model);
}
