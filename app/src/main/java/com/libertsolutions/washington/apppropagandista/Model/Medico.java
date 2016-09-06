package com.libertsolutions.washington.apppropagandista.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.libertsolutions.washington.apppropagandista.api.models.MedicoModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;

/**
 * Classe modelo dos dados do médico.
 *
 * @author Washington, Filipe Bezerra
 * @version 0.1.0, 26/12/2015
 * @since 0.1.0
 */
public class Medico extends ModeloBase<Medico> implements Parcelable {
    // id do médico no servidor
    Integer mIdMedico;

    String mNome;

    // data de aniversário, armazenado como milessegundos
    Long mDataAniversario;

    String mSecretaria;

    String mTelefone;

    String mEmail;

    String mCrm;

    // id de relacionamento com tabela Especialidade
    Integer mIdEspecialidade;

    public Medico() {
    }

    protected Medico(Parcel in) {
        boolean mIdNullHelper;
        mIdNullHelper = (in.readByte() == 1 );
        if (mIdNullHelper) {
            mId = in.readLong();
        } else {
            mId = null;
        }

        mStatus = (Status) in.readSerializable();

        boolean mIdMedicoNullHelper;
        mIdMedicoNullHelper = (in.readByte() == 1 );
        if (mIdMedicoNullHelper) {
            mIdMedico = in.readInt();
        } else {
            mIdMedico = null;
        }

        mNome = in.readString();

        boolean mDataAniversarioNullHelper;
        mDataAniversarioNullHelper = (in.readByte() == 1 );
        if (mDataAniversarioNullHelper) {
            mDataAniversario = in.readLong();
        } else {
            mDataAniversario = null;
        }

        mSecretaria = in.readString();

        mTelefone = in.readString();

        mEmail = in.readString();

        mCrm = in.readString();

        boolean mIdEspecialidadeNullHelper;
        mIdEspecialidadeNullHelper = (in.readByte() == 1 );
        if (mIdEspecialidadeNullHelper) {
            mIdEspecialidade = in.readInt();
        } else {
            mIdEspecialidade = null;
        }
    }

    public static final Creator<Medico> CREATOR = new Creator<Medico>() {
        @Override
        public Medico createFromParcel(Parcel in) {
            return new Medico(in);
        }

        @Override
        public Medico[] newArray(int size) {
            return new Medico[size];
        }
    };

    /**
     * Obtém os dados da agenda importados do webservice.
     *
     * @param model o modelo de dados da agenda recuperado do webservice.
     * @return a agenda.
     */
    public static Medico fromModel(@NonNull MedicoModel model) {
        Preconditions.checkNotNull(model, "model não pode ser nulo");
        Preconditions.checkNotNull(model.idMedico, "model.idMedico não pode ser nulo");
        Preconditions.checkNotNull(model.nome, "model.nome não pode ser nulo");
        Preconditions.checkNotNull(model.telefone, "model.telefone não pode ser nulo");
        Preconditions.checkState(model.idEspecialidade != 0, "model.idEspecialidade é inválido");

        return new Medico()
                .setId(model.idCliente)
                .setIdMedico(model.idMedico)
                .setNome(model.nome)
                .setDataAniversario(model.dataAniversario != null ?
                        DateTime.parse(model.dataAniversario).getMillis() : null)
                .setSecretaria(model.secretaria)
                .setTelefone(model.telefone)
                .setEmail(model.email)
                .setCrm(model.crm)
                .setIdEspecialidade(model.idEspecialidade);
    }

    public static MedicoModel toModel(@NonNull Medico medico, @Nullable List<Endereco> enderecos) {
        Preconditions.checkNotNull(medico, "medico não pode ser nulo");
        Preconditions.checkNotNull(medico.getId(),
                "medico.getId() não pode ser nulo");
        Preconditions.checkNotNull(medico.getIdMedico(),
                "medico.getIdMedico() não pode ser nulo");
        Preconditions.checkNotNull(medico.getNome(),
                "medico.getNome() não pode ser nulo");
        Preconditions.checkNotNull(medico.getTelefone(),
                "medico.getTelefone() não pode ser nulo");
        Preconditions.checkNotNull(medico.getIdEspecialidade(),
                "medico.getIdEspecialidade() não pode ser nulo");

        final MedicoModel model = new MedicoModel();
        model.idCliente = medico.getId();
        model.idMedico = medico.getIdMedico();
        model.nome = medico.getNome();
        model.dataAniversario = medico.getDataAniversario() != null ?
                new DateTime(medico.getDataAniversario()).toString("yyyy-MM-dd'T'HH:mm:ss") :
                null;
        model.secretaria = medico.getSecretaria();
        model.telefone = medico.getTelefone();
        model.email = medico.getEmail();
        model.crm = medico.getCrm();
        model.idEspecialidade = medico.getIdEspecialidade();

        if (enderecos == null || enderecos.isEmpty()) {
            model.enderecos = Collections.emptyList();
        } else {
            model.enderecos = new ArrayList<>();

            for (Endereco endereco : enderecos) {
                model.enderecos.add(Endereco.toModel(endereco));
            }
        }

        return model;
    }

    @Override
    public Medico setId(Long id) {
        mId = id;
        return this;
    }

    @Override
    public Medico setStatus(Status status) {
        mStatus = status;
        return this;
    }

    public Integer getIdMedico() {
        return mIdMedico;
    }

    public Medico setIdMedico(Integer idMedico) {
        mIdMedico = idMedico;
        return this;
    }

    public String getNome() {
        return mNome;
    }

    public Medico setNome(String nome) {
        this.mNome = nome;
        return this;
    }

    public Long getDataAniversario() {
        return mDataAniversario;
    }

    public Medico setDataAniversario(Long dataAniversario) {
        mDataAniversario = dataAniversario;
        return this;
    }

    public String getSecretaria() {
        return mSecretaria;
    }

    public Medico setSecretaria(String secretaria) {
        this.mSecretaria = secretaria;
        return this;
    }

    public String getTelefone() {
        return mTelefone;
    }

    public Medico setTelefone(String telefone) {
        this.mTelefone = telefone;
        return this;
    }

    public String getEmail() {
        return mEmail;
    }

    public Medico setEmail(String email) {
        this.mEmail = email;
        return this;
    }

    public String getCrm() {
        return mCrm;
    }

    public Medico setCrm(String crm) {
        this.mCrm = crm;
        return this;
    }

    public Integer getIdEspecialidade() {
        return mIdEspecialidade;
    }

    public Medico setIdEspecialidade(Integer idEspecialidade) {
        mIdEspecialidade = idEspecialidade;
        return this;
    }

    @Override
    public String toString() {
        return getNome();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeByte( (byte) (mId != null ? 1 : 0) );
        if (mId != null) {
            out.writeLong(mId);
        }

        out.writeSerializable(mStatus);

        out.writeByte( (byte) (mIdMedico != null ? 1 : 0) );
        if (mIdMedico != null) {
            out.writeInt(mIdMedico);
        }

        out.writeString(mNome);

        out.writeByte( (byte) (mDataAniversario != null ? 1 : 0) );
        if (mDataAniversario != null) {
            out.writeLong(mDataAniversario);
        }

        out.writeString(mSecretaria);

        out.writeString(mTelefone);

        out.writeString(mEmail);

        out.writeString(mCrm);

        out.writeByte( (byte) (mIdEspecialidade != null ? 1 : 0) );
        if (mIdEspecialidade != null) {
            out.writeInt(mIdEspecialidade);
        }
    }
}
