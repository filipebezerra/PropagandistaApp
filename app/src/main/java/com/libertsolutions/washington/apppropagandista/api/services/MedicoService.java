package com.libertsolutions.washington.apppropagandista.api.services;

import com.libertsolutions.washington.apppropagandista.api.models.MedicoModel;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Interface de serviço para acessos aos recursos HTTP disponíveis na API de Medico do webservice.
 *
 * @author Washington, Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */
public interface MedicoService {
    @GET("MedicoApi/Get/")
    Observable<List<MedicoModel>> getByCpf(@Query("cpf") String cpf);

    @PUT("MedicoApi/Put/")
    Observable<MedicoModel> put(@Query("cpf") String cpf,@Body MedicoModel model);

    @POST("MedicoApi/Post/")
    Observable<MedicoModel> post(@Body MedicoModel model);
}
