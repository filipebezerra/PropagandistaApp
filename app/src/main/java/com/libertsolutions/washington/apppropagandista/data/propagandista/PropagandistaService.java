package com.libertsolutions.washington.apppropagandista.data.propagandista;

import com.libertsolutions.washington.apppropagandista.domain.pojo.Propagandista;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Filipe Bezerra
 */
public interface PropagandistaService {
    @GET("PropagandistaApi/Get/")
    Observable<Propagandista> get(@Query("cpf") String cpf);
}
