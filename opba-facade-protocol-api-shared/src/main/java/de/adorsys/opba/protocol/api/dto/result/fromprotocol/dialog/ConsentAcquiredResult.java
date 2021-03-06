package de.adorsys.opba.protocol.api.dto.result.fromprotocol.dialog;

import java.net.URI;

public class ConsentAcquiredResult<T, C> extends RedirectionResult<T, C> {

    public ConsentAcquiredResult(URI redirectionTo, C cause) {
        super(redirectionTo, cause);
    }
}
