package de.adorsys.opba.protocol.xs2a.service.xs2a.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.opba.protocol.xs2a.service.storage.NeedsTransientStorage;
import de.adorsys.opba.protocol.xs2a.service.storage.TransientDataStorage;
import de.adorsys.xs2a.adapter.service.model.AuthenticationObject;
import de.adorsys.xs2a.adapter.service.model.StartScaProcessResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

// TODO - Make immutable, modify only with toBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aContext extends BaseContext implements NeedsTransientStorage {

    @JsonIgnore
    private TransientDataStorage transientStorage;

    // Mandatory static
    private String psuId;
    private String contentType = "application/json";

    // Mandatory dynamic
    private String psuIpAddress; // FIXME https://github.com/adorsys/open-banking-gateway/issues/251

    // In-process
    private String aspspScaApproach;
    private StartScaProcessResponse startScaProcessResponse;
    private String consentId;
    private String authorizationId;
    private String scaStatus;
    private List<ScaMethod> availableSca;
    private String userSelectScaId;
    private AuthenticationObject scaSelected;

    // TODO: protect from overriding using reflection https://github.com/adorsys/open-banking-gateway/issues/251
    private String redirectUriOk;

    // TODO: protect from overriding reflection https://github.com/adorsys/open-banking-gateway/issues/251
    private String redirectUriNok;

    private String fintechRedirectUriOk;
    private String fintechRedirectUriNok;

    private boolean redirectConsentOk;

    @JsonIgnore
    public String getPsuPassword() {
        TransientDataStorage.DataEntry entry = this.transientStorage.get(this);
        return null != entry ? this.transientStorage.get(this).getPsuPassword() : null;
    }

    @JsonIgnore
    public String getLastScaChallenge() {
        TransientDataStorage.DataEntry entry = this.transientStorage.get(this);
        return null != entry ? this.transientStorage.get(this).getScaChallengeResult() : null;
    }

    @JsonIgnore
    public void setPsuPassword(String psuPassword) {
        this.transientStorage.set(this, new TransientDataStorage.DataEntry(psuPassword, null));
    }

    @JsonIgnore
    public void setLastScaChallenge(String scaChallengeResult) {
        this.transientStorage.set(this, new TransientDataStorage.DataEntry(null, scaChallengeResult));
    }
}
