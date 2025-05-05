package org.crp.flowable.springboot.sample;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acme")
public class AcmeAppProperties {
    String imapInboundAdapterUrl;

    public String getImapInboundAdapterUrl() {
        return imapInboundAdapterUrl;
    }

    public void setImapInboundAdapterUrl(String imapInboundAdapterUrl) {
        this.imapInboundAdapterUrl = imapInboundAdapterUrl;
    }

}
