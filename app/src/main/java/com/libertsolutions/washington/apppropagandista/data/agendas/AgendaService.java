package com.libertsolutions.washington.apppropagandista.data.agendas;

import com.libertsolutions.washington.apppropagandista.domain.pojo.Agenda;
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
public interface AgendaService {
    @GET("AgendaApi/Get/")
    Observable<List<Agenda>> get(@Query("cpf") String cpf);

    @PUT("AgendaApi/Put/")
    Observable<Agenda> put(@Query("cpf") String cpf, @Body Agenda agenda);

    @POST("AgendaApi/Post/")
    Observable<Agenda> post(@Body Agenda agenda);
}
