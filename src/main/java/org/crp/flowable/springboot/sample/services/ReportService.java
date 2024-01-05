package org.crp.flowable.springboot.sample.services;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface ReportService {

    /**
     * Create a report with:
     * | account | Amount sent |
     * |---------|-------------|
     * @return [ { "account": ___, "amount": ___}]
     */
    ArrayNode accountAmountReport();
}
