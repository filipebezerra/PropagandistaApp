package com.libertsolutions.washington.apppropagandista.api.services;

import com.libertsolutions.washington.apppropagandista.Model.Propagandista;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Classe de serviço para acessos aos recursos HTTP disponíveis na
 * API de Propagandista do webservice.
 *
 * @author Filipe Bezerra
 * @version #, 02/12/2015
 * @since #
 * @see com.libertsolutions.washington.apppropagandista.api.controller.RetrofitController
 */
public interface PropagandistaService {
    @GET("PropagandistaApi/Get/")
    Observable<Propagandista> getByCpf(@Query("cpf") String cpf);
}
