package com.libertsolutions.washington.apppropagandista.api.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Classe modelo para envio para e recebimento de dados do webservice. <br><br>
 *
 * A fonte dos dados enviados são obtidas a partir da entidade
 * {@link com.libertsolutions.washington.apppropagandista.Model.Medico}. <br><br>
 *
 * Os dados recebidos do webservice são
 *
 *
 * @author Filipe Bezerra
 * @version 0.1.0, 24/12/2015
 * @since 0.1.0
 */
public class MedicoModel {
    @SerializedName("id_medico")
    public int idMedico;

    @SerializedName("id_cliente")
    public int idCliente;

    public String nome;

    public String dataAniversario;

    public String secretaria;

    public String telefone;

    public String email;

    public String crm;

    @SerializedName("id_especialidade")
    public int idEspecialidade;

    @SerializedName("Endereco")
    public List<EnderecoModel> enderecos;

    public static class EnderecoModel {
        @SerializedName("id_endereco")
        public int idEndereco;

        @SerializedName("id_cliente")
        public int idCliente;

        public String endereco;

        public String cep;

        public String numero;

        public String bairro;

        public String complemento;

        public String longitude;

        public String latitude;

        public String obs;

        @SerializedName("id_medico")
        public int idMedico;
    }
}
