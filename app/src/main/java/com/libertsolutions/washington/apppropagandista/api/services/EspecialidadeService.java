package com.libertsolutions.washington.apppropagandista.api.services;

import com.libertsolutions.washington.apppropagandista.api.models.EspecialidadeModel;
import java.util.List;
import retrofit.http.GET;
import rx.Observable;

/**
 * Interface de serviço para acessos aos recursos HTTP disponíveis na API de Especialidade do webservice.
 *
 * @author Washington, Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */
public interface EspecialidadeService {
    @GET("EspecialidadeApi/Get/")
    Observable<List<EspecialidadeModel>> get();
}
