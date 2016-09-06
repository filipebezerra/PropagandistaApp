package com.libertsolutions.washington.apppropagandista.data.especialidades;

import com.libertsolutions.washington.apppropagandista.domain.pojo.Especialidade;
import java.util.List;
import retrofit2.http.GET;
import rx.Observable;

/**
 * @author Filipe Bezerra
 */
public interface EspecialidadeService {
    @GET("EspecialidadeApi/Get/")
    Observable<List<Especialidade>> get();
}
