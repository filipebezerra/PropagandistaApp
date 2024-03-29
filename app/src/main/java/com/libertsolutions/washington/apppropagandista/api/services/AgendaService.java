package com.libertsolutions.washington.apppropagandista.api.services;

import com.libertsolutions.washington.apppropagandista.api.models.AgendaModel;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Interface de serviço para acessos aos recursos HTTP disponíveis na API de Agenda do webservice.
 *
 * @author Washington, Filipe Bezerra
 * @version 1.0, 20/12/2015
 * @since 1.0
 */
public interface AgendaService {
    @GET("AgendaApi/Get/")
    Observable<List<AgendaModel>> getByCpf(@Query("cpf") String cpf);

    @PUT("AgendaApi/Put/")
    Observable<AgendaModel> put(@Query("cpf") String cpf,@Body AgendaModel model);

    @POST("AgendaApi/Post/")
    Observable<AgendaModel> post(@Body AgendaModel model);
}
