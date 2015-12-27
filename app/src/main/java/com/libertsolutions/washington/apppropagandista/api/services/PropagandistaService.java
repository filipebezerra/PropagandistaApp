package com.libertsolutions.washington.apppropagandista.api.services;

import com.libertsolutions.washington.apppropagandista.api.models.PropagandistaModel;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Classe de serviço para acessos aos recursos HTTP disponíveis na
 * API de Propagandista do webservice.
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */
public interface PropagandistaService {
    @GET("PropagandistaApi/Get/")
    Observable<PropagandistaModel> getByCpf(@Query("cpf") String cpf);
}
