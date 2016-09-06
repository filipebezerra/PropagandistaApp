package com.libertsolutions.washington.apppropagandista.data.medicos;

import com.libertsolutions.washington.apppropagandista.domain.pojo.Medico;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Filipe Bezerra
 */
public interface MedicoService {
    @GET("MedicoApi/Get/")
    Observable<List<Medico>> get(@Query("cpf") String cpf);

    @PUT("MedicoApi/Put/")
    Observable<Medico> put(@Query("cpf") String cpf, @Body Medico medico);

    @POST("MedicoApi/Post/")
    Observable<Medico> post(@Body Medico medico);
}
