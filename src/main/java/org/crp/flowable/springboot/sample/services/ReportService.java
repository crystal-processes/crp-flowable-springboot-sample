package org.crp.flowable.springboot.sample.services;

import tools.jackson.databind.node.ArrayNode;

public interface ReportService {

    /**
     * Create a report with:
     * | account | Amount sent |
     * |---------|-------------|
     * @return [ { "account": ___, "amount": ___}]
     */
    ArrayNode accountAmountReport();
}
